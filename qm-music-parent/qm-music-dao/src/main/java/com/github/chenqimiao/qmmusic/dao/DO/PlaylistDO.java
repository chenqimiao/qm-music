package com.github.chenqimiao.qmmusic.dao.DO;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 19:22
 **/
@Setter
@Getter
public class PlaylistDO {
    private Long id;
    private Long user_id;
    private String name;
    private String description;
    private String cover_art;

    private Integer visibility;

    private Timestamp gmt_create;

    private Timestamp gmt_modify;

    private Integer song_count;

    private Integer duration;
}
