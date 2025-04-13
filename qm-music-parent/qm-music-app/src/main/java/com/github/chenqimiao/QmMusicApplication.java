/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.chenqimiao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Qimiao Chen
 * @since 2025/3/27 16:09
 **/
@SpringBootApplication(scanBasePackages = "com.github.chenqimiao")
@EnableScheduling
@EnableTransactionManagement
public class QmMusicApplication  {

    public static void main(String[] args) {
        System.setProperty("jsoup.unsafe.ssl", "true");
        System.setProperty("http.keepAlive.timeout", "60");
        System.setProperty("http.maxConnections", "60");
        System.setProperty("jdk.httpclient.connectionPoolSize", "100");
        System.setProperty("jdk.httpclient.keepAliveTimeout", "60");
        SpringApplication.run(QmMusicApplication.class, args);
    }

}