package com.github.chenqimiao.service.complex;

import com.github.chenqimiao.dto.AlbumDTO;
import com.github.chenqimiao.request.AlbumSearchRequest;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/7 16:35
 **/
public interface AlbumComplexService {


    void organizeAlbums();


    List<AlbumDTO> getAlbumList2(AlbumSearchRequest albumSearchRequest);

    List<AlbumDTO> searchAlbumByArtist(Long artistId);
}
