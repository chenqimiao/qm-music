package com.github.chenqimiao.qmmusic.dao.DO;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author Qimiao Chen
 * @since 2026/9/10
 **/
@Getter
@Setter
public class PlayQueueDO {

    private Long id;

    private Long user_id;

    /**
     * 逗号分隔的歌曲 id，保持队列顺序
     */
    private String song_ids;

    private Long current_song_id;

    /**
     * 当前歌曲播放进度（毫秒）
     */
    private Long position;

    private String changed_by;

    /**
     * 最后修改时间（epoch 毫秒）
     */
    private Long changed;

    private Timestamp gmt_create;

    private Timestamp gmt_modify;
}
