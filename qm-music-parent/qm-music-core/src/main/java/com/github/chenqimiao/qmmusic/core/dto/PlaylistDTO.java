package com.github.chenqimiao.qmmusic.core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 19:22
 **/
@Setter
@Getter
public class PlaylistDTO {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String coverArt;

    private Integer visibility;

    private Long gmtCreate;

    private Long gmtModify;

    private Integer songCount;
}
