package com.github.chenqimiao.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 19:18
 **/
@AllArgsConstructor
@Getter
public enum EnumPlayListVisibility {

     // 0-私有 1-公开 2-分享链接
    PRIVATE(0, "private"),
    PUBLIC(1, "public"),
    SHARE_URL(2, "ShareUrl"),
    ;


    private final Integer code;

    private final String desc;
}
