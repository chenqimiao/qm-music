package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.constant.ServerConstants;
import com.github.chenqimiao.dto.AlbumDTO;
import com.github.chenqimiao.dto.ArtistDTO;
import com.github.chenqimiao.dto.ComplexSongDTO;
import com.github.chenqimiao.enums.EnumUserStarType;
import com.github.chenqimiao.request.BatchStarInfoRequest;
import com.github.chenqimiao.request.subsonic.SearchRequest;
import com.github.chenqimiao.response.subsonic.SearchResult2Response;
import com.github.chenqimiao.response.subsonic.SearchResult3Response;
import com.github.chenqimiao.service.AlbumService;
import com.github.chenqimiao.service.ArtistService;
import com.github.chenqimiao.service.SongService;
import com.github.chenqimiao.service.UserStarService;
import com.github.chenqimiao.service.complex.SongComplexService;
import com.github.chenqimiao.util.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/4/3 09:51
 **/
@RestController
@RequestMapping(value = "/rest")
public class SearchController {

    @Autowired
    private AlbumService albumService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SongService songService;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private UserStarService userStarService;

    @Autowired
    private SongComplexService songComplexService;


    public static Type TYPE_LIST_ALBUM_2 = new TypeToken<List<SearchResult2Response.Album>>() {}.getType();

    public static Type TYPE_LIST_ARTIST_2 = new TypeToken<List<SearchResult2Response.ArtistItem>>() {}.getType();

    public static Type TYPE_LIST_SONG_2 = new TypeToken<List<SearchResult2Response.Song>>() {}.getType();

    @GetMapping("/search2")
    public SearchResult2Response search2(SearchRequest searchRequest, HttpServletRequest servletRequest) {

        searchRequest.setQuery(searchRequest.getQuery().replace("\"",""));
        SearchResult2Response.SearchResult2.SearchResult2Builder builder = SearchResult2Response.SearchResult2.builder();

        if (searchRequest.getArtistCount() != null && searchRequest.getArtistCount() > 0 ) {
            List<ArtistDTO> artists = artistService.searchByName(searchRequest.getQuery(), searchRequest.getArtistCount()
                    , searchRequest.getArtistOffset());
            builder.artists(modelMapper.map(artists, TYPE_LIST_ARTIST_2));
        }

        if (searchRequest.getAlbumCount() != null && searchRequest.getAlbumCount() > 0 ) {
            List<AlbumDTO> albums = albumService.searchByName(searchRequest.getQuery(), searchRequest.getAlbumCount()
                    , searchRequest.getAlbumOffset());
            builder.albums(modelMapper.map(albums, TYPE_LIST_ALBUM_2));
        }

        Long authedUserId = WebUtils.currentUserId(servletRequest);

        if (searchRequest.getSongCount() != null && searchRequest.getSongCount() > 0 ) {

            List<Long> songIds = songService.searchSongIdsByTitle(searchRequest.getQuery(), searchRequest.getSongCount()
                    , searchRequest.getAlbumOffset());
            List<ComplexSongDTO> complexSongs = songComplexService.queryBySongIds(songIds, authedUserId);
            List<SearchResult2Response.Song> songList = modelMapper.map(complexSongs, TYPE_LIST_SONG_2);
            builder.songs(songList);
        }

        SearchResult2Response.SearchResult2 searchResult2 = builder.build();
        this.wrapStarredTime(searchResult2, authedUserId);
        SearchResult2Response response = new SearchResult2Response();
        response.setSearchResult2(searchResult2);
        return response;

    }

    private void wrapStarredTime(SearchResult2Response.SearchResult2 searchResult2Response, Long authedUserId) {
        List<SearchResult2Response.Album> albums = searchResult2Response.getAlbums();
        List<SearchResult2Response.ArtistItem> artists = searchResult2Response.getArtists();

        if (CollectionUtils.isNotEmpty(albums)) {
            BatchStarInfoRequest batchStarInfoRequest = BatchStarInfoRequest.builder().userId(authedUserId)
                    .relationIds(albums.stream().map(SearchResult2Response.Album::getId).toList()).startType(EnumUserStarType.ALBUM).build();
            Map<Integer, Long> starredTimeMap = userStarService.batchQueryStarredTime(batchStarInfoRequest);
            albums.forEach(album -> {
                Long starredTimestamp = starredTimeMap.get(album.getId());
                album.setStarred(starredTimestamp != null ? new Date(starredTimestamp): null);
            });

        }
        if (CollectionUtils.isNotEmpty(artists)) {
            BatchStarInfoRequest batchStarInfoRequest = BatchStarInfoRequest.builder().userId(authedUserId)
                    .relationIds(artists.stream().map(SearchResult2Response.ArtistItem::getId).toList()).startType(EnumUserStarType.ARTIST).build();
            Map<Integer, Long> starredTimeMap = userStarService.batchQueryStarredTime(batchStarInfoRequest);
            artists.forEach(artistItem -> {
                Long starredTimestamp = starredTimeMap.get(artistItem.getId());
                artistItem.setStarred(starredTimestamp != null ? new Date(starredTimestamp): null);
            });
        }
    }

