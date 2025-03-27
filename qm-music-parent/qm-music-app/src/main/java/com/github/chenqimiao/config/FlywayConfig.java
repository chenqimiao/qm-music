package com.github.chenqimiao.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Qimiao Chen
 * @since 2025/3/27 23:18
 **/
@Configuration
public class FlywayConfig {
    @Bean
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration") // 迁移脚本路径
                .baselineOnMigrate(true) // 如果已有数据库，启用基线迁移
                .load();
    }
}
