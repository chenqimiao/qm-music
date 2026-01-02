package com.github.chenqimiao.qmmusic.core.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 23:48
 **/
@Setter
@Getter
public class UpdatePlaylistRequest {
    private Long playlistId;
    private String name;
    private String description;
    private List<Long> songIdsToAdd;
    private List<Long> songIndexesToRemove;
    private Integer visibility;
}
