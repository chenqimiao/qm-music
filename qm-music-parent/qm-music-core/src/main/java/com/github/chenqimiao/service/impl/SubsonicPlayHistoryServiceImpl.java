package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.constant.ModelMapperTypeConstants;
import com.github.chenqimiao.dto.PlayHistoryDTO;
import com.github.chenqimiao.repository.PlayHistoryRepository;
import com.github.chenqimiao.request.PlayHistoryRequest;
import com.github.chenqimiao.request.PlayHistorySaveRequest;
import com.github.chenqimiao.service.PlayHistoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/4/12 10:33
 **/
@Slf4j
@Service("subsonicPlayHistoryService")
public class SubsonicPlayHistoryServiceImpl implements PlayHistoryService {

    @Autowired
    private PlayHistoryRepository playHistoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Resource
    private ModelMapper ucModelMapper;

    @Override
    public void save(PlayHistoryRequest playHistoryRequest) {
        PlayHistorySaveRequest playHistorySaveRequest
                = modelMapper.map(playHistoryRequest, PlayHistorySaveRequest.class);
        playHistoryRepository.save(playHistorySaveRequest);

    }

    @Override
    public List<PlayHistoryDTO> queryRecentPlayHistoryList(Long userId, Integer offset, Integer size) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("offset", offset);
        params.put("size", size);
        params.put("orderBy", "gmt_modify desc");
        return ucModelMapper.map(playHistoryRepository.queryByCondition(params)
                , ModelMapperTypeConstants.TYPE_LIST_PLAY_HISTORY_DTO);
    }

    @Override
    public List<PlayHistoryDTO> queryFrequentPlayHistoryList(Long userId, Integer offset, Integer size) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("offset", offset);
        params.put("size", size);
        params.put("orderBy", "play_count desc");
        return ucModelMapper.map(playHistoryRepository.queryByCondition(params)
                , ModelMapperTypeConstants.TYPE_LIST_PLAY_HISTORY_DTO);
    }

    @Override
    public void cleanPlayHistory() {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        long sixMonthsTimestamp = sixMonthsAgo.atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
         playHistoryRepository.delGmtModifyLessThan(sixMonthsTimestamp);
    }

    @Override
    public List<PlayHistoryDTO> queryUserSpecifiedSongPlayHistoryList(Long userId, List<Long> songIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("songIds", songIds);
        return ucModelMapper.map(playHistoryRepository.queryByCondition(params)
                , ModelMapperTypeConstants.TYPE_LIST_PLAY_HISTORY_DTO);
    }
}
