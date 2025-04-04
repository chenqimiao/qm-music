package com.github.chenqimiao.DO;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 19:26
 **/
@Setter
@Getter
public class PlaylistItemDO {

    private Long id;

    private Long playlist_id;

    private Integer song_id;

    private Long sort_order;

    private Long gmt_create;

    private Long gmt_modify;
}
