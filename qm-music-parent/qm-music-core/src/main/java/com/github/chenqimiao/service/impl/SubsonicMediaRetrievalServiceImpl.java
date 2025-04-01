package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.DO.ArtistDO;
import com.github.chenqimiao.DO.SongDO;
import com.github.chenqimiao.dto.CoverStreamDTO;
import com.github.chenqimiao.dto.SongStreamDTO;
import com.github.chenqimiao.io.local.MusicFileReader;
import com.github.chenqimiao.io.model.MusicAlbumMeta;
import com.github.chenqimiao.io.model.MusicMeta;
import com.github.chenqimiao.repository.ArtistRepository;
import com.github.chenqimiao.repository.SongRepository;
import com.github.chenqimiao.service.MediaRetrievalService;
import com.github.chenqimiao.util.FileUtils;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.tag.images.Artwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 22:59
 **/
@Service("subsonicMediaRetrievalService")
public class SubsonicMediaRetrievalServiceImpl implements MediaRetrievalService {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Override
    public CoverStreamDTO getSongCoverStreamDTO(Integer songId, Integer size) {
        List<Artwork> artworks = this.getSongArtworks(songId);

        if (CollectionUtils.isEmpty(artworks)) {
            return null;
        }

        Optional<Artwork> first = artworks.stream().filter(n -> {
            String description = n.getDescription();
            if (StringUtils.containsIgnoreCase(description, "Lyric")) {
                return true;
            }
            if (StringUtils.containsIgnoreCase(description, "Song Art")) {
                return true;
            }
            return false;
        }).findFirst();

        if (first.isPresent()) {
            return CoverStreamDTO.builder()
                    .cover(first.get().getBinaryData())
                    .mimeType(first.get().getMimeType())
                    .build();
        }

        return CoverStreamDTO.builder()
                .cover(artworks.getFirst().getBinaryData())
                .mimeType(artworks.getFirst().getMimeType())
                .build();
    }


    private List<Artwork> getSongArtworks(Integer songId) {
        SongDO songDO = songRepository.findBySongId(songId);
        String filePath = songDO.getFile_path();
        MusicMeta musicMeta = MusicFileReader.readMusicMeta(filePath);
        MusicAlbumMeta musicAlbumMeta = musicMeta.getMusicAlbumMeta();
        List<Artwork> artworks = musicAlbumMeta.getArtworks();
        if (artworks.isEmpty()) {
            return new ArrayList<>();
        }
        return artworks;
    }

    @Override
    public CoverStreamDTO getAlbumCoverStreamDTO(Integer albumId, Integer size) {
        List<SongDO> songs = songRepository.findByAlbumId(albumId);
        // fallback
        Artwork fallback = null;
        for (SongDO song : songs) {
            List<Artwork> artworks = this.getSongArtworks(song.getId());
            if (CollectionUtils.isEmpty(artworks)) {
                continue;
            }
            fallback = artworks.get(0);
            Optional<Artwork> first = artworks.stream().filter(art ->{
                if (art.getPictureType() == 3) {
                    return true;
                }
                if (StringUtils.containsIgnoreCase(art.getDescription(),"Cover")) {
                    return true;
                }
                if (StringUtils.containsIgnoreCase(art.getDescription(),"Album")) {
                    return true;
                }
                return false;
            }).findFirst();

            if (first.isPresent()) {
                return CoverStreamDTO.builder()
                        .cover(first.get().getBinaryData())
                        .mimeType(first.get().getMimeType())
                        .build();
            }
        }
        if (fallback != null) {
            return CoverStreamDTO.builder()
                    .cover(fallback.getBinaryData())
                    .mimeType(fallback.getMimeType())
                    .build();
        }
        return CoverStreamDTO.builder().build();
    }


    @Override
    public CoverStreamDTO getArtistCoverStreamDTO(Integer artistId, Integer size) {
        List<SongDO> songs = songRepository.findByArtistId(artistId);
        // fallback
        Artwork fallback = null;
        for (SongDO song : songs) {
            List<Artwork> artworks = this.getSongArtworks(song.getId());
            if (CollectionUtils.isEmpty(artworks)) {
                continue;
            }
            fallback = artworks.get(0);
            Optional<Artwork> first = artworks.stream().filter(art ->{
                if (art.getPictureType() == 8) {
                    return true;
                }
                if (StringUtils.containsIgnoreCase(art.getDescription(),"Artist")) {
                    return true;
                }
                if (StringUtils.containsIgnoreCase(art.getDescription(),"Portrait")) {
                    return true;
                }
                return false;
            }).findFirst();

            if (first.isPresent()) {
                return CoverStreamDTO.builder()
                        .cover(first.get().getBinaryData())
                        .mimeType(first.get().getMimeType())
                        .build();
            }
        }
        if (fallback != null) {
            return CoverStreamDTO.builder()
                    .cover(fallback.getBinaryData())
                    .mimeType(fallback.getMimeType())
                    .build();
        }
        return CoverStreamDTO.builder().build();
    }

    @Override
    @SneakyThrows
    public String getLyrics(String artistName, String songTitle) {
        SongDO song = null;
        if (StringUtils.isNotBlank(artistName)) {
            song = songRepository.findByTitleAndArtistName(songTitle, artistName);
        }else {
            song = songRepository.findByTitle(songTitle);

        }
        if (song == null) {
            return null;
        }
        String filePath = song.getFile_path();
        MusicMeta musicMeta = MusicFileReader.readMusicMeta(filePath);
        String lyrics = musicMeta.getLyrics();
        if (StringUtils.isNotBlank(lyrics)) {
            return lyrics;
        }

        String lrcFile = FileUtils.replaceFileExtension(filePath, ".lrc");
        Path path = Paths.get(lrcFile);
        if (Files.exists(path)) {
            lyrics = Files.readString(Path.of(lrcFile));
            return lyrics;
        }

        return "";
    }

    @Override
    @SneakyThrows
    public SongStreamDTO getSongStream(Integer songId, Integer maxBitRate, String format, Integer estimateContentLength) {
        SongDO songDO = songRepository.findBySongId(songId);
        String filePath = songDO.getFile_path();
        return SongStreamDTO.builder()
                .songStream(new FileInputStream(filePath))
                .filePath(filePath)
                .size(Files.size(Paths.get(filePath))).build();

    }
}
