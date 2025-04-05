package com.github.chenqimiao.io.net.client;

import com.github.chenqimiao.io.net.model.ArtistInfo;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 14:16
 **/
@Component
public class LastfmMetaDataFetchClient implements MetaDataFetchClient {

    private static final String BASE_URL = "https://www.lastfm.com/";

    @Override
    @SneakyThrows
    public ArtistInfo fetchArtistInfo(String artistName) {
        String url = BASE_URL + encodeArtistName(artistName);

        Document doc = Jsoup.connect(url)
                .userAgent(getUserAgent())
                .timeout(10000)
                .get();

        // 2. 获取不同尺寸的图片 URL
        Element imageMeta = doc.select("meta[property=og:image]").first();
        String baseImageUrl = imageMeta != null ? imageMeta.attr("content") : "";
        String smallImageUrl = resizeImageUrl(baseImageUrl, "300x300");
        String mediumImageUrl = resizeImageUrl(baseImageUrl, "600x600");
        String largeImageUrl = baseImageUrl; // 原图

        // 3. 获取简介 (biography)
        String biography = doc.select(".wiki-content p").first().text();

        return ArtistInfo.builder().artistName(artistName).biography(biography)
                .smallImageUrl(smallImageUrl).largeImageUrl(largeImageUrl).mediumImageUrl(mediumImageUrl)
                .imageUrl(baseImageUrl).build();
    }

    // 处理艺人名称编码（空格转为+）
    private static String encodeArtistName(String name) {
        return name.replace(" ", "+");
    }

    // 生成不同尺寸的图片 URL（Last.fm 规则）
    private static String resizeImageUrl(String originalUrl, String size) {
        return originalUrl.replace("/i/u/", "/i/u/" + size + "/");
    }

    @Override
    public Boolean supportChinaRegion() {
        return Boolean.TRUE;
    }

    @Override
    @SneakyThrows
    public List<String> scrapeSimilarArtists(String artistName) {
        String url = "https://www.last.fm/music/"
                + URLEncoder.encode(artistName, StandardCharsets.UTF_8)
                + "/+similar";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", getUserAgent())

                .GET()
                .build();

        HttpResponse<String> response = getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        String html = response.body();
        List<String> similarArtists = new ArrayList<>();

        // 使用 Jsoup 解析 HTML（需添加依赖）
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select(".similar-artists-item-name");
        for (Element element : elements) {
            similarArtists.add(element.text().trim());
        }

        return similarArtists;
    }

    @Override
    public String getLastFmUrl(String artistName) {
        return  BASE_URL + encodeArtistName(artistName);
    }
}
