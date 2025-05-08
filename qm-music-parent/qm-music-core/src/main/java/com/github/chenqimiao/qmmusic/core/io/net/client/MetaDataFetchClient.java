package com.github.chenqimiao.qmmusic.core.io.net.client;


import com.github.chenqimiao.qmmusic.core.config.InsecureHttpClient;
import com.github.chenqimiao.qmmusic.core.io.net.model.Album;
import com.github.chenqimiao.qmmusic.core.io.net.model.ArtistInfo;
import com.github.chenqimiao.qmmusic.core.io.net.model.Track;
import com.github.chenqimiao.qmmusic.core.util.UserAgentGenerator;
import jakarta.annotation.Nullable;

import java.net.http.HttpClient;
import java.util.Collections;
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 14:12
 **/

public interface MetaDataFetchClient {


     String USER_AGENT = UserAgentGenerator.generateUserAgent();

     HttpClient HTTP_CLIENT = InsecureHttpClient.getInstance();

     Boolean supportChinaRegion();

     @Nullable
     ArtistInfo fetchArtistInfo(String artistName);


     @Nullable
     default String getLyrics(String songName, String artistName) {
          return null;
     }

     default List<String> scrapeSimilarArtists(String artistName) {
          return Collections.emptyList();
     }

     default String getUserAgent() {

          return USER_AGENT;
     }

     default HttpClient getHttpClient() {

          return HTTP_CLIENT;
     }

     default String getMusicBrainzId(String artistName) {
          return null;
     }

     default String getLastFmUrl(String artistName){
          return null;
     }

     default List<String> scrapeSimilarTrack(String trackName, String artistName,Integer limit) {
          return Collections.emptyList();
     }

     @Nullable
     default Track searchTrack(String trackName, String artistName) {
          return null;
     }

     @Nullable
     default Album searchAlbum(String albumTitle, String artistName) {

          return null;
     }

     default List<String> topTrack(String artistName, Integer limit) {
          return null;
     }

     default void rateLimit() {

     }
}
