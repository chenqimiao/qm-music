package com.github.chenqimiao.io.net.client;

import com.github.chenqimiao.io.net.model.ArtistInfo;
import com.jayway.jsonpath.JsonPath;
import jakarta.annotation.Nullable;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 15:58
 **/
@Component
public class Music163MetaDataFetchClient implements MetaDataFetchClient {
    @Override
    public Boolean supportChinaRegion() {
        return Boolean.TRUE;
    }

    @Nullable
    @Override
    public ArtistInfo fetchArtistInfo(String artistName) {
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
