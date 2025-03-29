package com.github.chenqimiao.service;

import com.github.chenqimiao.dto.ArtistDTO;
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

}
