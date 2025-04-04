package com.github.chenqimiao.DO;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 19:24
 **/
@Getter
@Setter
public class PlayHistoryDO {

    private Long id;

    private Long user_id;

    private Long song_id;

    private String client_type;

    private Integer play_count;

    private Long gmt_create;

    private Long gmt_modify;

}
