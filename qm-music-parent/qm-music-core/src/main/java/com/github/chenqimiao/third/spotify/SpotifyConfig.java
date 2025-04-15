package com.github.chenqimiao.third.spotify;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

/**
 * @author Qimiao Chen
 * @since 2025/4/15 09:58
 **/
@Configuration
@Slf4j
public class SpotifyConfig {
    @Value("${qm.spotify.client.id}")
    private String clientId;
    @Value("${qm.spotify.client.secret}")
    private String clientSecret;

    @Bean
    @ConditionalOnProperty(name = "qm.spotify.enable", havingValue = "true")
    @ConditionalOnExpression("'${qm.spotify.client.id:}'.trim().length() > 0 " +
            "&& '${qm.spotify.client.secret:}'.trim().length() > 0")
    public SpotifyApi spotifyApi() {

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();

        // 获取客户端凭证
        ClientCredentialsRequest request = spotifyApi.clientCredentials().build();
        try {
            ClientCredentials credentials = request.execute();
            spotifyApi.setAccessToken(credentials.getAccessToken());
        } catch (Exception e) {
            log.error("Spotify client credentials error", e);
            return null;
        }

        log.info("Spotify client credentials success !");
        return spotifyApi;
    }
}
