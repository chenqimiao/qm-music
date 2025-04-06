package com.github.chenqimiao.io.net.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.chenqimiao.io.net.model.ArtistInfo;
import com.github.chenqimiao.util.RandomStringUtils;
import com.jayway.jsonpath.JsonPath;
import jakarta.annotation.Nullable;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 15:58
 **/
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class Music163MetaDataFetchClient implements MetaDataFetchClient {

    private static final String SEARCH_API = "https://music.163.com/api/search/get";

    private static final String ARTIST_DETAIL_API = "https://music.163.com/api/artist/desc";

    private static final String REFERER = "https://music.163.com/";

    private static final String ARTIST_INFO_API = "https://music.163.com/artist/desc?id=";


    @Override
    public Boolean supportChinaRegion() {
        return Boolean.TRUE;
    }

    @Nullable
    @Override
    @SneakyThrows
    public ArtistInfo fetchArtistInfo(String artistName) {
        ArtistInfo artistInfo = new ArtistInfo();
        artistInfo.setArtistName(artistName);
        String artistImageUrl = this.getArtistImageUrl(artistName);
        if (artistImageUrl != null) {

            artistInfo.setImageUrl(artistImageUrl);
        }


        // 第一步：获取艺术家ID
        String artistId = getArtistId(artistName);
        if (artistId != null) {
            String biography = getArtistBiography(artistId);
            artistInfo.setBiography(biography);
        }
        return artistInfo;
    }

    private String getArtistId(String artistName) throws Exception {
        HttpClient client = getHttpClient();
        String encodedName = URLEncoder.encode(artistName, StandardCharsets.UTF_8);
        String apiUrl = SEARCH_API + "?s=" + encodedName + "&type=100&limit=1";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("User-Agent", USER_AGENT)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject json = JSON.parseObject(response.body());
            JSONObject result = json.getJSONObject("result");

            if (result != null) {
                JSONArray artists = result.getJSONArray("artists");
                if (artists != null && !artists.isEmpty()) {
                    return artists.getJSONObject(0).getString("id");
                }
            }
        }
        return null;
    }

    // 获取艺术家简介
    private  String getArtistBiography(String artistId) throws Exception {
        String targetUrl = ARTIST_INFO_API + artistId;


        // 关键：使用原生请求获取未渲染的HTML
        Document doc = Jsoup.connect(targetUrl)
                .userAgent(USER_AGENT)
                .referrer(REFERER)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .cookie("_ntes_nnid", RandomStringUtils.generate())
                .ignoreContentType(true)
                .get();

        // 定位目标元素
        Elements descSection = Objects.requireNonNull(doc.selectFirst(".n-artdesc")).select("p");
        if (!descSection.isEmpty()) {
            return Objects.requireNonNull(descSection.first()).text();
        } else {
          return null;
        }


    }

    // 清理HTML标签
    private static String cleanHtmlTags(String html) {
        return html.replaceAll("<[^>]*>", "")
                .replaceAll("&nbsp;", " ")
                .replaceAll("\\s+", "\n");
    }

    private String getArtistImageUrl(String artistName) throws Exception {
        HttpClient client = getHttpClient();

        String encodedName = URLEncoder.encode(artistName, StandardCharsets.UTF_8);
        String apiUrl = SEARCH_API + "?s=" + encodedName + "&type=100&limit=1";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("User-Agent", USER_AGENT)
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() == 200) {
            // 使用 FastJSON 解析
            JSONObject json = JSON.parseObject(response.body());
            JSONObject result = json.getJSONObject("result");

            if (result != null) {
                JSONArray artists = result.getJSONArray("artists");
                if (artists != null && !artists.isEmpty()) {
                    JSONObject artist = artists.getJSONObject(0);
                    return artist.getString("picUrl");
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    @SneakyThrows
    public String getLyrics(String songName, String artistName) {
        String encodedName = URLEncoder.encode(songName, StandardCharsets.UTF_8);
        String searchUrl = "https://music.163.com/api/search/get/web?type=1&s=" + encodedName;

        // 1. 搜索歌曲ID
        Document searchDoc = Jsoup.connect(searchUrl)
                .userAgent(getUserAgent())
                .ignoreContentType(true)
                .ignoreHttpErrors(true)   // 忽略 HTTP 错误（如 404）
                .ignoreContentType(true)  // 允许处理JSON
                .get();
        String json = searchDoc.text();
        String songId = JsonPath.read(json, "$.result.songs[0].id").toString();

        // 2. 获取歌词
        String lyricsUrl = "https://music.163.com/api/song/lyric?id=" + songId + "&lv=-1";
        Document lyricsDoc = Jsoup.connect(lyricsUrl).ignoreContentType(true).get();
        String lyricsJson = lyricsDoc.text();
        return JsonPath.read(lyricsJson, "$.lrc.lyric");
    }
}
