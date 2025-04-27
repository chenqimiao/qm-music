package com.github.chenqimiao.advice;

import com.github.chenqimiao.constant.ServerConstants;
import com.github.chenqimiao.exception.SubsonicUnauthorizedException;
import com.github.chenqimiao.response.subsonic.SubsonicAuthErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Qimiao Chen
 * @since 2025/3/28
 **/
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SubsonicUnauthorizedException.class)
    @ResponseStatus(HttpStatus.OK)
    public SubsonicAuthErrorResponse handleUnauthorized(SubsonicUnauthorizedException e) {
        SubsonicAuthErrorResponse errorResponse = new SubsonicAuthErrorResponse();
        errorResponse.setStatus(ServerConstants.STATUS_FAIL);
        errorResponse.setError(SubsonicAuthErrorResponse.Error.builder()
                .code(e.getEnumSubsonicAuthCode().getCode())
                .message(e.getEnumSubsonicAuthCode().getMessage()).build());
        return errorResponse;
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<String> handleMediaTypeError(HttpServletRequest request) {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        String fullUrl = query != null ? path + "?" + query : path;

        log.error("不支持的媒体类型请求: {}", fullUrl);

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body("请求路径 " + fullUrl + " 不支持指定格式");
    }
}
