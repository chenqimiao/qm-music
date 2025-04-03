package com.github.chenqimiao.service.complex.impl;

import com.github.chenqimiao.dto.ComplexSongDTO;
import com.github.chenqimiao.dto.SongDTO;
import com.github.chenqimiao.enums.EnumUserStarType;
import com.github.chenqimiao.request.BatchStarInfoRequest;
import com.github.chenqimiao.service.AlbumService;
import com.github.chenqimiao.service.SongService;
import com.github.chenqimiao.service.UserStarService;
import com.github.chenqimiao.service.complex.SongComplexService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 23:41
 **/
@Service("subsonicSongComplexService")
@Slf4j
public class SubsonicSongComplexService implements SongComplexService {

    @Autowired
    private SongService songService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserStarService userStarService;

    @Override
    public List<ComplexSongDTO> queryBySongIds(List<Long> songIds, @Nullable Long userId) {
        if (CollectionUtils.isEmpty(songIds)) {
            return new ArrayList<>();
        }
        List<SongDTO> songs = songService.batchQuerySongBySongIds(songIds);

        final Map<Long, Long> starredTimeMap = new HashMap<>();
        if (userId != null) {
            BatchStarInfoRequest batchStarInfoRequest = BatchStarInfoRequest.builder().userId(userId)
                    .relationIds(songIds).startType(EnumUserStarType.SONG).build();
            starredTimeMap.putAll(userStarService.batchQueryStarredTime(batchStarInfoRequest));
        }

        return songs.stream().map(n -> {
            ComplexSongDTO complexSongDTO = modelMapper.map(n, ComplexSongDTO.class);
            complexSongDTO.setStarred(starredTimeMap.get(n.getAlbumId()));
            complexSongDTO.setIsStar(complexSongDTO.getStarred() != null);
            return complexSongDTO;
        }).toList();
    }
}
