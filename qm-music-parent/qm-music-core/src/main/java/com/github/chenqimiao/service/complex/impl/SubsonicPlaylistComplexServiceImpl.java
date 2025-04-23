package com.github.chenqimiao.service.complex.impl;

import com.github.chenqimiao.DO.PlaylistItemDO;
import com.github.chenqimiao.dto.*;
import com.github.chenqimiao.repository.PlaylistItemRepository;
import com.github.chenqimiao.repository.PlaylistRepository;
import com.github.chenqimiao.request.UpdatePlaylistRequest;
import com.github.chenqimiao.service.PlaylistService;
import com.github.chenqimiao.service.complex.PlaylistComplexService;
import com.github.chenqimiao.service.complex.SongComplexService;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 20:59
 **/
@Service("subsonicPlaylistComplexService")
public class SubsonicPlaylistComplexServiceImpl implements PlaylistComplexService {

    @Autowired
    private PlaylistService playlistService;


    @Autowired
    private SongComplexService songComplexService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PlaylistItemRepository playlistItemRepository;
//
//    @Autowired
//    private TransactionTemplate transactionTemplate;


    @Override
    public List<ComplexPlaylistDTO> queryComplexPlaylist(List<Long> playlistIds, Long userId) {
        List<PlaylistDTO> playlists = playlistService.queryPlaylistsByPlaylistIds(playlistIds);
        List<PlaylistItemDTO> playlistItemDTOS = playlistService.queryPlaylistItemsByPlaylistIds(playlistIds);
        Map<Long, List<PlaylistItemDTO>> playlistItemMap = playlistItemDTOS.stream().collect(Collectors.groupingBy(PlaylistItemDTO::getPlaylistId));


        return playlists.stream().map(n -> {
            ComplexPlaylistDTO complexPlaylistDTO = modelMapper.map(n, ComplexPlaylistDTO.class);

            List<PlaylistItemDTO> playlistItems = playlistItemMap.getOrDefault(complexPlaylistDTO.getId(), Collections.emptyList());

            List<Long> songIds = playlistItems.stream().map(PlaylistItemDTO::getSongId).toList();

            List<ComplexSongDTO> complexSongs = songComplexService.queryBySongIds(songIds, userId);

            Map<Long, ComplexSongDTO> complexSongMap = complexSongs.stream().collect(Collectors.toMap(SongDTO::getId, m -> m));

            List<ComplexSongDTO> sortedComplexSongs = playlistItems.stream()
                    .map(x -> complexSongMap.get(x.getSongId())).collect(Collectors.toList());
            complexPlaylistDTO.setComplexSongs(sortedComplexSongs);

            return complexPlaylistDTO;
        }).toList();
    }

    @Override
    public Long createOrUpdatePlaylist(Long playlistId, String name, Long songId, Long userId) {

        PlaylistDTO playlistDTO = playlistService.queryPlaylistByPlaylistId(playlistId);

        return this.doCreateOrUpdatePlaylist(playlistDTO, playlistId, name, songId, userId);
    }




    @Transactional
    public Long doCreateOrUpdatePlaylist(PlaylistDTO existPlaylist ,
                                         Long playlistId, String name, Long songId, Long userId){

        PlaylistDTO playlistDTO ;

        if (playlistId == null) {

            playlistDTO =  playlistService.createPlayListAndReturn(name, userId);
        }else {
            if (existPlaylist == null
                    || !Objects.equals(existPlaylist.getUserId(), userId)) {
                throw new RuntimeException("業務異常");
            }

            playlistService.updatePlaylistNameByPlaylistId(name, playlistId);

            playlistDTO = playlistService.queryPlaylistByPlaylistId(playlistId);
        }


        if (songId != null) {

            playlistService.saveSongToPlaylist(songId, userId, playlistDTO.getId());
        }

        return playlistDTO.getId();
    }


    @Override
    @Transactional
    public void deletePlaylistByPlaylistId(Long playlistId) {
        playlistItemRepository.deleteByPlaylistId(playlistId);
        playlistRepository.delById(playlistId);
    }

    @Override
    @Transactional
    public void updatePlaylist(UpdatePlaylistRequest updatePlaylistRequest) {
        Long playlistId = updatePlaylistRequest.getPlaylistId();
        List<Long> songIdToAdd = updatePlaylistRequest.getSongIdToAdd();
        if (CollectionUtils.isNotEmpty(songIdToAdd)) {
            songIdToAdd.stream().forEach(songId -> {
                PlaylistItemDO playlistItem = new PlaylistItemDO();
                playlistItem.setPlaylist_id(playlistId);
                playlistItem.setSong_id(songId);
                playlistItemRepository.save(playlistItem);
            });
        }
        List<Long> songIndexToRemove = updatePlaylistRequest.getSongIndexToRemove();
        if (CollectionUtils.isNotEmpty(songIndexToRemove)) {
            playlistItemRepository.deleteByPlaylistIdAndPositionIndex(playlistId, songIndexToRemove);
        }

        int incrNum = CollectionUtils.size(songIdToAdd) - CollectionUtils.size(songIndexToRemove);

        if (incrNum != 0) {
            playlistRepository.incrSongCount(playlistId, incrNum);
        }

        String name = updatePlaylistRequest.getName();
        Integer visibility = updatePlaylistRequest.getVisibility();
        String description = updatePlaylistRequest.getDescription();

        if (name != null || description != null || visibility != null
                || CollectionUtils.isNotEmpty(songIdToAdd)) {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("playlistId", playlistId);
            paramMap.put("name", name);
            paramMap.put("description", description);
            paramMap.put("visibility", visibility);
            paramMap.put("coverArt", songIdToAdd.getLast());
            playlistRepository.updateByPlaylistId(paramMap);
        }



    }

}
