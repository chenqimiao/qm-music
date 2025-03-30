package com.github.chenqimiao.service;

import com.github.chenqimiao.dto.AlbumAggDTO;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 15:24
 **/
public interface SongService {


    AlbumAggDTO queryByAlbumId(Integer albumId);
}
