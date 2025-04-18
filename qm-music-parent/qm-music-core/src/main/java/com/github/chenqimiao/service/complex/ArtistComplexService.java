package com.github.chenqimiao.service.complex;

import com.github.chenqimiao.dto.ComplexArtistDTO;
import jakarta.annotation.Nullable;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/3 23:57
 **/
public interface ArtistComplexService {


    List<ComplexArtistDTO> queryByArtistIds(List<Long> artistIds, @Nullable Long userId);

    void organizeArtists();
}
