package com.github.chenqimiao.core.io.net.client;

import com.alibaba.fastjson2.JSONObject;
import com.github.chenqimiao.core.constant.RateLimiterConstants;
import com.github.chenqimiao.core.exception.RateLimitException;
import com.github.chenqimiao.core.io.net.config.MetaDataFetchClientConfig;
import com.github.chenqimiao.core.io.net.model.Album;
import com.github.chenqimiao.core.io.net.model.ArtistInfo;
import com.github.chenqimiao.core.io.net.model.Track;
import com.github.chenqimiao.core.util.TimeZoneUtils;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 16:15
 **/
@Component
@Slf4j
public class MetaDataFetchClientCommander implements MetaDataFetchClient{



    @Override
    public Boolean supportChinaRegion() {

        return Boolean.TRUE;
    }


    private Function<MetaDataFetchClient, Boolean> isApi() {
       return n -> {
            return n instanceof MetaDataFetchApiClient;
        };
    }

    private Function<MetaDataFetchClient, Boolean> notApi() {
        return n -> {
            return ! (n instanceof MetaDataFetchApiClient);
        };
    }

    private static List<MetaDataFetchClient> getMetaDataFetchClients() {
        Boolean currentRegionIsChina = TimeZoneUtils.currentRegionIsChina();
        List<MetaDataFetchClient> metaDataFetchClients = MetaDataFetchClientConfig.getMetaDataFetchClients();
        List<MetaDataFetchClient> list = metaDataFetchClients.stream().filter(n -> !Boolean.TRUE.equals(currentRegionIsChina)
                || n.supportChinaRegion()).collect(Collectors.toList());
       // Random random = new Random(System.currentTimeMillis());
        // Collections.shuffle(list, random);
        return list;
    }

    @Nullable
    @Override
    public ArtistInfo fetchArtistInfo(String artistName) {
        this.rateLimit();
        ArtistInfo artistInfo = this.doFetchArtistInfo(artistName, this.isApi());
        Boolean retry = this.needRetry(artistInfo);

        if(Boolean.TRUE.equals(retry)) {

            RateLimiter limiter = RateLimiterConstants.limiters.computeIfAbsent(RateLimiterConstants.HTML_RESOLVER_LIMIT_KEY,
                    key -> RateLimiter.create(2));

            // 尝试获取令牌
            if (!limiter.tryAcquire(1, TimeUnit.MILLISECONDS)) {
                return null;
            }

            ArtistInfo artistInfoExt = this.doFetchArtistInfo(artistName, this.notApi());

            return this.merge(Lists.newArrayList(artistInfo, artistInfoExt));

        }

        return artistInfo;

    }

