package com.github.chenqimiao.qmmusic.core.service.impl;

import com.github.chenqimiao.qmmusic.core.dto.PlayQueueDTO;
import com.github.chenqimiao.qmmusic.core.request.SavePlayQueueRequest;
import com.github.chenqimiao.qmmusic.core.service.PlayQueueService;
import com.github.chenqimiao.qmmusic.dao.DO.PlayQueueDO;
import com.github.chenqimiao.qmmusic.dao.repository.PlayQueueRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2026/9/10
 **/
@Service("subsonicPlayQueueServiceImpl")
public class SubsonicPlayQueueServiceImpl implements PlayQueueService {

    private static final String SONG_ID_SEPARATOR = ",";

    @Autowired
    private PlayQueueRepository playQueueRepository;

    @Override
    public void save(SavePlayQueueRequest request) {
        if (CollectionUtils.isEmpty(request.getSongIds())) {
            playQueueRepository.deleteByUserId(request.getUserId());
            return;
        }
        PlayQueueDO playQueueDO = new PlayQueueDO();
        playQueueDO.setUser_id(request.getUserId());
        playQueueDO.setSong_ids(request.getSongIds().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(SONG_ID_SEPARATOR)));
        playQueueDO.setCurrent_song_id(request.getCurrentSongId());
        playQueueDO.setPosition(request.getPosition());
        playQueueDO.setChanged_by(request.getChangedBy());
        playQueueDO.setChanged(System.currentTimeMillis());
        playQueueRepository.upsert(playQueueDO);
    }

    @Override
    public PlayQueueDTO queryByUserId(Long userId) {
        PlayQueueDO playQueueDO = playQueueRepository.queryByUserId(userId);
        if (playQueueDO == null) {
            return null;
        }
        PlayQueueDTO playQueueDTO = new PlayQueueDTO();
        playQueueDTO.setUserId(playQueueDO.getUser_id());
        playQueueDTO.setSongIds(parseSongIds(playQueueDO.getSong_ids()));
        playQueueDTO.setCurrentSongId(playQueueDO.getCurrent_song_id());
        playQueueDTO.setPosition(playQueueDO.getPosition());
        playQueueDTO.setChangedBy(playQueueDO.getChanged_by());
        playQueueDTO.setChanged(playQueueDO.getChanged());
        return playQueueDTO;
    }

    @Override
    public void deleteByUserId(Long userId) {
        playQueueRepository.deleteByUserId(userId);
    }

    private List<Long> parseSongIds(String songIds) {
        if (StringUtils.isBlank(songIds)) {
            return Collections.emptyList();
        }
        return Arrays.stream(songIds.split(SONG_ID_SEPARATOR))
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .toList();
    }
}
