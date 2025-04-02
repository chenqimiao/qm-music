package com.github.chenqimiao.service.complex;

import com.github.chenqimiao.dto.CoverStreamDTO;
import com.github.chenqimiao.dto.SongStreamDTO;


/**
 * @author Qimiao Chen
 * @since 2025/3/30 22:59
 **/
public interface MediaRetrievalService {


    CoverStreamDTO getSongCoverStreamDTO(Integer songId, Integer size);

    CoverStreamDTO getAlbumCoverStreamDTO(Integer albumId, Integer size);

    CoverStreamDTO getArtistCoverStreamDTO(Integer artistId, Integer size);

    String getLyrics(String artistName, String songTitle);

    SongStreamDTO getSongStream(Integer songId, Integer maxBitRate, String format, Integer estimateContentLength);
}
