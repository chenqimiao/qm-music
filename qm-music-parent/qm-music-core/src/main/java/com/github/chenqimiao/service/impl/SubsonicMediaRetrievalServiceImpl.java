package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.DO.ArtistDO;
import com.github.chenqimiao.DO.SongDO;
import com.github.chenqimiao.dto.SongStreamDTO;
import com.github.chenqimiao.io.local.MusicFileReader;
import com.github.chenqimiao.io.model.MusicAlbumMeta;
import com.github.chenqimiao.io.model.MusicMeta;
import com.github.chenqimiao.repository.ArtistRepository;
import com.github.chenqimiao.repository.SongRepository;
import com.github.chenqimiao.service.MediaRetrievalService;
import lombok.SneakyThrows;
import org.jaudiotagger.tag.images.Artwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
    public File getSongCoverArt(Integer songId, Integer size) {
        SongDO songDO = songRepository.findBySongId(songId);
        String filePath = songDO.getFile_path();
        MusicMeta musicMeta = MusicFileReader.readMusicMeta(filePath);
        MusicAlbumMeta musicAlbumMeta = musicMeta.getMusicAlbumMeta();
        List<Artwork> artworks = musicAlbumMeta.getArtworks();
        // todo
        return null;
    }

    @Override
    public byte[] getSongCoverArtByte(Integer songId, Integer size) {
        SongDO songDO = songRepository.findBySongId(songId);
        String filePath = songDO.getFile_path();
        MusicMeta musicMeta = MusicFileReader.readMusicMeta(filePath);
        MusicAlbumMeta musicAlbumMeta = musicMeta.getMusicAlbumMeta();
        List<Artwork> artworks = musicAlbumMeta.getArtworks();
        if (artworks.isEmpty()) {
            return null;
        }
        return artworks.getFirst().getBinaryData();
    }

    @Override
    public File getAlbumCoverArt(Integer albumId, Integer size) {
       return null;
    }

    @Override
    public File getArtistCoverArt(Integer artistId, Integer size) {
        return null;
    }

    @Override
    public String getLyrics(String artistName, String songTitle) {
        SongDO song = songRepository.findByTitleAndArtistName(songTitle, artistName);
        if (song == null) {
            return null;
        }
        String filePath = song.getFile_path();
        MusicMeta musicMeta = MusicFileReader.readMusicMeta(filePath);
        return musicMeta.getLyrics();
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
