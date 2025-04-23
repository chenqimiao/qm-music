package com.github.chenqimiao.service;

import com.github.chenqimiao.dto.PlayHistoryDTO;
import com.github.chenqimiao.request.PlayHistoryRequest;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/12 10:32
 **/
public interface PlayHistoryService {

    void save(PlayHistoryRequest playHistoryRequest);

    List<PlayHistoryDTO> queryRecentPlayHistoryList(Long userId, Integer offset, Integer size);

    List<PlayHistoryDTO> queryFrequentPlayHistoryList(Long userId, Integer offset, Integer size);

    void cleanPlayHistory();

    List<PlayHistoryDTO> queryUserSpecifiedSongPlayHistoryList(Long userId, List<Long> songIds);
}
