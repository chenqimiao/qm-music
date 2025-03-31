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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.io.IOException;
import java.nio.file.*;
import java.io.UncheckedIOException;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/3/31 14:45
 **/
@Service("mediaFetcherService")
@Slf4j
public class MediaFetcherServiceImpl implements MediaFetcherService {

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

        List<SongDO> songs = songRepository.findAll();

        List<Integer> toBeRemoveSongIds = songs.stream().filter(song -> {
            String filePath = song.getFile_path();
            Path path = Paths.get(filePath);
            return !Files.exists(path);
        }).map(SongDO::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(toBeRemoveSongIds)) {
            songRepository.deleteByIds(toBeRemoveSongIds);
        }

        Set<String> songSet = songs.stream().filter(song -> !toBeRemoveSongIds.contains(song.getId()))
                .map(n -> n.getFile_path()).collect(Collectors.toSet());

//        Map<String, Integer> songMap= songs.stream().collect(Collectors.toMap(SongDO::getFile_path, SongDO::getId));

        Path root = Paths.get(rootPath); // 根目录

        try (var stream = Files.walk(root)) {
            stream.parallel() // 启用并行流加速
                    .filter(Files::isRegularFile) // 只保留普通文件
                    .forEach(path -> {
                        String filePath = path.toAbsolutePath().toString();
                        if (songSet.contains(filePath)) {
                            return;
                        }

                        MusicMeta musicMeta = MusicFileReader.readMusicMeta(path.toFile());
                        this.save(musicMeta, path);
                    });
        } catch (IOException e) {
            log.error("文件遍历io异常", e);
            e.printStackTrace();
        } catch (UncheckedIOException e) { // 处理并行流中可能抛出的未检查异常
            log.error("文件访问错误 ", e);
        }

    }

    @SneakyThrows
    private void save(MusicMeta musicMeta, Path path) {
        MusicAlbumMeta musicAlbumMeta = musicMeta.getMusicAlbumMeta();

        ArtistDO artistDO = artistRepository.queryByName(musicAlbumMeta.getAlbumArtist());
        if (artistDO == null){
            artistDO = new ArtistDO();
            artistDO.setName(musicAlbumMeta.getAlbumArtist());
            artistDO.setFirst_letter(FirstLetterUtil.getFirstLetter(musicAlbumMeta.getAlbumArtist()));
            artistRepository.save(artistDO);
            artistDO = artistRepository.queryByName(musicAlbumMeta.getAlbumArtist());
        }

        AlbumDO albumDO = albumRepository.queryByName(musicAlbumMeta.getAlbum());

        if (albumDO ==null) {
            albumDO = new AlbumDO();
            albumDO.setTitle(musicAlbumMeta.getAlbum());
            albumDO.setArtist_id(artistDO.getId());
            albumDO.setRelease_year(musicAlbumMeta.getOriginalYear());
            albumDO.setGenre(musicAlbumMeta.getMusicbrainzReleaseType());
            albumDO.setSong_count(0);
            albumDO.setDuration(1234);
            albumDO.setArtist_name(artistDO.getName());
            albumRepository.save(albumDO);
            albumDO = albumRepository.queryByName(musicAlbumMeta.getAlbum());
        }

        SongDO songDO = new SongDO();
        songDO.setParent(1);
        songDO.setTitle(musicMeta.getTitle());
        songDO.setAlbum_id(albumDO.getId());
        songDO.setArtist_id(artistDO.getId());
        songDO.setDuration(musicMeta.getTrackLength());
        songDO.setSuffix(FileUtils.getFileExtension(path));
        songDO.setContent_type(AudioContentTypeDetector.mapFormatToMimeType(musicMeta.getFormat()));
        songDO.setFile_path(path.toAbsolutePath().toString());
        songDO.setFile_hash(FileUtils.getFileExtension(path));
        songDO.setSize(Files.size(path));
        songDO.setYear(musicAlbumMeta.getOriginalYear());
        songDO.setBit_rate(Integer.valueOf(musicMeta.getBitRate()));
        songDO.setArtist_name(artistDO.getName());
        songRepository.save(songDO);

    }


}
