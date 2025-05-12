package com.github.chenqimiao.qmmusic.app.command;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/5/12
 **/
@Component
@Slf4j
public class PrintEnvCommand implements CommandLineRunner {

    private final ConfigurableEnvironment environment;

    public PrintEnvCommand(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        Map<String, String> allProperties = new HashMap<>();

        environment.getPropertySources().forEach(propertySource -> {
            if (propertySource instanceof MapPropertySource) {
                // 普通属性源（如 application.properties、系统变量等）
                MapPropertySource mapSource = (MapPropertySource) propertySource;
                mapSource.getSource().forEach((key, value) -> {
                    if (key.toLowerCase().contains("qm")) {
                        allProperties.put(key, String.valueOf(value));
                    }
                });
            }
        });

        // 转换为格式化 JSON
        String jsonOutput = JSONObject.toJSONString(
                allProperties,
                JSONWriter.Feature.PrettyFormat
        );

        // 输出到日志（或 System.out）
      log.info("spring env : {}", jsonOutput);

    }
}
