package com.github.chenqimiao.io.local.model;

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
}
