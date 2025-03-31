package com.github.chenqimiao.service;

import java.io.File;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 22:59
 **/
public interface MediaRetrievalService {

    File getSongCoverArt(Integer songId, Integer size);

    byte[] getSongCoverArtByte(Integer songId, Integer size);

    File getAlbumCoverArt(Integer albumId, Integer size);

    File getArtistCoverArt(Integer artistId, Integer size);

    String getLyrics(String artistName, String songTitle);
}
