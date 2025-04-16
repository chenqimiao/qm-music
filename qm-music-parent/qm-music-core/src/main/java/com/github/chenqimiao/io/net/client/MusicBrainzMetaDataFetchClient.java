package com.github.chenqimiao.io.net.client;

import com.github.chenqimiao.io.net.model.ArtistInfo;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

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
//        List<Artist> artists = MusicBrainzClient.searchArtist(artistName);
//
//        return CollectionUtils.isNotEmpty(artists) ? artists.getFirst().id() : null;
        return null;
    }
}
