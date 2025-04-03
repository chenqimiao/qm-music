package com.github.chenqimiao.service;

import com.github.chenqimiao.dto.AlbumDTO;
import com.github.chenqimiao.request.AlbumSearchRequest;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 19:37
 **/
public interface AlbumService {

    List<AlbumDTO> getAlbumList2(AlbumSearchRequest albumSearchRequest);


    List<AlbumDTO> searchByName(String albumName, Integer pageSize, Integer offset) ;

    List<AlbumDTO> batchQueryAlbumByAlbumIds(List<Integer> albumIds) ;
}
