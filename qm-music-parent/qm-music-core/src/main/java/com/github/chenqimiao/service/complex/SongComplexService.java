package com.github.chenqimiao.service.complex;

import com.github.chenqimiao.dto.ComplexSongDTO;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 23:37
 **/
public interface SongComplexService {

    List<ComplexSongDTO> queryBySongIds(List<Integer> songIds, Integer userId);

}
