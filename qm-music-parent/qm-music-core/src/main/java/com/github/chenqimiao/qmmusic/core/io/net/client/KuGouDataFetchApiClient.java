package com.github.chenqimiao.qmmusic.core.io.net.client;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.github.chenqimiao.qmmusic.core.constant.RateLimiterConstants;
import com.github.chenqimiao.qmmusic.core.exception.RateLimitException;
import com.github.chenqimiao.qmmusic.core.io.net.model.Album;
import com.github.chenqimiao.qmmusic.core.io.net.model.ArtistInfo;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Qimiao Chen
 * @since 2025/4/20 14:17
 **/
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE + 1)
@Slf4j
public class KuGouDataFetchApiClient implements MetaDataFetchApiClient{

    private final HttpClient HTTP_CLIENT = getHttpClient();


    private static final String SONG_SEARCH_URL = "http://mobilecdn.kugou.com/api/v3/search/song?keyword=";

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
    public Album searchAlbum(String albumTitle, String artistName) {
        try {
            // 1. 编码关键词并构建URL
            String encodedKeyword = URLEncoder.encode(albumTitle, StandardCharsets.UTF_8);
            String url = SONG_SEARCH_URL + encodedKeyword;

            // 2. 创建HTTP请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", USER_AGENT)
                    .GET()
                    .build();

            // 3. 发送请求并获取响应
            HttpResponse<String> response = HTTP_CLIENT.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            // 4. 处理状态码
            if (response.statusCode() != 200) {
                log.error("请求失败，状态码: {}", response.statusCode());
                return null;
            }

            // 5. 使用Fastjson2解析JSON
            SongResponse songResponse = JSONObject.parseObject(
                    response.body(),
                    SongResponse.class
            );

            if (songResponse.status() != 1 || songResponse.data() == null) {
                log.error("API返回错误: {}", songResponse.error());
                return null;
            }

            if (songResponse.data != null) {
                List<SongInfo> info = songResponse.data.info;
                if (CollectionUtils.isEmpty(info)) {
                    return null;
                }
                String unionCover = info.getFirst().transParam.unionCover;
                if (StringUtils.isBlank(unionCover)) {
                    return null;
                }

                Album album = new Album();
                album.setAlbumTitle(albumTitle);
                album.setImageUrl(unionCover.replace("{size}", "100"));
                return album;

            }

        } catch (Exception e) {
            log.error("API query error ", e);
            return null;
        }
        return null;
    }

    public void rateLimit() {
        RateLimiter limiter = RateLimiterConstants
                .limiters.computeIfAbsent(RateLimiterConstants.KU_GOU_API_LIMIT_KEY,
                        key -> RateLimiter.create(25));

        // 尝试获取令牌
        if (!limiter.tryAcquire(1, TimeUnit.MILLISECONDS)) {
            throw new RateLimitException();
        }
    }



    private record SongResponse(
            int status,
            String error,
            SongData data // 注意 data 是对象，不是列表
    ) {}

    private record SongData(
            List<SongInfo> info
    ) {}

    private record SongInfo(
            @JSONField(name = "trans_param")
            TransParam transParam
    ) {}

    private record TransParam(
            @JSONField(name = "union_cover")
            String unionCover
    ) {}
}
