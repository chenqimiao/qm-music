package com.github.chenqimiao.qmmusic.app.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Qimiao Chen
 * @since 2025/3/27 23:18
 **/
@Configuration
@Slf4j
public class FlywayConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Bean
    @Primary
    public FlywayMigrationInitializer flywayInitializer(Flyway flyway, ObjectProvider<FlywayMigrationStrategy> migrationStrategy) {
        this.beforeCreateFlywayMigrationInitializer();
        return new FlywayMigrationInitializer(flyway, (FlywayMigrationStrategy) migrationStrategy.getIfAvailable());
    }


    @SneakyThrows
    private void beforeCreateFlywayMigrationInitializer()  {
        // 从 JDBC URL 中提取文件路径
        String filePath = extractSqliteFilePath(jdbcUrl);

        if (filePath != null) {
            Path dbPath = Paths.get(filePath);
            // 创建父目录（如果不存在）
            Files.createDirectories(dbPath.getParent());
            log.info("已创建数据库目录:{} " , dbPath.getParent());
        }

    }

    private String extractSqliteFilePath(String jdbcUrl) {
        // 移除 "jdbc:sqlite:" 前缀
        String pathPart = jdbcUrl.replaceFirst("^jdbc:sqlite:", "");

        // 处理包含 "file:" 和查询参数的情况
        if (pathPart.startsWith("file:")) {
            pathPart = pathPart.replaceFirst("^file:", "");
            // 分离查询参数（如 ?mode=rwc）
            int queryParamIndex = pathPart.indexOf('?');
            if (queryParamIndex > 0) {
                pathPart = pathPart.substring(0, queryParamIndex);
            }
        }

        return pathPart.isEmpty() ? null : pathPart;
    }

}
