package com.github.chenqimiao.service.complex;

import com.github.chenqimiao.dto.ComplexPlaylistDTO;
import jakarta.annotation.Nullable;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 20:52
 **/
public interface PlaylistComplexService {


    List<ComplexPlaylistDTO> queryComplexPlaylist(List<Long> playlistIds, @Nullable Long userId);

}
