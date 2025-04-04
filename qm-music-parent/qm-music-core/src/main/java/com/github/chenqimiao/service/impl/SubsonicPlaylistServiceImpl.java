package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.DO.PlaylistDO;
import com.github.chenqimiao.DO.PlaylistItemDO;
import com.github.chenqimiao.constant.ModelMapperTypeConstants;
import com.github.chenqimiao.dto.PlaylistDTO;
import com.github.chenqimiao.dto.PlaylistItemDTO;
import com.github.chenqimiao.repository.PlaylistItemRepository;
import com.github.chenqimiao.repository.PlaylistRepository;
import com.github.chenqimiao.service.PlaylistService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 20:14
 **/
@Service("subsonicPlaylistService")
public class SubsonicPlaylistServiceImpl implements PlaylistService {


    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private ModelMapper ucModelMapper;

    @Autowired
    private PlaylistItemRepository playlistItemRepository;

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
}
