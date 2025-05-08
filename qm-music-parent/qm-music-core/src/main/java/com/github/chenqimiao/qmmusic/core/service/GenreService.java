package com.github.chenqimiao.qmmusic.core.service;

import com.github.chenqimiao.qmmusic.core.dto.GenreStatisticsDTO;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 16:03
 **/
public interface GenreService {

    List<GenreStatisticsDTO> statistics();
}
