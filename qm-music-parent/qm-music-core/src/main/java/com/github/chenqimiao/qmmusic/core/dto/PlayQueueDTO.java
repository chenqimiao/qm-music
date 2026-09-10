package com.github.chenqimiao.qmmusic.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2026/9/10
 **/
@Getter
@Setter
public class PlayQueueDTO {

    private Long userId;

    /**
     * 队列中的歌曲 id，按播放顺序
     */
    private List<Long> songIds;

    private Long currentSongId;

    /**
     * 当前歌曲播放进度（毫秒）
     */
    private Long position;

    private String changedBy;

    /**
     * 最后修改时间（epoch 毫秒）
     */
    private Long changed;
}
