package com.github.chenqimiao.core.third.spotify;

import lombok.extern.slf4j.Slf4j;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Track;

/**
 * @author Qimiao Chen
 * @since 2025/4/14 18:11
 **/
@Slf4j
public class SpotifyClient {

    private SpotifyApi spotifyApi;

    public SpotifyClient(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
    }



    public Track searchTrack(String query) {
        try {
            Track[] tracks = spotifyApi.searchTracks(query)
                    .limit(3)
                    .build()
                    .execute()
                    .getItems();
            if (tracks != null && tracks.length > 0) {
                return tracks[0];
            }
            return null;
        } catch (Exception e) {
            log.error("spotify search track error, query: {} ", query, e);
            return null;
        }
    }



    public Artist searchArtist(String query) {

        try {
            Artist[] artists = spotifyApi.searchArtists(query)
                    .limit(3)
                    .build()
                    .execute()
                    .getItems();
            if (artists != null && artists.length > 0) {
                return artists[0];
            }
            return null;
        } catch (Exception e) {
            log.error("spotify search artists error, query: {} ", query, e);
            return null;
        }
    }

    public AlbumSimplified searchAlbum(String query) {

        try {
            AlbumSimplified[] items = spotifyApi.searchAlbums(query)
                    .limit(3)
                    .build()
                    .execute()
                    .getItems();
            if (items != null && items.length > 0) {
                return items[0];
            }
            return null;
        } catch (Exception e) {
            log.error("spotify search album error, query: {} ", query, e);
            return null;
        }
    }


    /**
     * NOT FOUND ERROR
     */
    @Deprecated
    public Artist[]  getArtistsRelatedArtists(String id) {

        try {
            Artist[] artists = spotifyApi.getArtistsRelatedArtists(id)
                    .build()
                    .execute();

            return artists;
        } catch (Exception e) {
           log.error("spotify getArtistsRelatedArtists error, id: {} ", id, e);
           return null;
        }
    }

    /**
     * NOT FOUND ERROR
     */
    @Deprecated
    public Artist[]  getArtistsRelatedArtistsByName(String query) {

        Artist artist = this.searchArtist(query);
        if (artist == null) {
            return null;
        }
        String id = artist.getId();
        return this.getArtistsRelatedArtists(id);
    }

}
