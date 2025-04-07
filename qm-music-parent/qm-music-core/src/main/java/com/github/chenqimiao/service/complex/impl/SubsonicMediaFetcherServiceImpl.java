package com.github.chenqimiao.service.complex.impl;

import com.github.chenqimiao.DO.AlbumDO;
import com.github.chenqimiao.DO.ArtistDO;
import com.github.chenqimiao.DO.ArtistRelationDO;
import com.github.chenqimiao.DO.SongDO;
import com.github.chenqimiao.enums.EnumArtistRelationType;
import com.github.chenqimiao.io.local.AudioContentTypeDetector;
import com.github.chenqimiao.io.local.MusicFileReader;
import com.github.chenqimiao.io.local.model.MusicAlbumMeta;
import com.github.chenqimiao.io.local.model.MusicMeta;
import com.github.chenqimiao.repository.*;
import com.github.chenqimiao.service.complex.AlbumComplexService;
import com.github.chenqimiao.service.complex.MediaFetcherService;
import com.github.chenqimiao.service.complex.SongComplexService;
import com.github.chenqimiao.util.FileUtils;
import com.github.chenqimiao.util.FirstLetterUtil;
import com.github.chenqimiao.util.MD5Utils;
import io.github.mocreates.Sequence;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
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

    @Autowired
    private Sequence sequence;

    @Autowired
    private ArtistRelationRepository artistRelationRepository;

    @Autowired
    private UserStarRepository userStarRepository;

    @Autowired
    private PlaylistItemRepository playlistItemRepository;

    @Autowired
    private SongComplexService songComplexService;

    @Autowired
    private AlbumComplexService albumComplexService;


    @Override
    @SneakyThrows
    public void fetchMusic(String rootPath) {
        AtomicInteger songCount = new AtomicInteger(0);
        StopWatch stopWatch = new StopWatch("fetchMusic");
        stopWatch.start();
        List<SongDO> songs = songRepository.findAll();

        List<Long> cleanedSongIds = this.cleanOutOfDateSongs(songs);

        Set<String> standBySongSet = songs.stream().filter(song -> !cleanedSongIds.contains(song.getId()))
                .map(SongDO::getFile_path).collect(Collectors.toSet());

        this.traverseVideoFiles(rootPath, standBySongSet, songCount);

        this.organizeAlbums();
        stopWatch.stop();
        log.info("consumes time: {} ms, scan count of song : {}", stopWatch.getTime(TimeUnit.MILLISECONDS), songCount);
    }

    private void organizeAlbums() {
        albumComplexService.organizeAlbums();
    }

    private void traverseVideoFiles(String rootPath, Set<String> standBySongSet, AtomicInteger songCount) {
        Path root = Paths.get(rootPath); // 根目录

        try (var stream = Files.walk(root)) {
            stream.parallel(). // 启用并行流加速
                filter(Files::isRegularFile) // 只保留普通文件
                    .filter(FileUtils::isVideo) //仅扫描video
                    .forEach(path -> {
                        try{
                            String filePath = path.toAbsolutePath().normalize().toString();
                            if (standBySongSet.contains(filePath)) {
                                return;
                            }
                            MusicMeta musicMeta = MusicFileReader.readMusicMeta(path.toFile());
                            if (musicMeta == null) {
                                return;
                            }
                            this.save(musicMeta, path);
                            songCount.incrementAndGet();
                        }catch (Exception e) {
                            log.error("traverse file exception: {} ", path, e);
                        }

                    });
        } catch (IOException e) {
            log.error("io exception", e);
        } catch (UncheckedIOException e) { // 处理并行流中可能抛出的未检查异常
            log.error("unknown io exception", e);
        } catch (Exception e) {
            log.error("unknown  exception", e);
        }
    }

    private List<Long> cleanOutOfDateSongs(List<SongDO> songs) {
        List<Long> toBeRemoveSongIds = songs.stream().filter(song -> {
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
            songComplexService.cleanSongs(toBeRemoveSongIds);
        }
        return toBeRemoveSongIds;
    }

    @SneakyThrows
    private void save(MusicMeta musicMeta, Path path) {
        String delimiterRegx = "(and|&|,|、)+";
        MusicAlbumMeta musicAlbumMeta = musicMeta.getMusicAlbumMeta();

        List<ArtistDO> songArtists = new ArrayList<>();
        List<ArtistDO> albumArtists = new ArrayList<>();
        if (StringUtils.isNotBlank(musicMeta.getArtist())) {
            songArtists = Arrays.stream(musicMeta.getArtist().split(delimiterRegx))
                    .map(String::trim).distinct()
                    .map(n -> {
                        ArtistDO artistDO  = new ArtistDO();
                        artistDO.setId(sequence.nextId());
                        artistDO.setName(n);
                        artistDO.setFirst_letter(FirstLetterUtil.getFirstLetter(n));
                        return artistDO;
                    }).toList();

            songArtists = artistRepository.saveAndReturn(songArtists);
        }

        if (StringUtils.isNotBlank(musicAlbumMeta.getAlbumArtist())) {
            albumArtists = Arrays.stream(musicAlbumMeta.getAlbumArtist().split(delimiterRegx))
                    .map(String::trim).distinct()
                    .map(n -> {
                        ArtistDO artistDO  = new ArtistDO();
                        artistDO.setId(sequence.nextId());
                        artistDO.setName(n);
                        artistDO.setFirst_letter(FirstLetterUtil.getFirstLetter(n));
                        return artistDO;
                    }).toList();

            albumArtists = artistRepository.saveAndReturn(albumArtists);

        }


        AlbumDO albumDO = null;
        if (StringUtils.isNotBlank(musicAlbumMeta.getAlbum())) {
             albumDO = albumRepository.queryByUniqueKey(musicAlbumMeta.getAlbum());

            if (albumDO == null) {
                albumDO = new AlbumDO();
                albumDO.setId(sequence.nextId());
                albumDO.setTitle(musicAlbumMeta.getAlbum());
                albumDO.setArtist_id(CollectionUtils.isNotEmpty(albumArtists)? albumArtists.getFirst().getId(): null);
                String releaseYear = StringUtils.isNotBlank(musicAlbumMeta.getYear()) ? musicAlbumMeta.getYear()
                        : musicAlbumMeta.getOriginalYear();
                albumDO.setRelease_year(MusicFileReader.beautifyReleaseYear(releaseYear));
                albumDO.setGenre(StringUtils.isNotBlank(musicAlbumMeta.getGenre()) ? musicAlbumMeta.getGenre() : musicMeta.getGenre());
                albumDO.setSong_count(0);
                albumDO.setDuration(1234);
                albumDO.setArtist_name(CollectionUtils.isNotEmpty(albumArtists)? albumArtists.getFirst().getName(): null);
                albumDO = albumRepository.saveAndReturn(albumDO);
            }
        }


        SongDO songDO = new SongDO();
        long songId = sequence.nextId();
        songDO.setId(songId);
        songDO.setParent(1L);
        songDO.setTitle(musicMeta.getTitle());
        songDO.setAlbum_id(Optional.ofNullable(albumDO).map(AlbumDO::getId).orElse(null));
        songDO.setAlbum_title(Optional.ofNullable(albumDO).map(AlbumDO::getTitle).orElse(null));
        songDO.setArtist_id(CollectionUtils.isNotEmpty(songArtists) ? songArtists.getFirst().getId() : null);
        songDO.setArtist_name(CollectionUtils.isNotEmpty(songArtists) ? songArtists.getFirst().getName() : null);
        songDO.setDuration(musicMeta.getTrackLength());
        songDO.setSuffix(FileUtils.getFileExtension(path));
        songDO.setContent_type(AudioContentTypeDetector.mapFormatToMimeType(musicMeta.getFormat()));
        songDO.setFile_path(path.toAbsolutePath().normalize().toString());
        songDO.setFile_hash(MD5Utils.calculateMD5(path));
        songDO.setSize(Files.size(path));
        String releaseYear = StringUtils.isNotBlank(musicAlbumMeta.getYear()) ? musicAlbumMeta.getYear()
                : musicAlbumMeta.getOriginalYear();
        songDO.setYear(MusicFileReader.beautifyReleaseYear(releaseYear));
        int bitRate = NumberUtils.toInt(musicMeta.getBitRate(), NumberUtils.INTEGER_ZERO);
        songDO.setBit_rate(bitRate == 0 ? null : bitRate);
        songDO.setGenre(musicMeta.getGenre());

        songDO.setFile_last_modified(FileUtils.getLastModified(path));
        songDO.setTrack(StringUtils.isBlank(musicMeta.getTrack()) ? "1" : musicMeta.getTrack() );
        songRepository.save(songDO);

        // save relation


        List<ArtistRelationDO> songRelationList = songArtists.stream().map(n -> {
            ArtistRelationDO artistRelationDO = new ArtistRelationDO();
            artistRelationDO.setType(EnumArtistRelationType.SONG.getCode());
            artistRelationDO.setArtist_id(n.getId());
            artistRelationDO.setRelation_id(songId);
            return artistRelationDO;
        }).toList();

        List<ArtistRelationDO> artistRelationList = new ArrayList<>(songRelationList);

        if (albumDO != null) {
            Long albumId = albumDO.getId();
            List<ArtistRelationDO> albumRelationList = albumArtists.stream().map(n -> {
                ArtistRelationDO artistRelationDO = new ArtistRelationDO();
                artistRelationDO.setType(EnumArtistRelationType.ALBUM.getCode());
                artistRelationDO.setArtist_id(n.getId());
                artistRelationDO.setRelation_id(albumId);
                return artistRelationDO;
            }).toList();
            artistRelationList.addAll(albumRelationList);

        }

        artistRelationRepository.save(artistRelationList);

    }

}
