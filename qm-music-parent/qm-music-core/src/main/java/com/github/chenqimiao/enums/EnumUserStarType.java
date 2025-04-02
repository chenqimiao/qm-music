package com.github.chenqimiao.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 17:27
 **/
@Getter
@AllArgsConstructor
public enum EnumUserStarType {
    SONG(1, "song"),
    ALBUM(2, "album"),
    ARTIST(3, "artist"),
    ;
    private final Integer code;

    private final String desc;


    public static EnumUserStarType parseObjByName(Integer code){
        Optional<EnumUserStarType> instance = Arrays.stream(values())
                .filter(obj -> obj.getCode().equals(code)).findFirst();
        return instance.orElse(null);
    }
}
