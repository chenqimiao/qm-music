package com.github.chenqimiao.qmmusic.core.service;

import com.github.chenqimiao.qmmusic.core.dto.PlayHistoryDTO;
import com.github.chenqimiao.qmmusic.core.request.PlayHistoryRequest;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/12 10:32
 **/
public interface PlayHistoryService {

    void save(PlayHistoryRequest playHistoryRequest);

    List<PlayHistoryDTO> queryRecentPlayHistoryList(Long userId, Integer offset, Integer size);

    List<PlayHistoryDTO> queryFrequentPlayHistoryList(Long userId, Integer offset, Integer size);

    void cleanPlayHistory(Integer month);

    List<PlayHistoryDTO> queryUserSpecifiedSongPlayHistoryList(Long userId, List<Long> songIds);
}
