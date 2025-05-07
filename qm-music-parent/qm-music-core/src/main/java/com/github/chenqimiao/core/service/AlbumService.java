package com.github.chenqimiao.core.service;

import com.github.chenqimiao.core.dto.AlbumDTO;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 19:37
 **/
public interface AlbumService {

    List<AlbumDTO> searchByName(String albumName, Integer pageSize, Integer offset) ;

    List<AlbumDTO> batchQueryAlbumByAlbumIds(List<Long> albumIds) ;

    AlbumDTO queryAlbumByAlbumId (Long albumId) ;
}
