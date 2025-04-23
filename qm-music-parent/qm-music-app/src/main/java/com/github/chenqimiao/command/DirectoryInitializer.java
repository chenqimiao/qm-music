package com.github.chenqimiao.command;

import com.github.chenqimiao.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/15 21:58
 **/
@Component
@Slf4j
public class DirectoryInitializer  implements CommandLineRunner {
    // 注入配置的目录列表
    @Value("${qm.cache.dir}")
    private String cacheDirectory;


    @Override
    public void run(String... args) throws Exception {
        createDirectories(List.of(cacheDirectory + "/" + CommonConstants.ALBUM_DIR_SUFFIX
                , cacheDirectory + "/" + CommonConstants.SONG_DIR_SUFFIX
                , cacheDirectory + "/" + CommonConstants.ARTIST_DIR_SUFFIX));
    }

    private void createDirectories(List<String> directories) {
        directories.forEach(dir -> {
            Path path = Paths.get(dir);
            try {
                Files.createDirectories(path);
                log.info("Successfully created directory: {} ", path.toAbsolutePath());
            } catch (Exception e) {
                log.error("Failed to create directory: {}", path, e);
                throw new RuntimeException("cache directory creation failed", e);
            }
        });
    }
}
