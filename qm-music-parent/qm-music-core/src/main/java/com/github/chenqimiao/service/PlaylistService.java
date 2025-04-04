package com.github.chenqimiao.service;

import com.github.chenqimiao.dto.PlaylistDTO;
import com.github.chenqimiao.dto.PlaylistItemDTO;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 20:13
 **/

public interface PlaylistService {

    List<PlaylistDTO> queryPlaylistsByUserId(Long userId);

    List<PlaylistDTO> queryPlaylistsByPlaylistIds(List<Long> playlistIds);

    List<PlaylistItemDTO> queryPlaylistItemsByPlaylistIds(List<Long> playlistIds);

    PlaylistDTO queryPlaylistByPlaylistId(Long playlistId);

    PlaylistDTO createPlayListAndReturn(String name, Long userId);

    int updatePlaylistNameByPlaylistId(String name, Long playlistId);

    void saveSongToPlaylist(Long songId, Long userId, Long id);

    List<PlaylistItemDTO> queryPlaylistItemsByPlaylistId(Integer playlistId);

}
