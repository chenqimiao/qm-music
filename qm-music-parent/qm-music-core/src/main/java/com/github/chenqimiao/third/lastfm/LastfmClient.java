package com.github.chenqimiao.third.lastfm;

import com.alibaba.fastjson2.JSONObject;
import com.github.chenqimiao.config.InsecureHttpClient;
import com.github.chenqimiao.third.lastfm.model.Artist;
import com.github.chenqimiao.third.lastfm.model.SimilarArtistsResponse;
import com.github.chenqimiao.third.lastfm.model.SimilarTracksResponse;
import com.github.chenqimiao.third.lastfm.model.Track;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/15 12:26
 **/
@Component
@Slf4j
public class LastfmClient {

    private static final String API_URL = "https://ws.audioscrobbler.com/2.0/";

    private final HttpClient httpClient = InsecureHttpClient.getInstance();

    @Value("${qm.lastfm.enable}")
    private Boolean lastfmEnabled;

    @Value("${qm.lastfm.api_key}")
    private String lastfmApiKey;



    // 获取相似艺术家
    public List<Artist> getSimilarArtists(String artist, int limit) {
        if (!this.lastfmEnabled()) {
            return null;
        }
        String encodedArtist = URLEncoder.encode(artist, StandardCharsets.UTF_8);
        String query = String.format(
                "method=artist.getsimilar&artist=%s&api_key=%s&format=json&limit=%d",
                 encodedArtist, lastfmApiKey, limit
        );
        URI uri = URI.create(API_URL + "?" + query);

        String response = sendRequest(uri);
        SimilarArtistsResponse result = JSONObject.parseObject(response, SimilarArtistsResponse.class);

        if(result == null) {
            return null;
        }
        if (result.getError() != null) {
            log.error("API Error: {}" ,  result.getError());
            return null;
        }
        return result.getSimilarArtists().getArtists();
    }

    // 获取相似歌曲
    public List<Track> getSimilarTracks(String track, String artist, int limit) {
        if (!this.lastfmEnabled()) {
            return null;
        }
        String encodedTrack = URLEncoder.encode(track, StandardCharsets.UTF_8);
        String encodedArtist = URLEncoder.encode(artist, StandardCharsets.UTF_8);
        String query = String.format(
                "method=track.getsimilar&track=%s&artist=%s&api_key=%s&format=json&limit=%d",
                encodedTrack, encodedArtist, lastfmApiKey, limit
        );
        URI uri = URI.create(API_URL + "?" + query);

        String response = sendRequest(uri);
        SimilarTracksResponse result = JSONObject.parseObject(response, SimilarTracksResponse.class);

        if(result == null) {
            return null;
        }

        if (result.getError() != null) {
            log.error("API Error: {}" ,  result.getError());
            return null;
        }
        return result.getSimilarTracks().getTracks();
    }

    private String sendRequest(URI uri)  {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        try{

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
            if (response.statusCode() != 200) {
                log.error("HTTP Error : {}" ,  response.statusCode());
                return null;
            }
            return response.body();

        }catch (Exception e) {
            log.error("httpClient send error ",  e);
            return null;
        }

    }

    private boolean lastfmEnabled() {
        return Boolean.TRUE.equals(lastfmEnabled)
                && StringUtils.isNotBlank(lastfmApiKey);
    };

}
