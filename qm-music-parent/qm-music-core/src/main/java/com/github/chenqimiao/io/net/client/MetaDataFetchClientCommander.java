package com.github.chenqimiao.io.net.client;

import com.github.chenqimiao.io.net.config.MetaDataFetchClientConfig;
import com.github.chenqimiao.io.net.model.ArtistInfo;
import com.github.chenqimiao.util.TimeZoneUtils;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Random;
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
        for (MetaDataFetchClient metaDataFetchClient : getMetaDataFetchClients()) {
            try {
                ArtistInfo artistInfo = metaDataFetchClient.fetchArtistInfo(artistName);
                if (artistInfo != null) {
                    return artistInfo;
                }
            }catch (Exception e) {
                log.warn(e.getMessage());
            }

        }
        return null;
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
                log.warn(e.getMessage());
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
                log.warn(e.getMessage());
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
}