    private ArtistInfo merge(List<ArtistInfo> artistInfos) {
        ArtistInfo newArtistInfo = new ArtistInfo();

        Field[] fields = ArtistInfo.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object o = field.get(newArtistInfo);
                if (Objects.isNull(o)) {
                    for (ArtistInfo artistInfo : artistInfos) {
                        Object newVal = field.get(artistInfo);
                        if (newVal != null) {
                            field.set(newArtistInfo, newVal);
                            break;
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                log.error("merge artistInfos error : {} ", JSONObject.toJSONString(artistInfos), e);
            }
        }

        return newArtistInfo;
    }

    private Boolean needRetry(ArtistInfo artistInfo) {
        Boolean[] retry = {Boolean.FALSE};

        Field[] fields = ArtistInfo.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object o = field.get(artistInfo);
                if (Objects.isNull(o)) {
                    retry[0] = Boolean.TRUE;
                    break;
                }
            } catch (IllegalAccessException e) {
                log.error("map artist properties[{}] error", JSONObject.toJSONString(artistInfo), e);

            }
        }
        return retry[0];
    }

    private ArtistInfo doFetchArtistInfo(String artistName, Function<MetaDataFetchClient, Boolean> filterFunction) {
        List<ArtistInfo> artistInfos = getMetaDataFetchClients().stream().filter(filterFunction::apply)
                .parallel()
                .map(n -> {
                    try{
                        n.rateLimit();
                        return n.fetchArtistInfo(artistName);
                    }catch (RateLimitException e){
                        log.info("{} fetchArtistInfo rate limit", n.getClass());
                        return null;
                    }catch (Exception e){
                        log.error("{} fetchArtistInfo error", n.getClass(), e);
                        return null;
                    }
                }).filter(Objects::nonNull)
                .sorted( (n1, n2) -> {
                    if (StringUtils.isBlank(n1.getBiography())) {
                        return NumberUtils.INTEGER_ONE;
                    }
                    if (StringUtils.isBlank(n2.getBiography())) {
                        return -NumberUtils.INTEGER_ONE;
                    }
                    return StringUtils.length(n2.getBiography()) - StringUtils.length(n1.getBiography()) ;
                }).toList();

        ArtistInfo properArtist = new ArtistInfo();

        Field[] fields = ArtistInfo.class.getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {

            field.setAccessible(true);
            artistInfos.stream().map(n -> {
                try {
                    return field.get(n);
                } catch (IllegalAccessException e) {
                    log.error("map artist properties[{}] error", JSONObject.toJSONString(n), e);
                    return null;
                }
            }).filter(Objects::nonNull).findFirst().ifPresent(n -> {
                try {
                    field.set(properArtist, n);
                } catch (IllegalAccessException e) {
                    log.error("set properArtist[{}] error", JSONObject.toJSONString(n), e);
                    throw new RuntimeException(e);
                }
            });

        });
        return properArtist;
    }


        @Nullable
    @Override
    public String getLyrics(String songName, String artistName) {
        this.rateLimit();
        for (MetaDataFetchClient metaDataFetchClient : getMetaDataFetchClients()) {
            try {
                metaDataFetchClient.rateLimit();
                String lyrics = metaDataFetchClient.getLyrics(songName, artistName);
                if (StringUtils.isNotBlank(lyrics)) {
                    return lyrics;
                }
            }catch (Exception e) {
                log.warn("getLyrics error", e );
            }

        }
        return null;
    }

    @Override
    public List<String> scrapeSimilarArtists(String artistName) {
        this.rateLimit();
        for (MetaDataFetchClient metaDataFetchClient : getMetaDataFetchClients()) {
            try {
                metaDataFetchClient.rateLimit();
                List<String> similarArtists = metaDataFetchClient.scrapeSimilarArtists(artistName);
                if (CollectionUtils.isNotEmpty(similarArtists)){
                    return similarArtists;
                }
            }catch (Exception e) {
                log.warn("scrapeSimilarArtists error", e );
            }

        }
        return Collections.emptyList();
    }

    @Override
    public String getMusicBrainzId(String artistName) {
        this.rateLimit();
        for (MetaDataFetchClient metaDataFetchClient : getMetaDataFetchClients()) {
            try {
                metaDataFetchClient.rateLimit();
                String musicBrainzId = metaDataFetchClient.getMusicBrainzId(artistName);
                if (StringUtils.isNotBlank(musicBrainzId)) {
                    return musicBrainzId;
                }
            }catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public String getLastFmUrl(String artistName) {
        this.rateLimit();
        for (MetaDataFetchClient metaDataFetchClient : getMetaDataFetchClients()) {
            try {
                metaDataFetchClient.rateLimit();
                String lastFmUrl = metaDataFetchClient.getLastFmUrl(artistName);
                if (StringUtils.isNotBlank(lastFmUrl)) {
                    return lastFmUrl;
                }
            }catch (Exception e) {
                log.warn(e.getMessage());
            }

        }
        return null;
    }

    @Override
    public List<String> scrapeSimilarTrack(String trackName, String artistName, Integer limit) {
        this.rateLimit();
        for (MetaDataFetchClient metaDataFetchClient : getMetaDataFetchClients()) {
            try {
                metaDataFetchClient.rateLimit();
                List<String> trackNames = metaDataFetchClient.scrapeSimilarTrack(trackName, artistName, limit == null ? 20 : limit);
                if (CollectionUtils.isNotEmpty(trackNames)) {
                    return trackNames;
                }
            }catch (Exception e) {
                log.warn(e.getMessage());
            }

        }
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public Track searchTrack(String trackName, String artistName) {
        this.rateLimit();
        for (MetaDataFetchClient metaDataFetchClient : getMetaDataFetchClients()) {
            try {
                metaDataFetchClient.rateLimit();
                Track track = metaDataFetchClient.searchTrack(trackName, artistName);
                if (track != null) {
                    return track;
                }
            }catch (Exception e) {
                log.warn(e.getMessage());
            }

        }
        return null;
    }

    @Nullable
    @Override
    public Album searchAlbum(String albumTitle, String artistName) {
        this.rateLimit();
        for (MetaDataFetchClient metaDataFetchClient : getMetaDataFetchClients()) {
            try {
                metaDataFetchClient.rateLimit();
                Album album = metaDataFetchClient.searchAlbum(albumTitle, artistName);
                if (album != null) {
                    return album;
                }
            }catch (Exception e) {
                log.warn(e.getMessage());
            }

        }
        return null;
    }

    @Nullable
    @Override
    public List<String> topTrack(String artistName, Integer limit)  {
        this.rateLimit();
        for (MetaDataFetchClient metaDataFetchClient : getMetaDataFetchClients()) {
            try {
                metaDataFetchClient.rateLimit();
                List<String> tracks = metaDataFetchClient.topTrack(artistName, limit);
                if (CollectionUtils.isNotEmpty(tracks)) {
                    return tracks;
                }
            }catch (Exception e) {
                log.warn(e.getMessage());
            }

        }
        return null;
    }


    public static void main(String[] args) {

        Field[] fields = ArtistInfo.class.getDeclaredFields();

        System.out.println(fields[0]);
        System.out.println(fields[1]);
        System.out.println(fields[2]);
        System.out.println(fields[3]);
        System.out.println(fields[4]);
        System.out.println(fields[5]);
        System.out.println(fields.length);
    }
}
