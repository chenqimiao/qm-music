package com.github.chenqimiao.qmmusic.core.service.complex.impl;

import com.github.chenqimiao.qmmusic.core.dto.*;
import com.github.chenqimiao.qmmusic.core.request.UpdatePlaylistRequest;
import com.github.chenqimiao.qmmusic.core.service.PlaylistService;
import com.github.chenqimiao.qmmusic.core.service.SongService;
import com.github.chenqimiao.qmmusic.core.service.complex.PlaylistComplexService;
import com.github.chenqimiao.qmmusic.core.service.complex.SongComplexService;
import com.github.chenqimiao.qmmusic.dao.DO.PlaylistItemDO;
import com.github.chenqimiao.qmmusic.dao.repository.PlaylistItemRepository;
import com.github.chenqimiao.qmmusic.dao.repository.PlaylistRepository;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
    @Autowired
    private SongService songService;
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
            SongDTO songDTO = songService.queryBySongId(songId);
            if (songDTO != null) {
                playlistService.saveSongToPlaylist(songId, songDTO.getDuration(), userId, playlistDTO.getId());
            }
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
    @Transactional(rollbackFor = Exception.class)
    public void updatePlaylist(UpdatePlaylistRequest updatePlaylistRequest) {
        Long playlistId = updatePlaylistRequest.getPlaylistId();
        List<Long> songIdsToAdd = updatePlaylistRequest.getSongIdsToAdd();
        List<Long> songIndexToRemove = updatePlaylistRequest.getSongIndexesToRemove();
        List<Long> songIdsToRemove = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(songIndexToRemove)) {
            List<PlaylistItemDO> playlistItemsToRemove = playlistItemRepository.queryByPlaylistIdAndIndexes(playlistId, songIndexToRemove);
            songIdsToRemove.addAll(playlistItemsToRemove.stream().map(PlaylistItemDO::getSong_id).toList());
            playlistItemRepository.deleteByPlaylistIdAndPositionIndex(playlistId, songIndexToRemove);
        }
        // 先执行删除，再执行添加，避免位置索引冲突
        if (CollectionUtils.isNotEmpty(songIdsToAdd)) {
            songIdsToAdd.forEach(songId -> {
                PlaylistItemDO playlistItem = new PlaylistItemDO();
                playlistItem.setPlaylist_id(playlistId);
                playlistItem.setSong_id(songId);
                playlistItemRepository.save(playlistItem);
            });
        }

        int incrNum = CollectionUtils.size(songIdsToAdd) - CollectionUtils.size(songIdsToRemove);

        if (incrNum != 0) {
            playlistRepository.incrSongCount(playlistId, incrNum);
        }

        String name = updatePlaylistRequest.getName();
        Integer visibility = updatePlaylistRequest.getVisibility();
        String description = updatePlaylistRequest.getDescription();

        if (name != null || description != null || visibility != null
                || CollectionUtils.isNotEmpty(songIdsToAdd)) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("playlistId", playlistId);
            paramMap.put("name", name);
            paramMap.put("description", description);
            paramMap.put("visibility", visibility);
            Long coverArt = CollectionUtils.isNotEmpty(songIdsToAdd)
                    ? songIdsToAdd.getLast()
                    : null;
            paramMap.put("coverArt", coverArt);
            playlistRepository.updateByPlaylistId(paramMap);
        }

        int durationToRemove = songService.sumDurationBySongIds(songIdsToRemove);

        int durationToAdd = songService.sumDurationBySongIds(songIdsToAdd);

        int durationToIncr = durationToAdd - durationToRemove;

        if (durationToIncr != NumberUtils.INTEGER_ZERO) {

            playlistRepository.incrDuration(playlistId, durationToIncr);

        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteItemsBySongIds(List<Long> songIds) {
        List<PlaylistItemDO> playlistItems = playlistItemRepository.queryBySongIds(songIds);
        if (CollectionUtils.isEmpty(playlistItems)) {
            return;
        }
        Map<Long, Long> songCountMap =
                playlistItems.stream().collect(Collectors.groupingBy(PlaylistItemDO::getPlaylist_id, Collectors.counting()));

        // 更新歌单歌曲数量
        songCountMap.forEach((k, v) -> {
            playlistRepository.incrSongCount(k, -v.intValue());
        });

        playlistItemRepository.delBySongIds(songIds);

        Map<Long, List<Long>> durationToReduceMap =
                playlistItems.stream().collect(Collectors.groupingBy(PlaylistItemDO::getPlaylist_id,
                        Collectors.mapping(PlaylistItemDO::getSong_id, Collectors.toList())
                ));

        // 更新歌单时长
        durationToReduceMap.forEach((k, v) -> {

            int durationToReduce = songService.sumDurationBySongIds(v);

            if (Objects.equals(durationToReduce, NumberUtils.INTEGER_ZERO)) {
                return;
            }
            playlistRepository.incrDuration(k, -durationToReduce);

        });

        // 刷新歌单封面
        playlistItems.stream().map(PlaylistItemDO::getPlaylist_id).distinct().forEach(playlistId -> {
            playlistItemRepository.queryByPlaylistIdAndIndexes(playlistId, Lists.newArrayList(NumberUtils.LONG_ZERO)).stream()
                    .findFirst()
                    .ifPresent(item -> {
                        Map<String, Object> paramMap = new HashMap<>();
                        paramMap.put("playlistId", playlistId);
                        paramMap.put("coverArt", item.getSong_id());
                        playlistRepository.updateByPlaylistId(paramMap);
                    });
        });
    }

}
