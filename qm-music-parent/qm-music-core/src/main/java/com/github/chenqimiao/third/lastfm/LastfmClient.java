package com.github.chenqimiao.third.lastfm;

import com.alibaba.fastjson2.JSONObject;
import com.github.chenqimiao.config.InsecureHttpClient;
import com.github.chenqimiao.third.lastfm.model.*;
import lombok.extern.slf4j.Slf4j;

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

@Slf4j
public class LastfmClient {

    private static final String API_URL = "https://ws.audioscrobbler.com/2.0/";

    private final HttpClient httpClient = InsecureHttpClient.getInstance();


    private final String lastfmApiKey;

    public LastfmClient(String lastfmApiKey) {
        this.lastfmApiKey = lastfmApiKey;
    }

    public ArtistInfo getArtistInfo(String artistName, String lang) {
        String encodedArtist = URLEncoder.encode(artistName, StandardCharsets.UTF_8);
        String query = String.format(
                "method=artist.getinfo&artist=%s&api_key=%s&format=json&lang=%s",
                encodedArtist, lastfmApiKey, lang
        );
        URI uri = URI.create(API_URL + "?" + query);

        String response = sendRequest(uri);
        ArtistInfoResponse result = JSONObject.parseObject(response, ArtistInfoResponse.class);

        if(result == null) {
            return null;
        }
        if (result.getError() != null) {
            log.error("API Error: {}" ,  result.getError());
            return null;
        }
        return result.getArtistInfo();
    }



    // 获取相似艺术家
    public List<Artist> getSimilarArtists(String artist, int limit) {
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

}
