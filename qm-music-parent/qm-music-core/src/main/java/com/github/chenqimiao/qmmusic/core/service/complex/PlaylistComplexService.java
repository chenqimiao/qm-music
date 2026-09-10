package com.github.chenqimiao.qmmusic.core.service.complex;

import com.github.chenqimiao.qmmusic.core.dto.ComplexPlaylistDTO;
import com.github.chenqimiao.qmmusic.core.request.UpdatePlaylistRequest;
import jakarta.annotation.Nullable;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 20:52
 **/
public interface PlaylistComplexService {


    List<ComplexPlaylistDTO> queryComplexPlaylist(List<Long> playlistIds, @Nullable Long userId);

    Long createOrUpdatePlaylist(Long playlistId, String name, Long songId, Long userId);

    void deletePlaylistByPlaylistId(Long playlistId);

    /**
     * 删除用户的全部歌单及歌单项（删除用户时清理）
     */
    void deletePlaylistsByUserId(Long userId);

    void updatePlaylist(UpdatePlaylistRequest updatePlaylistRequest);

    void deleteItemsBySongIds(List<Long> songIds);
}
