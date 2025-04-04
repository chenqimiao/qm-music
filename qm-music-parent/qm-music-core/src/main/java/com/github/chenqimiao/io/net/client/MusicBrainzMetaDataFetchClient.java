package com.github.chenqimiao.io.net.client;

import com.github.chenqimiao.io.net.MusicBrainzClient;
import com.github.chenqimiao.io.net.model.Artist;
import com.github.chenqimiao.io.net.model.ArtistInfo;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 14:23
 **/
@Component
public class MusicBrainzMetaDataFetchClient implements MetaDataFetchClient {

    @Override
    public ArtistInfo fetchArtistInfo(String artistName) {
        return null;
    }

    @Override
    public Boolean supportChinaRegion() {
        return Boolean.TRUE;
    }


    @Override
    @SneakyThrows
    public String getMusicBrainzId(String artistName) {
        List<Artist> artists = MusicBrainzClient.searchArtist(artistName);

        return CollectionUtils.isNotEmpty(artists) ? artists.getFirst().id() : null;
    }
}
