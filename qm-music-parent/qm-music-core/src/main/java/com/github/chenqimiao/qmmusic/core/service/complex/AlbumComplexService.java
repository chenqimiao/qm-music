package com.github.chenqimiao.qmmusic.core.service.complex;

import com.github.chenqimiao.qmmusic.core.dto.AlbumDTO;
import com.github.chenqimiao.qmmusic.core.dto.ComplexAlbumDTO;
import com.github.chenqimiao.qmmusic.core.request.AlbumSearchRequest;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/7 16:35
 **/
public interface AlbumComplexService {


    void organizeAlbums();


    List<AlbumDTO> getAlbumList2(AlbumSearchRequest albumSearchRequest);

    List<AlbumDTO> searchAlbumByArtist(Long artistId);

    List<ComplexAlbumDTO> getComplexAlbumList2(AlbumSearchRequest albumSearchRequest);

}
