package com.github.chenqimiao.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Qimiao Chen
 * @since 2025/4/3 16:57
 **/
@AllArgsConstructor
@Getter
public enum EnumArtistRelationType {

    SONG(1, "song"),
    ALBUM(2, "album"),
    ;
    private final Integer code;

    private final String desc;


    public static EnumArtistRelationType parseObjByName(Integer code){
        Optional<EnumArtistRelationType> instance = Arrays.stream(values())
                .filter(obj -> obj.getCode().equals(code)).findFirst();
        return instance.orElse(null);
    }
}
