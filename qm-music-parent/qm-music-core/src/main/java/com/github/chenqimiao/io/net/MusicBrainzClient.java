package com.github.chenqimiao.io.net;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 13:22
 **/

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.chenqimiao.io.net.model.Artist;
import com.github.chenqimiao.io.net.model.ArtistDetail;
import com.github.chenqimiao.util.UserAgentGenerator;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MusicBrainzClient {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final String BASE_URL = "https://musicbrainz.org/ws/2/artist/";
    private static final String USER_AGENT = UserAgentGenerator.generateUserAgent();

    // 搜索艺术家
    public static List<Artist> searchArtist(String name) throws Exception {
        String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
        String url = BASE_URL + "?query=artist:" + encodedName + "&fmt=json";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", USER_AGENT)
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return parseSearchResponse(response.body());
    }

    // 获取详细信息
    public static ArtistDetail getArtistDetail(String mbid, String... includes) throws Exception {
        String includeParams = String.join("+", includes);
        String url = BASE_URL + mbid + "?inc=" + includeParams + "&fmt=json";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", USER_AGENT)
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return parseDetailResponse(response.body());
    }

    // 解析搜索响应（Fastjson）
    private static List<Artist> parseSearchResponse(String json) {
        JSONObject root = JSON.parseObject(json);
        JSONArray artistsArray = root.getJSONArray("artists");
        List<Artist> artists = new ArrayList<>();

        for (int i = 0; i < artistsArray.size(); i++) {
            JSONObject artistObj = artistsArray.getJSONObject(i);
            List<String> tags = new ArrayList<>();
            JSONArray tagsArray = artistObj.getJSONArray("tags");
            if (tagsArray != null) {
                for (int j = 0; j < tagsArray.size(); j++) {
                    tags.add(tagsArray.getJSONObject(j).getString("name"));
                }
            }
            artists.add(new Artist(
                    artistObj.getString("id"),
                    artistObj.getString("name"),
                    artistObj.getString("country"),
                    tags
            ));
        }
        return artists;
    }

    // 解析详细信息响应（Fastjson）
    private static ArtistDetail parseDetailResponse(String json) {
        JSONObject artistObj = JSON.parseObject(json);
        List<ArtistDetail.Tag> tags = new ArrayList<>();
        JSONArray tagsArray = artistObj.getJSONArray("tags");
        if (tagsArray != null) {
            for (int i = 0; i < tagsArray.size(); i++) {
                JSONObject tag = tagsArray.getJSONObject(i);
                tags.add(new ArtistDetail.Tag(
                        tag.getString("name"),
                        tag.getIntValue("count")
                ));
            }
        }

        List<ArtistDetail.Work> works = new ArrayList<>();
        JSONArray worksArray = artistObj.getJSONArray("works");
        if (worksArray != null) {
            for (int i = 0; i < worksArray.size(); i++) {
                JSONObject work = worksArray.getJSONObject(i);
                works.add(new ArtistDetail.Work(
                        work.getString("id"),
                        work.getString("title")
                ));
            }
        }

        return new ArtistDetail(
                artistObj.getString("id"),
                artistObj.getString("name"),
                artistObj.getString("disambiguation"),
                tags,
                works
        );
    }


    @SneakyThrows
    public static void main(String[] args) {
//        List<Artist> artists = MusicBrainzClient.searchArtist("陈奕迅");
//        System.out.println(JSONObject.toJSONString(artists));
//        ArtistDetail artistDetail = MusicBrainzClient.getArtistDetail("86119d30-d930-4e65-a97a-e31e22388166", "tags", "works");
//        System.out.println(JSONObject.toJSONString(artistDetail));

    }
}