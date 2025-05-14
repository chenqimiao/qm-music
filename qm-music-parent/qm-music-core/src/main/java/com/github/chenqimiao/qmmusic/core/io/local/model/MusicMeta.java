package com.github.chenqimiao.qmmusic.core.io.local.model;

import lombok.*;

/**
 * @author Qimiao Chen
 * @since 2025/3/23 21:51
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MusicMeta {

    private String title;

    /**
     * 音乐风格：流行、摇滚、说唱、民谣...
     */
    private String genre;

    /**
     * 歌词
      */
    private String lyrics;

    /**
     * 描述
     */
    private String comment;


    private MusicAlbumMeta musicAlbumMeta;

    private String artist;

    private String format;

    private String bitRate;

    private Integer trackLength;

    private String track;

    // 采样率 (samplingRate)
    private String samplingRate;

    // 声道数 (channelCount) - 注意：某些格式可能不支持
    private String channels;

    private String bitDepth;

    private String trackGain;
    private String trackPeak;

    private String discNo ;
    private String discTotal;
}
