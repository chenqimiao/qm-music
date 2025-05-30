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

    void updatePlaylist(UpdatePlaylistRequest updatePlaylistRequest);
}
