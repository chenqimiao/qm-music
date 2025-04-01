package com.github.chenqimiao.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 00:46
 **/
@AllArgsConstructor
@Getter
public enum EnumAudioCodec {
    ACC("acc", Lists.newArrayList("mp4, flv"), "高效音频编码"),
    LIB_MP3_LAME("libmp3lame", Lists.newArrayList("mp3"), "MP3 编码"),
    lib_x264("libx264", Lists.newArrayList("mp4", "flv"), "H.264/AVC 编码"),
    vp9("vp9", Lists.newArrayList("webm"), "VP9 编码"),
    ;

    private final String name;

    private final List<String> supportedFormats;

    private final String desc;

    public static EnumAudioCodec parseObjByCode(String code){
        Optional<EnumAudioCodec> instance = Arrays.stream(values()).filter(obj -> obj.getName().equals(code)).findFirst();
        return instance.orElse(null);
    }

    public static List<EnumAudioCodec> byFormat(String format){
        format = format.toLowerCase();
        return Arrays.stream(values()).filter(obj -> obj.getSupportedFormats().contains(format)).collect(Collectors.toList());
    }
}
