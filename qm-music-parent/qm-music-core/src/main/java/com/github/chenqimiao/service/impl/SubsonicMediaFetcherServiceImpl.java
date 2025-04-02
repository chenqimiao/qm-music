package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.DO.AlbumDO;
import com.github.chenqimiao.DO.ArtistDO;
import com.github.chenqimiao.DO.SongDO;
import com.github.chenqimiao.io.local.AudioContentTypeDetector;
import com.github.chenqimiao.io.local.MusicFileReader;
import com.github.chenqimiao.io.model.MusicAlbumMeta;
import com.github.chenqimiao.io.model.MusicMeta;
import com.github.chenqimiao.repository.AlbumRepository;
import com.github.chenqimiao.repository.ArtistRepository;
import com.github.chenqimiao.repository.SongRepository;
import com.github.chenqimiao.repository.UserRepository;
import com.github.chenqimiao.service.MediaFetcherService;
import com.github.chenqimiao.util.FileUtils;
import com.github.chenqimiao.util.FirstLetterUtil;
import com.github.chenqimiao.util.MD5Utils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.io.IOException;
import java.nio.file.*;
import java.io.UncheckedIOException;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/3/31 14:45
 **/
@Service("subsonicMediaFetcherService")
@Slf4j
public class SubsonicMediaFetcherServiceImpl implements MediaFetcherService {

    private static final ExecutorService VIRTUAL_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    // 线程安全的计数器
    private static final AtomicLong fileCount = new AtomicLong();
    private static final AtomicLong dirCount = new AtomicLong();

    @Autowired
    private SongRepository songRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository;


    @Override
    @SneakyThrows
    public void fetchMusic(String rootPath) {
        AtomicInteger songCount = new AtomicInteger(0);
        StopWatch stopWatch = new StopWatch("fetchMusic");
        stopWatch.start();
        List<SongDO> songs = songRepository.findAll();

        List<Integer> toBeRemoveSongIds = songs.stream().filter(song -> {
            String filePath = song.getFile_path();
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return true;
            }
            long lastModifiedMillis = FileUtils.getLastModified(path);
            // changed ?
            if (Math.abs(lastModifiedMillis - song.getFile_last_modified()) > 1000L) {
                return true;
            }
            return false;
        }).map(SongDO::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(toBeRemoveSongIds)) {
            songRepository.deleteByIds(toBeRemoveSongIds);
        }

        Set<String> songSet = songs.stream().filter(song -> !toBeRemoveSongIds.contains(song.getId()))
                .map(n -> n.getFile_path()).collect(Collectors.toSet());

//        Map<String, Integer> songMap= songs.stream().collect(Collectors.toMap(SongDO::getFile_path, SongDO::getId));

        Path root = Paths.get(rootPath); // 根目录

        try (var stream = Files.walk(root)) {
            stream.parallel(). // 启用并行流加速
                filter(Files::isRegularFile) // 只保留普通文件
                    .forEach(path -> {
                        String filePath = path.toAbsolutePath().normalize().toString();
                        if (songSet.contains(filePath)) {
                            return;
                        }
                        MusicMeta musicMeta = MusicFileReader.readMusicMeta(path.toFile());
                        if (musicMeta == null) {
                            return;
                        }
                        this.save(musicMeta, path);
                        songCount.incrementAndGet();
                    });
        } catch (IOException e) {
            log.error("文件遍历io异常", e);
        } catch (UncheckedIOException e) { // 处理并行流中可能抛出的未检查异常
            log.error("文件访问错误 ", e);
        }

