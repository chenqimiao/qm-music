package com.github.chenqimiao.service;

import com.github.chenqimiao.dto.GenreStatisticsDTO;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 16:03
 **/
public interface GenreService {

    List<GenreStatisticsDTO> statistics();
}
