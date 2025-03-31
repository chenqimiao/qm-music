package com.github.chenqimiao.service;

import com.github.chenqimiao.dto.SongStreamDTO;
import org.modelmapper.internal.asm.tree.IincInsnNode;

import java.io.File;
import java.io.InputStream;

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

    SongStreamDTO getSongStream(Integer songId, Integer maxBitRate, String format, Integer estimateContentLength);
}