    public static Type TYPE_LIST_ALBUM_3 = new TypeToken<List<SearchResult3Response.Album>>() {}.getType();

    public static Type TYPE_LIST_ARTIST_3 = new TypeToken<List<SearchResult3Response.ArtistItem>>() {}.getType();

    public static Type TYPE_LIST_SONG_3 = new TypeToken<List<SearchResult3Response.Song>>() {}.getType();

    @GetMapping("/search3")
    public SearchResult3Response search3(SearchRequest searchRequest, HttpServletRequest servletRequest) {

        searchRequest.setQuery(searchRequest.getQuery().replace("\"",""));

        SearchResult3Response.SearchResult3.SearchResult3Builder builder = SearchResult3Response.SearchResult3.builder();

        Long authedUserId = WebUtils.currentUserId(servletRequest);

        if (searchRequest.getArtistCount() != null
                && searchRequest.getArtistCount() > 0 ) {
            List<ArtistDTO> artists = artistService.searchByName(searchRequest.getQuery(), searchRequest.getArtistCount()
                    , searchRequest.getArtistOffset());
            builder.artists(modelMapper.map(artists, TYPE_LIST_ARTIST_3));
        }

        if (searchRequest.getAlbumCount() != null && searchRequest.getAlbumCount() > 0 ) {
            List<AlbumDTO> albums = albumService.searchByName(searchRequest.getQuery(), searchRequest.getAlbumCount()
                    , searchRequest.getAlbumOffset());
            builder.albums(modelMapper.map(albums, TYPE_LIST_ALBUM_3));
        }

        if (searchRequest.getSongCount() != null && searchRequest.getSongCount() > 0 ) {
            List<Long> songIds = songService.searchSongIdsByTitle(searchRequest.getQuery(), searchRequest.getSongCount()
                    , searchRequest.getAlbumOffset());
            List<ComplexSongDTO> complexSongs = songComplexService.queryBySongIds(songIds, authedUserId);
            List<SearchResult3Response.Song> songList = modelMapper.map(complexSongs, TYPE_LIST_SONG_3);
            builder.songs(songList);
        }

        SearchResult3Response.SearchResult3 searchResult3 = builder.build();
        this.wrapStarredTime(searchResult3, authedUserId);
        return SearchResult3Response.builder().searchResult3(searchResult3).build();

    }

    private void wrapStarredTime(SearchResult3Response.SearchResult3 searchResult3Response, Long authedUserId) {
        List<SearchResult3Response.Album> albums = searchResult3Response.getAlbums();
        List<SearchResult3Response.ArtistItem> artists = searchResult3Response.getArtists();

        if (CollectionUtils.isNotEmpty(albums)) {
            BatchStarInfoRequest batchStarInfoRequest = BatchStarInfoRequest.builder().userId(authedUserId)
                    .relationIds(albums.stream().map(SearchResult3Response.Album::getId).toList()).startType(EnumUserStarType.ALBUM).build();
            Map<Integer, Long> starredTimeMap = userStarService.batchQueryStarredTime(batchStarInfoRequest);
            albums.forEach(album -> {
                Long starredTimestamp = starredTimeMap.get(album.getId());
                album.setStarred(starredTimestamp != null ? new Date(starredTimestamp): null);
            });

        }
        if (CollectionUtils.isNotEmpty(artists)) {
            BatchStarInfoRequest batchStarInfoRequest = BatchStarInfoRequest.builder().userId(authedUserId)
                    .relationIds(artists.stream().map(SearchResult3Response.ArtistItem::getId).toList()).startType(EnumUserStarType.ARTIST).build();
            Map<Integer, Long> starredTimeMap = userStarService.batchQueryStarredTime(batchStarInfoRequest);
            artists.forEach(artistItem -> {
                Long starredTimestamp = starredTimeMap.get(artistItem.getId());
                artistItem.setStarred(starredTimestamp != null ? new Date(starredTimestamp): null);
            });
        }
    }

}
