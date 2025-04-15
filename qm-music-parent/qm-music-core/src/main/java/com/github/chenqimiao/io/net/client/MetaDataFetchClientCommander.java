package com.github.chenqimiao.io.net.client;

import com.alibaba.fastjson2.JSONObject;
import com.github.chenqimiao.io.net.config.MetaDataFetchClientConfig;
import com.github.chenqimiao.io.net.model.Album;
import com.github.chenqimiao.io.net.model.ArtistInfo;
import com.github.chenqimiao.io.net.model.Track;
import com.github.chenqimiao.util.TimeZoneUtils;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
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

    private static List<MetaDataFetchClient> getMetaDataFetchClients() {
        Boolean currentRegionIsChina = TimeZoneUtils.currentRegionIsChina();
        List<MetaDataFetchClient> metaDataFetchClients = MetaDataFetchClientConfig.getMetaDataFetchClients();
        List<MetaDataFetchClient> list = metaDataFetchClients.stream().filter(n -> !Boolean.TRUE.equals(currentRegionIsChina)
                || n.supportChinaRegion()).collect(Collectors.toList());
        Random random = new Random(System.currentTimeMillis());
        Collections.shuffle(list, random);
        return list;
    }

    @Nullable
    @Override
    public ArtistInfo fetchArtistInfo(String artistName) {
        List<ArtistInfo> artistInfos = getMetaDataFetchClients().stream().parallel()
                .map(n -> {
                    try{
                        return n.fetchArtistInfo(artistName);
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
        for (MetaDataFetchClient metaDataFetchClient : getMetaDataFetchClients()) {
            try {
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
        for (MetaDataFetchClient metaDataFetchClient : getMetaDataFetchClients()) {
            try {
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
        for (MetaDataFetchClient metaDataFetchClient : getMetaDataFetchClients()) {
            try {
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
        for (MetaDataFetchClient metaDataFetchClient : getMetaDataFetchClients()) {
            try {
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


    public List<String> scrapeSimilarTrack(String trackName, String artistName) {
        for (MetaDataFetchClient metaDataFetchClient : getMetaDataFetchClients()) {
            try {
                List<String> trackNames = metaDataFetchClient.scrapeSimilarTrack(trackName, artistName);
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
    public Track searchTrack(String trackName, String artistName) {
        // TODO ..
        return null;
    }

    @Nullable
    public Album searchAlbum(String albumTitle, String artistName) {

        for (MetaDataFetchClient metaDataFetchClient : getMetaDataFetchClients()) {
            try {
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
