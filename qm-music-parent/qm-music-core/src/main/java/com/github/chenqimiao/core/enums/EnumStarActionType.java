package com.github.chenqimiao.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 17:41
 **/
@Getter
@AllArgsConstructor
public enum EnumStarActionType {
    STAR("star", "star"),
    UN_STAR("un_star", "un_star"),
            ;

    private final String code;


    private final String desc;

}
