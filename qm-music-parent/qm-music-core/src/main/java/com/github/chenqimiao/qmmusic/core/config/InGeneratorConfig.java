package com.github.chenqimiao.qmmusic.core.config;

import io.github.mocreates.Sequence;
import io.github.mocreates.config.SequenceConfig;
import io.github.mocreates.config.SimpleSequenceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 11:33
 **/
@Configuration
public class InGeneratorConfig {

    @Bean
    public Sequence sequence() {
        SequenceConfig sequenceConfig = new SimpleSequenceConfig();
        return new Sequence(sequenceConfig);
    }
}
