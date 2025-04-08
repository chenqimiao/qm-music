package com.github.chenqimiao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Qimiao Chen
 * @since 2025/3/27 16:09
 **/
@SpringBootApplication(scanBasePackages = "com.github.chenqimiao")
@EnableTransactionManagement
public class QmMusicApplication  {

    public static void main(String[] args) {
        System.setProperty("jsoup.unsafe.ssl", "true");
        System.setProperty("jdk.httpclient.connectionPoolSize", "100");
        SpringApplication.run(QmMusicApplication.class, args);
    }

}