package com.github.chenqimiao.util;

import com.github.chenqimiao.config.InsecureHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author Qimiao Chen
 * @since 2025/4/6 05:41
 **/
public abstract class ImageToByteConverter {

    private static String USER_AGENT = UserAgentGenerator.generateUserAgent();


    public static byte[] convertWithHttpClient(String imageUrl) throws IOException, InterruptedException {

        HttpClient insecureClient = InsecureHttpClient.createInsecureClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(imageUrl))
                .header("User-Agent", USER_AGENT)
                .GET()
                .build();

        HttpResponse<InputStream> response = insecureClient.send(
                request,
                HttpResponse.BodyHandlers.ofInputStream()
        );

        if (response.statusCode() != 200) {
            throw new IOException("HTTP error code: " + response.statusCode());
        }

        try (InputStream inputStream = response.body();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray();
        }
    }
}
