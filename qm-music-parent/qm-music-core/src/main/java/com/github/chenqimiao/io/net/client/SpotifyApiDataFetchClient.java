package com.github.chenqimiao.io.net.client;

import com.github.chenqimiao.constant.RateLimiterConstants;
import com.github.chenqimiao.exception.RateLimitException;
import com.github.chenqimiao.io.net.model.Album;
import com.github.chenqimiao.io.net.model.ArtistInfo;
import com.github.chenqimiao.io.net.model.Track;
import com.github.chenqimiao.third.spotify.SpotifyClient;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Image;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Qimiao Chen
 * @since 2025/4/15 17:39
 **/
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
//@ConditionalOnBean(SpotifyApi.class)
public class SpotifyApiDataFetchClient implements MetaDataFetchClient {

    @Autowired(required = false)
    private SpotifyClient spotifyClient;

    @Override
    public Boolean supportChinaRegion() {
        return Boolean.TRUE;
    }

    @Nullable
    @Override
    public ArtistInfo fetchArtistInfo(String artistName) {
        Artist artist = spotifyClient.searchArtist(artistName);
        if (artist == null) {
            return null;
        }
        ArtistInfo artistInfo = new ArtistInfo();
        artistInfo.setArtistName(artistName);
        Image[] images = artist.getImages();
        if (images != null && images.length > 0) {
            List<Image> imageList = Arrays.stream(images)
                    .sorted(Comparator.comparingInt(Image::getWidth)).toList();
            artistInfo.setSmallImageUrl(imageList.get(0).getUrl());
            artistInfo.setMediumImageUrl(imageList.get(1).getUrl());
            artistInfo.setLargeImageUrl(imageList.get(2).getUrl());
            artistInfo.setImageUrl(imageList.get(1).getUrl());
        }

        return artistInfo;
    }

    @Nullable
    public Track searchTrack(String trackName, String artistName) {

        se.michaelthelin.spotify.model_objects.specification.Track track = spotifyClient.searchTrack(trackName);
        if (track == null) {return null;}
        Track result = new Track();
        result.setTrackName(trackName);
        result.setAlbumName(track.getAlbum().getName());
        result.setArtistName(track.getAlbum().getName());

        return result;
    }

    @Nullable
    public Album searchAlbum(String albumTitle, String artistName) {
        AlbumSimplified albumSimplified = spotifyClient.searchAlbum(albumTitle);
        Image[] images = albumSimplified.getImages();
        Album album = new Album();
        album.setAlbumTitle(albumTitle);
        if (images != null && images.length > 0) {
            List<Image> imageList = Arrays.stream(images)
                    .sorted(Comparator.comparingInt(Image::getWidth)).toList();
            album.setSmallImageUrl(imageList.get(0).getUrl());
            album.setMediumImageUrl(imageList.get(1).getUrl());
            album.setLargeImageUrl(imageList.get(2).getUrl());
            album.setImageUrl(imageList.get(1).getUrl());
        }
        return album;
    }


    @Override
    public void rateLimit() {
        RateLimiter limiter = RateLimiterConstants
                .limiters.computeIfAbsent(RateLimiterConstants.SPOTIFY_API_LIMIT_KEY,
                        key -> RateLimiter.create(1));

        // 尝试获取令牌
        if (!limiter.tryAcquire(1, TimeUnit.MILLISECONDS)) {
           throw new RateLimitException();
        }

    }
}
