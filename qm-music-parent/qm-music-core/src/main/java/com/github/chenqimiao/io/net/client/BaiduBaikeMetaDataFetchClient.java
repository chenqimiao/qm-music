package com.github.chenqimiao.io.net.client;

import com.github.chenqimiao.io.net.model.ArtistInfo;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 14:20
 **/
@Component
public class BaiduBaikeMetaDataFetchClient implements MetaDataFetchClient {

    private static final String BASE_URL = "https://baike.baidu.com/item/";

    @Override
    @SneakyThrows
    public ArtistInfo fetchArtistInfo(String artistName) {
        String encodedName = URLEncoder.encode(artistName, StandardCharsets.UTF_8);
        String url = BASE_URL + encodedName;

        // 发送请求（需模拟浏览器 User-Agent）
        Document doc = Jsoup.connect(url)
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Referer", "https://www.baidu.com/")
                .header("Sec-Fetch-Dest", "document")
                .userAgent(this.getUserAgent())
                .ignoreHttpErrors(true)   // 忽略 HTTP 错误（如 404）
                .ignoreContentType(true)  // 忽略 Content-Type 检查 (false)  // 关闭 SSL 证书验证
                .timeout(10_000)
                .get();

        ArtistInfo artistInfo = new ArtistInfo();
        artistInfo.setArtistName(artistName);

        // 解析字段
        artistInfo.setBiography(this.parseBiography(doc));
        this.parseImageUrls(doc, artistInfo);

        if (!StringUtils.isAllBlank(artistInfo.getBiography(), artistInfo.getImageUrl(),
                artistInfo.getLargeImageUrl(), artistInfo.getMediumImageUrl(), artistInfo.getSmallImageUrl())) {
            return artistInfo;
        }

        return null;
    }


    // 解析简介
    private String parseBiography(Document doc) {
        Element summary = doc.select("div.lemma-summary").first();
        if (summary != null) {
            return (summary.text().replaceAll("\\s+", " "));
        }
        return null;
    }

    // 解析图片 URL（动态生成多尺寸）
    private void parseImageUrls(Document doc, ArtistInfo artistInfo) {
        Element image = doc.select("div.summary-pic img").first();
        if (image != null) {
            String originalUrl = image.attr("src");
            artistInfo.setImageUrl(image.attr("src"));
            artistInfo.setSmallImageUrl(originalUrl.replace("pic/", "pic/resize,m_lfit,w_300/"));
            artistInfo.setMediumImageUrl(originalUrl.replace("pic/", "pic/resize,m_lfit,w_600/"));
            artistInfo.setLargeImageUrl(originalUrl);
        }
    }


    @Override
    public Boolean supportChinaRegion() {
        return Boolean.TRUE;
    }

}
