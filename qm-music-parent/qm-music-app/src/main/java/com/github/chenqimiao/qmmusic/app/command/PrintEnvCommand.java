package com.github.chenqimiao.qmmusic.app.command;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.*;

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
        Set<String> allKeys = new TreeSet<>();
        environment.getPropertySources().forEach(ps -> {
            if (ps instanceof MapPropertySource) {
                String[] propertyNames = ((MapPropertySource) ps).getPropertyNames();
                allKeys.addAll(Arrays.asList(propertyNames));
            }
        });

        // 2. 通过 environment.getProperty(key) 获取最终生效值
        Map<String, String> effectiveProperties = new LinkedHashMap<>();
        for (String key : allKeys) {
            String value = environment.getProperty(key);
            if (value != null) {
                // 过滤敏感字段（如 password）
                if (key.toLowerCase().contains("qm")) {
                    effectiveProperties.put(key, value);
                }

            }
        }

        // 转换为格式化 JSON
        String jsonOutput = JSONObject.toJSONString(
                effectiveProperties,
                JSONWriter.Feature.PrettyFormat
        );

        // 输出到日志（或 System.out）
      log.info("spring env : {}", jsonOutput);

    }
}
