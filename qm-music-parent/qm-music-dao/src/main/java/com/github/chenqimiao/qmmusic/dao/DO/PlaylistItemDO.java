package com.github.chenqimiao.qmmusic.dao.DO;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 19:26
 **/
@Setter
@Getter
public class PlaylistItemDO {

    private Long id;

    private Long playlist_id;

    private Long song_id;

    private Long sort_order;

    private Timestamp gmt_create;

    private Timestamp gmt_modify;

    private Integer duration;
}
