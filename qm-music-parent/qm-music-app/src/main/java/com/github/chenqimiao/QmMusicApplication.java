package com.github.chenqimiao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Qimiao Chen
 * @since 2025/3/27 16:09
 **/
@SpringBootApplication(scanBasePackages = "com.github.chenqimiao")
public class QmMusicApplication  {

    public static void main(String[] args) {
        System.setProperty("jsoup.unsafe.ssl", "true");
        SpringApplication.run(QmMusicApplication.class, args);
    }

}