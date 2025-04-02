package com.github.chenqimiao.service.complex.impl;

import com.github.chenqimiao.dto.AlbumDTO;
import com.github.chenqimiao.dto.ComplexSongDTO;
import com.github.chenqimiao.dto.SongDTO;
import com.github.chenqimiao.enums.EnumUserStarType;
import com.github.chenqimiao.request.BatchStarInfoRequest;
import com.github.chenqimiao.service.AlbumService;
import com.github.chenqimiao.service.SongService;
import com.github.chenqimiao.service.complex.MediaAnnotationService;
import com.github.chenqimiao.service.complex.SongComplexService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 23:41
 **/
@Service("subsonicMediaFetcherService")
@Slf4j
public class SubsonicSongComplexService implements SongComplexService {

    @Autowired
    private SongService songService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MediaAnnotationService mediaAnnotationService;

    @Override
    public List<ComplexSongDTO> queryBySongIds(List<Integer> songIds, Integer userId) {
        if (CollectionUtils.isEmpty(songIds)) {
            return new ArrayList<>();
        }

        List<SongDTO> songs = songService.batchQuerySongs(songIds);
        List<Integer> albumIds = songs.stream().map(SongDTO::getAlbumId).filter(Objects::nonNull).toList();
        List<AlbumDTO> albums = albumService.queryByAlbumIds(albumIds);
        Map<Integer, AlbumDTO> albumMap = albums.stream().collect(Collectors.toMap(AlbumDTO::getId, n -> n));

        BatchStarInfoRequest batchStarInfoRequest = BatchStarInfoRequest.builder().userId(userId)
                .relationIds(songIds).startType(EnumUserStarType.SONG).build();
        Map<Integer, Long> starredTimeMap = mediaAnnotationService.batchQueryStarredTime(batchStarInfoRequest);


        return songs.stream().map(n -> {
            ComplexSongDTO complexSongDTO = modelMapper.map(n, ComplexSongDTO.class);
            AlbumDTO albumDTO = albumMap.get(n.getAlbumId());
            complexSongDTO.setAlbumTitle(albumDTO.getTitle());
            complexSongDTO.setStarred(starredTimeMap.get(n.getAlbumId()));
            complexSongDTO.setIsStar(complexSongDTO.getStarred() != null);
            return complexSongDTO;
        }).toList();
    }
}
