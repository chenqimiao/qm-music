package com.github.chenqimiao.io.net.client;

import com.github.chenqimiao.constant.RateLimiterConstants;
import com.github.chenqimiao.exception.RateLimitException;
import com.github.chenqimiao.io.net.model.ArtistInfo;
import com.github.chenqimiao.third.lastfm.LastfmClient;
import com.github.chenqimiao.third.lastfm.model.Artist;
import com.github.chenqimiao.third.lastfm.model.Track;
import com.github.chenqimiao.util.TransliteratorUtils;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/4/15 16:19
 **/
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnBean(LastfmClient.class)
public class LastfmApiDataFetchClient implements MetaDataFetchClient {

    @Autowired
    private LastfmClient lastfmClient;

    @Override
    public Boolean supportChinaRegion() {
        return Boolean.TRUE;
    }

    @Nullable
    @Override
    public ArtistInfo fetchArtistInfo(String artistName) {
        var artistInfo = new com.github.chenqimiao.third.lastfm.model.ArtistInfo();
        if (TransliteratorUtils.isChineseString(artistName)) {
           artistInfo = lastfmClient.getArtistInfo(artistName, "zh");
        }else {
            artistInfo = lastfmClient.getArtistInfo(artistName, "en");
        }
        if (artistInfo == null) {
            return null;
        }
        ArtistInfo result = new ArtistInfo();
        result.setArtistName(artistName);
        if (artistInfo.getBio() != null) {
            result.setBiography(artistInfo.getBio().getSummary());
        }
        return result;
    }

    @Override
   public List<String> scrapeSimilarArtists(String artistName) {
        // 繁体 or 简体
        List<Artist> similarArtists = lastfmClient.getSimilarArtists(artistName, 5);
        if (CollectionUtils.isNotEmpty(similarArtists)) {
            return similarArtists.stream().sorted((n1, n2) -> {
                return (int)(n2.getMatchScore() -n1.getMatchScore());
            }).map(Artist::getName).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> scrapeSimilarTrack(String trackName, String artistName, Integer limit) {
        // 简体
        List<Track> similarTracks = lastfmClient.getSimilarTracks(trackName, artistName, limit);
        if (CollectionUtils.isNotEmpty(similarTracks)) {
            return similarTracks.stream().sorted((n1, n2) -> {
                return (int)(n2.getMatchScore() -n1.getMatchScore());
            }).map(Track::getName).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


    @Override
    public void rateLimit() {
        RateLimiter limiter = RateLimiterConstants
                .limiters.computeIfAbsent(RateLimiterConstants.LAST_FM_API_LIMIT_KET,
                        key -> RateLimiter.create(3));

        // 尝试获取令牌
        if (!limiter.tryAcquire(1, TimeUnit.MILLISECONDS)) {
            throw new RateLimitException();
        }

    }
}

