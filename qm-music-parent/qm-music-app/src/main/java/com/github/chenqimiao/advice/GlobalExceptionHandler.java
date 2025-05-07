package com.github.chenqimiao.advice;

import com.github.chenqimiao.constant.ServerConstants;
import com.github.chenqimiao.enums.EnumSubsonicErrorCode;
import com.github.chenqimiao.core.exception.ResourceDisappearException;
import com.github.chenqimiao.exception.SubsonicCommonErrorException;
import com.github.chenqimiao.response.subsonic.SubsonicCommonErrorResponse;
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

    @ExceptionHandler(SubsonicCommonErrorException.class)
    @ResponseStatus(HttpStatus.OK)
    public SubsonicCommonErrorResponse handleUnauthorized(SubsonicCommonErrorException e) {
        SubsonicCommonErrorResponse errorResponse = new SubsonicCommonErrorResponse();
        errorResponse.setStatus(ServerConstants.STATUS_FAIL);
        errorResponse.setError(SubsonicCommonErrorResponse.Error.builder()
                .code(e.getEnumSubsonicErrorCode().getCode())
                .message(e.getEnumSubsonicErrorCode().getMessage()).build());
        return errorResponse;
    }



    @ExceptionHandler(ResourceDisappearException.class)
    @ResponseStatus(HttpStatus.OK)
    public SubsonicCommonErrorResponse handleResourceDisappearException(ResourceDisappearException e) {
        SubsonicCommonErrorResponse errorResponse = new SubsonicCommonErrorResponse();
        errorResponse.setStatus(ServerConstants.STATUS_FAIL);
        errorResponse.setError(SubsonicCommonErrorResponse.Error.builder()
                .code(EnumSubsonicErrorCode.E_70.getCode())
                .message(e.getMessage()).build());
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
