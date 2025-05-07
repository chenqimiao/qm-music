package com.github.chenqimiao.core.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/27 15:24
 **/
@Getter
@AllArgsConstructor
public enum EnumOpenSubsonicExt {

    SONG_LYRICS("songLyrics", Lists.newArrayList(1, 2), "structuredSongLyrics"),
    FORM_POST("formPost", Lists.newArrayList(1, 2), "formPost"),

    ;

    private final String name;

    private final List<Integer> version;

    private final String desc;
}
