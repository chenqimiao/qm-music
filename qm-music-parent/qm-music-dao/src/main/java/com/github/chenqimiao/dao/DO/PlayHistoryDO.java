package com.github.chenqimiao.DO;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

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

    private Timestamp gmt_create;

    private Timestamp gmt_modify;

}
