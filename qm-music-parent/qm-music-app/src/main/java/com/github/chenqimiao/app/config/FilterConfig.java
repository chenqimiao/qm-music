package com.github.chenqimiao.app.config;

import com.github.chenqimiao.app.filter.ViewSuffixFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @author Qimiao Chen
 * @since 2025/4/26 00:47
 **/
@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<ViewSuffixFilter> viewSuffixFilter() {
        FilterRegistrationBean<ViewSuffixFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new ViewSuffixFilter());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // 确保优先执行
        return bean;
    }
}
