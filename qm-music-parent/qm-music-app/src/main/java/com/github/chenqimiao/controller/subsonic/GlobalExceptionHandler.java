package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.exception.SubsonicUnauthorizedException;
import com.github.chenqimiao.response.subsonic.SubsonicAuthErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Qimiao Chen
 * @since 2025/3/28 17:34
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SubsonicUnauthorizedException.class)
    @ResponseStatus(HttpStatus.OK)
    public SubsonicAuthErrorResponse handleUnauthorized(SubsonicUnauthorizedException e) {
        return SubsonicAuthErrorResponse.builder()
                .error(SubsonicAuthErrorResponse.Error.builder()
                        .code(e.getEnumSubsonicAuthCode().getCode())
                        .message(e.getEnumSubsonicAuthCode().getMessage())
                        .build())
                .build();
    }
}
