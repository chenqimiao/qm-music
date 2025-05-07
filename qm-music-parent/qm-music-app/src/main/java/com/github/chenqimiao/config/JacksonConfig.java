package com.github.chenqimiao.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.chenqimiao.core.util.DateTimeUtils;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Qimiao Chen
 * @since 2025/4/12 16:49
 **/
@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // 设置全局日期格式
            String pattern = DateTimeUtils.yyyyMMddTHHmmss; // 替换为实际格式
            builder.simpleDateFormat(pattern);

            //使用当前时区
            builder.timeZone(DateTimeUtils.getCurTimezone());

            // 针对 Java 8 时间类型（LocalDateTime等）的配置
            builder.modulesToInstall(new JavaTimeModule());
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            builder.featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        };
    }
}
