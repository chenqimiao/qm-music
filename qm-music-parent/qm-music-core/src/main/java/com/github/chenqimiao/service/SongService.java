package com.github.chenqimiao.service;

import com.github.chenqimiao.dto.AlbumAggDTO;
import com.github.chenqimiao.dto.SongDTO;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 15:24
 **/
public interface SongService {


    AlbumAggDTO queryByAlbumId(Integer albumId);


    List<SongDTO> searchByTitle(String songTitle, Integer pageSize, Integer offset);


    SongDTO queryBySongId(Integer songId);
}
