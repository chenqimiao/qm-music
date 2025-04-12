package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.repository.PlayHistoryRepository;
import com.github.chenqimiao.request.PlayHistoryRequest;
import com.github.chenqimiao.request.PlayHistorySaveRequest;
import com.github.chenqimiao.service.PlayHistoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Qimiao Chen
 * @since 2025/4/12 10:33
 **/
@Service("subsonicPlayHistoryService")
public class SubsonicPlayHistoryServiceImpl implements PlayHistoryService {

    @Autowired
    private PlayHistoryRepository playHistoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void save(PlayHistoryRequest playHistoryRequest) {
        PlayHistorySaveRequest playHistorySaveRequest
                = modelMapper.map(playHistoryRequest, PlayHistorySaveRequest.class);
        playHistoryRepository.save(playHistorySaveRequest);

    }
}
