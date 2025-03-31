package com.github.chenqimiao.service;

import com.github.chenqimiao.dto.CoverStreamDTO;
import com.github.chenqimiao.dto.SongStreamDTO;

import java.io.File;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 22:59
 **/
public interface MediaRetrievalService {

    File getSongCoverArt(Integer songId, Integer size);

    CoverStreamDTO getSongCoverStreamDTO(Integer songId, Integer size);

    File getAlbumCoverArt(Integer albumId, Integer size);

    File getArtistCoverArt(Integer artistId, Integer size);

    String getLyrics(String artistName, String songTitle);

    SongStreamDTO getSongStream(Integer songId, Integer maxBitRate, String format, Integer estimateContentLength);
}
