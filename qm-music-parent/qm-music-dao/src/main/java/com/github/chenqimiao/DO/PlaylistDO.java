package com.github.chenqimiao.DO;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 19:22
 **/
@Setter
@Getter
public class PlaylistDO {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String cover_art;

    private Integer visibility;

    private Long gmt_create;

    private Long gmt_modify;

    private Integer song_count;
}
