package com.github.chenqimiao;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Qimiao Chen
 * @since 2025/3/27 16:09
 **/
@SpringBootApplication(scanBasePackages = "com.github.chenqimiao")
public class QmMusicApplication  implements CommandLineRunner {

    @Autowired
    private Flyway flyway;

    public static void main(String[] args) {
        SpringApplication.run(QmMusicApplication.class, args);
    }

    @Override
    public void run(String... args)  {
        flyway.migrate();
    }
}