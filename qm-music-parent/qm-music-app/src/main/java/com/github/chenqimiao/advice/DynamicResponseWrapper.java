package com.github.chenqimiao.advice;

import com.alibaba.fastjson2.JSONObject;
import com.github.chenqimiao.constant.ServerConstants;
import com.github.chenqimiao.response.subsonic.SubsonicResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author Qimiao Chen
 * @since 2025/4/26 01:15
 **/
@RestControllerAdvice
public class DynamicResponseWrapper implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        ResolvableType resolvableType = ResolvableType.forMethodParameter(returnType);;
        Class<?> rawClass = resolvableType.getRawClass();
        return rawClass != null && SubsonicResponse.class.isAssignableFrom(rawClass);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 获取请求路径
        String path = request.getURI().getPath();
        boolean isSubsonic = path.contains("/rest"); // 路径包含 `/rest`

        // 获取请求参数（例如 f=json 或 f=xml）
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        String format = servletRequest.getParameter("f");
        // 判断是否需要包装
        boolean needWrap = isSubsonic &&  "json".equalsIgnoreCase(format);

        if (needWrap) {
            // 包装成 { "subsonic-response": { ... } }
            JSONObject wrapper = new JSONObject();
            wrapper.put(ServerConstants.SUBSONIC_RESPONSE_ROOT_WRAP, body);
            return wrapper;
        } else {
            // 直接返回原始对象
            return body;
        }
    }
}
