package com.github.chenqimiao.service;

import com.github.chenqimiao.dto.ArtistAggDTO;
import com.github.chenqimiao.dto.ArtistDTO;
import com.github.chenqimiao.enums.EnumArtistRelationType;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 15:32
 **/
public interface ArtistService {

    List<ArtistDTO> searchArtist(@Nullable Long ifModifiedSince);


    Map<String, List<ArtistDTO>> searchArtistMap(@Nullable Long ifModifiedSince);



    ArtistAggDTO queryArtistWithAlbums(Long artistId);

    List<ArtistDTO> searchByName(String artistName, Integer pageSize, Integer offset);


    Map<String, List<ArtistDTO>> queryAllArtistGroupByFirstLetter(Long musicFolderId, EnumArtistRelationType enumArtistRelationType);

    List<ArtistDTO> batchQueryArtistByArtistIds(List<Long> artistIds);


    List<ArtistDTO> searchByNames(List<String> artistNames);


    ArtistDTO queryArtistByArtistId(Long artistId);



}
