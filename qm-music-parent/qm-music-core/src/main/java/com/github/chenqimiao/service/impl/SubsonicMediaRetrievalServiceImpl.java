package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.service.MediaRetrievalService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 22:59
 **/
@Service("subsonicMediaRetrievalService")
public class SubsonicMediaRetrievalServiceImpl implements MediaRetrievalService {

    @Override
    public File getSongCoverArt(Integer songId, Integer size) {
       return null;
    }

    @Override
    public File getAlbumCoverArt(Integer albumId, Integer size) {

       return null;
    }

    @Override
    public File getArtistCoverArt(Integer artistId, Integer size) {
        return null;
    }
}
