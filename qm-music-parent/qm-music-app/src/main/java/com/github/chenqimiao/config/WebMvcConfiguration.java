package com.github.chenqimiao.config;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import com.github.chenqimiao.interceptor.SubsonicAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;


@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private SubsonicAuthInterceptor subsonicAuthInterceptor;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        converters.removeIf(converter -> converter.getClass().getName().contains("MappingJackson2HttpMessageConverter"));

        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();
        // 配置序列化特性
        config.setWriterFeatures(
                JSONWriter.Feature.WriteNulls        // 输出空字段
        );

        config.setDateFormat("yyyy-MM-dd HH:mm:ss");
        config.setCharset(StandardCharsets.UTF_8);

        converter.setFastJsonConfig(config);
        converter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON));
        converters.add(0, converter);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                // 启用参数匹配模式（默认参数名为 'f'）
                .favorParameter(true)
                // 自定义参数名（例如: /api/data?f=json）
                .parameterName("f")
                // 忽略 Accept 头，仅根据参数处理（可选）
                .ignoreAcceptHeader(true)
                // 设置支持的媒体类型
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("text", MediaType.TEXT_PLAIN)
                // 默认格式（当未指定参数时）
                .defaultContentType(MediaType.APPLICATION_XML);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(subsonicAuthInterceptor).addPathPatterns("/rest/**");
    }

}
