package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.DO.AlbumDO;
import com.github.chenqimiao.DO.ArtistDO;
import com.github.chenqimiao.DO.SongDO;
import com.github.chenqimiao.io.local.MusicFileReader;
import com.github.chenqimiao.io.model.MusicAlbumMeta;
import com.github.chenqimiao.io.model.MusicMeta;
import com.github.chenqimiao.repository.SongRepository;
import com.github.chenqimiao.service.MediaFetcherService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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


    @Override
    @SneakyThrows
    public void fetchMusic(String rootPath) {

        List<SongDO> songs = songRepository.findAll();

        List<Integer> toBeRemoveSongIds = songs.stream().filter(song -> {
            String filePath = song.getFile_path();
            Path path = Paths.get(filePath);
            return !Files.exists(path);
        }).map(SongDO::getId).collect(Collectors.toList());

        songRepository.deleteByIds(toBeRemoveSongIds);

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
                        this.save(musicMeta);
                    });
        } catch (IOException e) {
            log.error("文件遍历io异常", e);
            e.printStackTrace();
        } catch (UncheckedIOException e) { // 处理并行流中可能抛出的未检查异常
            log.error("文件访问错误 ", e);
        }

    }

    private void save(MusicMeta musicMeta) {
        MusicAlbumMeta musicAlbumMeta = musicMeta.getMusicAlbumMeta();
        String albumArtist = musicAlbumMeta.getAlbumArtist();

//        ArtistDO albumDO = new ArtistDO();
//        albumDO.setName();
//        albumDO.set
        SongDO songDO = new SongDO();
        // TODO ..

    }


}
