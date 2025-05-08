package com.github.chenqimiao.qmmusic.core.service.impl;

import com.github.chenqimiao.qmmusic.core.constant.ModelMapperTypeConstants;
import com.github.chenqimiao.qmmusic.core.dto.PlaylistDTO;
import com.github.chenqimiao.qmmusic.core.dto.PlaylistItemDTO;
import com.github.chenqimiao.qmmusic.core.enums.EnumPlayListVisibility;
import com.github.chenqimiao.qmmusic.core.service.PlaylistService;
import com.github.chenqimiao.qmmusic.dao.DO.PlaylistDO;
import com.github.chenqimiao.qmmusic.dao.DO.PlaylistItemDO;
import com.github.chenqimiao.qmmusic.dao.repository.PlaylistItemRepository;
import com.github.chenqimiao.qmmusic.dao.repository.PlaylistRepository;
import com.google.common.collect.Lists;
import io.github.mocreates.Sequence;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 20:14
 **/
@Service("subsonicPlaylistService")
public class SubsonicPlaylistServiceImpl implements PlaylistService {


    @Autowired
    private PlaylistRepository playlistRepository;

    @Resource
    private ModelMapper ucModelMapper;

    @Autowired
    private PlaylistItemRepository playlistItemRepository;

    @Autowired
    private Sequence sequence;

    @Override
    public List<PlaylistDTO> queryPlaylistsByUserId(Long userId) {
        List<PlaylistDO> playlists = playlistRepository.getPlaylists(userId);
        return ucModelMapper.map(playlists, ModelMapperTypeConstants.TYPE_LIST_PLAYLIST_DTO);
    }

    @Override
    public List<PlaylistDTO> queryPlaylistsByPlaylistIds(List<Long> playlistIds) {
        List<PlaylistDO> playlists = playlistRepository.getPlaylistsByIds(playlistIds);
        return ucModelMapper.map(playlists, ModelMapperTypeConstants.TYPE_LIST_PLAYLIST_DTO);
    }

    @Override
    public List<PlaylistItemDTO> queryPlaylistItemsByPlaylistIds(List<Long> playlistIds) {
        List<PlaylistItemDO> playlistItems = playlistItemRepository.queryByPlaylistIds(playlistIds);
        return ucModelMapper.map(playlistItems, ModelMapperTypeConstants.TYPE_LIST_PLAYLIST_ITEM_DTO);
    }

    @Override
    public PlaylistDTO queryPlaylistByPlaylistId(Long playlistId) {
        List<PlaylistDTO> playlists = this.queryPlaylistsByPlaylistIds(Lists.newArrayList(playlistId));
        return CollectionUtils.isEmpty(playlists) ? null : playlists.getFirst();
    }

    @Override
    public PlaylistDTO createPlayListAndReturn(String name, Long userId) {
        long id = sequence.nextId();
        PlaylistDO playlistDO = new PlaylistDO();
        playlistDO.setId(id);
        playlistDO.setName(name);
        playlistDO.setUser_id(userId);
        playlistDO.setDescription("");
        playlistDO.setCover_art("");
        playlistDO.setVisibility(EnumPlayListVisibility.PRIVATE.getCode());
        playlistDO.setSong_count(NumberUtils.INTEGER_ZERO);
        playlistRepository.save(playlistDO);
        return this.queryPlaylistByPlaylistId(id);
    }

    @Override
    public int updatePlaylistNameByPlaylistId(String name, Long playlistId) {

       return playlistRepository.updateNameByPlaylistId(name, playlistId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSongToPlaylist(Long songId, Long userId, Long playlistId) {
        PlaylistItemDO playlistItemDO = new PlaylistItemDO();
        playlistItemDO.setId(sequence.nextId());
        playlistItemDO.setPlaylist_id(playlistId);
        playlistItemDO.setSong_id(songId);
        playlistItemRepository.save(playlistItemDO);

        playlistRepository.incrSongCount(playlistId, 1);
    }

    @Override
    public List<PlaylistItemDTO> queryPlaylistItemsByPlaylistId(Long playlistId) {
        List<PlaylistItemDTO> playlistItems = this.queryPlaylistItemsByPlaylistIds(Lists.newArrayList(playlistId));
        return playlistItems;
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

        songCountMap.forEach((k, v) -> {
            playlistRepository.incrSongCount(k, -v.intValue());
        });

        playlistItemRepository.delBySongIds(songIds);

    }
}