        stopWatch.stop();
        log.info("consumes time: {} ms, scan count of song : {}", stopWatch.getTime(TimeUnit.MILLISECONDS), songCount);
    }

    @SneakyThrows
    private void save(MusicMeta musicMeta, Path path) {
        MusicAlbumMeta musicAlbumMeta = musicMeta.getMusicAlbumMeta();

        ArtistDO songArtist = null;
        ArtistDO albumArtist = null;
        if (StringUtils.isNotBlank(musicMeta.getArtist())) {
            ArtistDO artistDO = artistRepository.queryByName(musicMeta.getArtist());
            if (artistDO == null){
                artistDO = new ArtistDO();
                artistDO.setName(musicMeta.getArtist());
                artistDO.setFirst_letter(FirstLetterUtil.getFirstLetter(musicMeta.getArtist()));
                artistRepository.save(artistDO);
                artistDO = artistRepository.queryByName(musicMeta.getArtist());
            }
            songArtist = artistDO;
            albumArtist = artistDO;
        }

        if (StringUtils.isNotBlank(musicAlbumMeta.getAlbumArtist())) {
            ArtistDO albumArtistDO = artistRepository.queryByName(musicAlbumMeta.getAlbumArtist());
            if (albumArtistDO == null){
                albumArtistDO = new ArtistDO();
                albumArtistDO.setName(musicAlbumMeta.getAlbumArtist());
                albumArtistDO.setFirst_letter(FirstLetterUtil.getFirstLetter(musicAlbumMeta.getAlbumArtist()));
                artistRepository.save(albumArtistDO);
                albumArtistDO = artistRepository.queryByName(musicAlbumMeta.getAlbumArtist());
                if (songArtist == null) songArtist = albumArtistDO;
                if (albumArtistDO != null) albumArtist = albumArtistDO;
            }
        }


        AlbumDO albumDO = null;
        if (StringUtils.isNotBlank(musicAlbumMeta.getAlbum())) {
             albumDO = albumRepository.queryByName(musicAlbumMeta.getAlbum());

            if (albumDO ==null) {
                albumDO = new AlbumDO();
                albumDO.setTitle(musicAlbumMeta.getAlbum());
                albumDO.setArtist_id(albumArtist != null ? albumArtist.getId() : null);
                albumDO.setRelease_year(StringUtils.isNotBlank(musicAlbumMeta.getYear()) ? musicAlbumMeta.getYear()
                        : musicAlbumMeta.getOriginalYear());
                albumDO.setGenre(StringUtils.isNotBlank(musicAlbumMeta.getGenre()) ? musicAlbumMeta.getGenre() : musicMeta.getGenre());
                albumDO.setSong_count(0);
                albumDO.setDuration(1234);
                albumDO.setArtist_name(albumArtist != null ? albumArtist.getName() : null);
                albumRepository.save(albumDO);
                albumDO = albumRepository.queryByName(musicAlbumMeta.getAlbum());
            }
        }


        SongDO songDO = new SongDO();
        songDO.setParent(1);
        songDO.setTitle(musicMeta.getTitle());
        songDO.setAlbum_id(Optional.ofNullable(albumDO).map(AlbumDO::getId).orElse(null));
        songDO.setArtist_id(Optional.ofNullable(songArtist).map(ArtistDO::getId).orElse(null));
        songDO.setDuration(musicMeta.getTrackLength());
        songDO.setSuffix(FileUtils.getFileExtension(path));
        songDO.setContent_type(AudioContentTypeDetector.mapFormatToMimeType(musicMeta.getFormat()));
        songDO.setFile_path(path.toAbsolutePath().normalize().toString());
        songDO.setFile_hash(MD5Utils.calculateMD5(path));
        songDO.setSize(Files.size(path));
        songDO.setYear(StringUtils.isNotBlank(musicAlbumMeta.getYear()) ? musicAlbumMeta.getYear()
                : musicAlbumMeta.getOriginalYear());
        songDO.setBit_rate(Integer.valueOf(musicMeta.getBitRate()));
        songDO.setArtist_name(songArtist != null ? songArtist.getName() : null);
        songDO.setGenre(musicMeta.getGenre());

        songDO.setFile_last_modified(FileUtils.getLastModified(path));
        songDO.setTrack(musicMeta.getTrack());
        songRepository.save(songDO);

    }


}
