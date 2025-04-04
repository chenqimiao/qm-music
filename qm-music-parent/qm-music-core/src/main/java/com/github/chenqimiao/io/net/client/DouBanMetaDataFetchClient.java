package com.github.chenqimiao.io.net.client;

import com.github.chenqimiao.io.net.model.ArtistInfo;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 14:18
 **/
@Component
public class DouBanMetaDataFetchClient implements MetaDataFetchClient {

    private static final String BASE_URL = "https://music.douban.com/subject_search?search_text=";

    @Override
    @SneakyThrows
    public ArtistInfo fetchArtistInfo(String artistName) {

        // 1. 搜索艺人并获取详情页 URL
        String searchUrl = BASE_URL
                + URLEncoder.encode(artistName, StandardCharsets.UTF_8);
        Document searchDoc = Jsoup.connect(searchUrl)
                .userAgent(this.getUserAgent())
                .timeout(10000)
                .ignoreHttpErrors(true)   // 忽略 HTTP 错误（如 404）
                .ignoreContentType(true)
                .get();

        // 2. 提取首个艺人详情页链接
        Element firstResult = searchDoc.select(".item-root .title a").first();
        if (firstResult == null) {
           return null;
        }
        String detailUrl = firstResult.attr("href");

        // 3. 解析详情页
        Document detailDoc = Jsoup.connect(detailUrl).get();
        ArtistInfo artistInfo = new ArtistInfo();
        artistInfo.setArtistName(artistName);
        this.parseArtistDetails(detailDoc, artistInfo);
        return artistInfo;
    }
    /**
     * 解析详情页数据
     */
    private void parseArtistDetails(Document doc, ArtistInfo artistInfo) {

        // 简介（通常位于 class="related-info" 的 div 内）
        Element bioElement = doc.select(".related-info .intro").first();
        if (bioElement != null) {
            artistInfo.setBiography(bioElement.text().replaceAll("\\s+", " "));
        }

        // 图片 URL（豆瓣通常只提供单张主图，需手动生成多尺寸链接）
        Element imageElement = doc.select(".subject img[rel='v:image']").first();
        if (imageElement != null) {
            String originalImageUrl = imageElement.attr("src");
            artistInfo.setImageUrl(originalImageUrl);;

            // 豆瓣图片尺寸规则：s=small, m=medium, l=large (示例规则)
            artistInfo.setSmallImageUrl(originalImageUrl.replace("/l/", "/s/"));
            artistInfo.setMediumImageUrl(originalImageUrl.replace("/l/", "/m/"));
            artistInfo.setLargeImageUrl(originalImageUrl);
        }
    }

    @Override
    public Boolean supportChinaRegion() {
        return Boolean.TRUE;
    }
}
