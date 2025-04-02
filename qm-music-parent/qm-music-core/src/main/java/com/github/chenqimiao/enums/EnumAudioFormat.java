package com.github.chenqimiao.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;


/**
 * @author Qimiao Chen
 * @since 2025/4/2 13:41
 **/
@AllArgsConstructor
@Getter
public enum EnumAudioFormat {

    MP3("map3", "mp3"),
    FLV("flv", "flv"),
    MP4("mp4", "mp4"),
    WEBM("webm", "webm")
    ;

    private final String name;


    private final String desc;


    public static EnumAudioFormat parseObjByName(String name){
        Optional<EnumAudioFormat> instance = Arrays.stream(values()).filter(obj -> obj.getName().equals(name)).findFirst();
        return instance.orElse(null);
    }
}
