package com.github.chenqimiao.exception;

import com.github.chenqimiao.enums.EnumSubsonicAuthCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Qimiao Chen
 * @since 2025/3/28 17:35
 **/
@Getter
@AllArgsConstructor
public class SubsonicUnauthorizedException extends RuntimeException{

    private EnumSubsonicAuthCode enumSubsonicAuthCode;

}
