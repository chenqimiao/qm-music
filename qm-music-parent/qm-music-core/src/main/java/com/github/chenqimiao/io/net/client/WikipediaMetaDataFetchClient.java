package com.github.chenqimiao.io.net.client;

import com.github.chenqimiao.constant.JsoupConstants;
import com.github.chenqimiao.io.net.model.ArtistInfo;
import com.github.chenqimiao.util.UserAgentGenerator;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 14:21
 **/
@Component
public class WikipediaMetaDataFetchClient implements MetaDataFetchClient {

    private static final String BASE_URL = "https://en.wikipedia.org/wiki/";


    @Override
    @SneakyThrows
    public ArtistInfo fetchArtistInfo(String artistName) {
        String encodedName = URLEncoder.encode(artistName, StandardCharsets.UTF_8);
        String url = BASE_URL + encodedName;

        // 发送请求（需设置 User-Agent）
        Document doc = Jsoup.connect(url)
                .userAgent(UserAgentGenerator.generateUserAgent())
                .timeout(JsoupConstants.TIME_OUT)
                .ignoreHttpErrors(true)   // 忽略 HTTP 错误（如 404）
                .ignoreContentType(true)
                .get();
        ArtistInfo artistInfo = new ArtistInfo();
        artistInfo.setArtistName(artistName);
        this.parseArtistDetails(doc, artistInfo);
        return artistInfo;
    }

    /**
     * 解析页面数据
     */
    private void parseArtistDetails(Document doc, ArtistInfo artistInfo) {
        // 1. 获取艺人名称（页面标题）
        Element title = doc.select("h1#firstHeading").first();
        // 2. 获取简介（首段文本）
        Element firstParagraph = doc.select("#mw-content-text p").first();
        if (firstParagraph != null) {
            artistInfo.setArtistName(firstParagraph.text().replaceAll("\\[\\d+\\]", "")); // 去除引用标记
        }

        // 3. 获取图片 URL（信息框中的第一张图片）
        Element image = doc.select(".infobox img").first();
        if (image != null) {
            String originalUrl = image.attr("src");
            if (originalUrl.startsWith("//")) {
                originalUrl = "https:" + originalUrl;
            }
            artistInfo.setImageUrl(originalUrl);;

            // 生成不同尺寸的图片 URL（维基媒体服务参数）
            artistInfo.setSmallImageUrl(this. resizeImageUrl(originalUrl, 300));
            artistInfo.setMediumImageUrl(this. resizeImageUrl(originalUrl, 600));
            artistInfo.setLargeImageUrl(this. resizeImageUrl(originalUrl, 1200));
        }

    }

    private String resizeImageUrl(String originalUrl, int width) {
        // 示例规则：替换原 URL 中的尺寸参数
        return originalUrl.replace("/commons/thumb/", "/commons/thumb/0/0/")
                .replaceAll("/\\d+px-", "/" + width + "px-");
    }

    @Override
    public Boolean supportChinaRegion() {
        return Boolean.FALSE;
    }

}
