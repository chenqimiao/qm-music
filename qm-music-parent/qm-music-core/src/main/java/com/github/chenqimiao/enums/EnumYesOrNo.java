package com.github.chenqimiao.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 18:52
 **/
@AllArgsConstructor
@Getter
public enum EnumYesOrNo {

    YES(1, "yes"),
    NO(0, "no"),
    ;
    private final Integer code;

    private final String desc;

}
