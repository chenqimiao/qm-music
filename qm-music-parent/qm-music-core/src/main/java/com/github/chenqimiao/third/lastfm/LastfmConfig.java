package com.github.chenqimiao.third.lastfm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Qimiao Chen
 * @since 2025/4/15 16:32
 **/
@Configuration
public class LastfmConfig {


    @Value("${qm.lastfm.api_key}")
    private String lastfmApiKey;


    @Bean
    @ConditionalOnProperty(name = "qm.lastfm.enable", havingValue = "true")
    @ConditionalOnExpression("'${qm.lastfm.api_key:}'.trim().length() > 0 " )
    public LastfmClient lastfmClient() {
        return new LastfmClient(lastfmApiKey);
    }
}
