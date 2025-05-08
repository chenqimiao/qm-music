package com.github.chenqimiao.qmmusic.app.controller.subsonic;

import com.github.chenqimiao.qmmusic.app.request.subsonic.SearchRequest;
import com.github.chenqimiao.qmmusic.app.response.subsonic.SearchResult2Response;
import com.github.chenqimiao.qmmusic.app.response.subsonic.SearchResult3Response;
import com.github.chenqimiao.qmmusic.app.util.WebUtils;
import com.github.chenqimiao.qmmusic.core.dto.SearchResultDTO;
import com.github.chenqimiao.qmmusic.core.enums.EnumUserStarType;
import com.github.chenqimiao.qmmusic.core.io.net.client.MetaDataFetchClientCommander;
import com.github.chenqimiao.qmmusic.core.io.net.model.ArtistInfo;
import com.github.chenqimiao.qmmusic.core.request.BatchStarInfoRequest;
import com.github.chenqimiao.qmmusic.core.request.CommonSearchRequest;
import com.github.chenqimiao.qmmusic.core.service.UserStarService;
import com.github.chenqimiao.qmmusic.core.service.complex.SearchService;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/4/3
 **/
@RestController
@RequestMapping(value = "/rest")
public class SearchController {


    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserStarService userStarService;


    public static Type TYPE_LIST_ALBUM_2 = new TypeToken<List<SearchResult2Response.Album>>() {}.getType();

    public static Type TYPE_LIST_ARTIST_2 = new TypeToken<List<SearchResult2Response.ArtistItem>>() {}.getType();

    public static Type TYPE_LIST_SONG_2 = new TypeToken<List<SearchResult2Response.Song>>() {}.getType();

    @RequestMapping("/search2")
    public SearchResult2Response search2(SearchRequest searchRequest) {

        searchRequest.setQuery(searchRequest.getQuery().replace("\"",""));
        SearchResult2Response.SearchResult2.SearchResult2Builder builder = SearchResult2Response.SearchResult2.builder();
        Long authedUserId = WebUtils.currentUserId();

        CommonSearchRequest commonSearchRequest = modelMapper.map(searchRequest, CommonSearchRequest.class);
        commonSearchRequest.setAuthedUserId(authedUserId);
        SearchResultDTO searchResultDTO = searchService.search(commonSearchRequest);


        builder.artists(modelMapper.map(searchResultDTO.getArtists(), TYPE_LIST_ARTIST_2));

        builder.albums(modelMapper.map(searchResultDTO.getAlbums(), TYPE_LIST_ALBUM_2));

        List<SearchResult2Response.Song> songList = modelMapper.map(searchResultDTO.getComplexSongs(), TYPE_LIST_SONG_2);
        builder.songs(songList);

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
                    .relationIds(albums.stream().map(n -> Long.valueOf(n.getId())).toList()).startType(EnumUserStarType.ALBUM).build();
            Map<Long, Long> starredTimeMap = userStarService.batchQueryStarredTime(batchStarInfoRequest);
            albums.forEach(album -> {
                Long starredTimestamp = starredTimeMap.get(Long.valueOf(album.getId()));
                album.setStarred(starredTimestamp != null ? new Date(starredTimestamp): null);
            });

        }
        if (CollectionUtils.isNotEmpty(artists)) {
            BatchStarInfoRequest batchStarInfoRequest = BatchStarInfoRequest.builder().userId(authedUserId)
                    .relationIds(artists.stream().map(n -> Long.valueOf(n.getId())).toList()).startType(EnumUserStarType.ARTIST).build();
            Map<Long, Long> starredTimeMap = userStarService.batchQueryStarredTime(batchStarInfoRequest);
            artists.forEach(artistItem -> {
                Long starredTimestamp = starredTimeMap.get(Long.valueOf(artistItem.getId()));
                artistItem.setStarred(starredTimestamp != null ? new Date(starredTimestamp): null);
            });
        }
    }

    public static Type TYPE_LIST_ALBUM_3 = new TypeToken<List<SearchResult3Response.Album>>() {}.getType();

    public static Type TYPE_LIST_ARTIST_3 = new TypeToken<List<SearchResult3Response.ArtistItem>>() {}.getType();

    public static Type TYPE_LIST_SONG_3 = new TypeToken<List<SearchResult3Response.Song>>() {}.getType();

    @Resource
    private MetaDataFetchClientCommander metaDataFetchClientCommander;

    @Autowired
    private SearchService searchService;

    @RequestMapping("/search3")
    public SearchResult3Response search3(SearchRequest searchRequest) {

        searchRequest.setQuery(searchRequest.getQuery().replace("\"",""));

        SearchResult3Response.SearchResult3.SearchResult3Builder builder = SearchResult3Response.SearchResult3.builder();

        Long authedUserId = WebUtils.currentUserId();
        String query = searchRequest.getQuery();

        if (query != null && query.contains(" - ")) {
            String[] split = query.split(" - ");
            if (split.length == 2) {
                searchRequest.setSongCount(4000);
                searchRequest.setAlbumCount(4000);
                SearchRequest searchRequest1 = new SearchRequest();
                SearchRequest searchRequest2 = new SearchRequest();
                modelMapper.map(searchRequest, searchRequest1);
                modelMapper.map(searchRequest, searchRequest2);
                searchRequest1.setQuery(split[0].trim());
                searchRequest2.setQuery(split[1].trim());
                SearchResult3Response searchResult3Response1 = this.search3(searchRequest1);
                SearchResult3Response searchResult3Response2 = this.search3(searchRequest2);
                return this.mergeSearchResult(searchResult3Response1, searchResult3Response2);
            }



        }

        CommonSearchRequest commonSearchRequest = modelMapper.map(searchRequest, CommonSearchRequest.class);
        commonSearchRequest.setAuthedUserId(authedUserId);
        SearchResultDTO searchResultDTO = searchService.search(commonSearchRequest);

        builder.artists(modelMapper.map(searchResultDTO.getArtists(), TYPE_LIST_ARTIST_3));
        builder.albums(modelMapper.map(searchResultDTO.getAlbums(), TYPE_LIST_ALBUM_3));
        List<SearchResult3Response.Song> songList = modelMapper.map(searchResultDTO.getComplexSongs(), TYPE_LIST_SONG_3);
        builder.songs(songList);

        SearchResult3Response.SearchResult3 searchResult3 = builder.build();
        this.wrapStarredTime(searchResult3, authedUserId);
        this.wrapArtistImgUrl(searchResult3);
        this.wrapOpenSubsonicExt(searchResult3);
        return SearchResult3Response.builder().searchResult3(searchResult3).build();

    }

    private SearchResult3Response mergeSearchResult(SearchResult3Response r1,
                                                    SearchResult3Response r2) {
        var searchResult3 = new SearchResult3Response.SearchResult3();
        var r13 = r1.getSearchResult3();
        var r23 = r2.getSearchResult3();
        if (CollectionUtils.isNotEmpty(r13.getAlbums())
                && CollectionUtils.isNotEmpty(r23.getAlbums())) {
            Set<String> albumIdSet
                    = r13.getAlbums().stream().map(SearchResult3Response.Album::getId)
                    .collect(Collectors.toSet());
            List<SearchResult3Response.Album> target
                    = r23.getAlbums().stream().filter(n -> albumIdSet.contains(n.getId())).toList();
            searchResult3.setAlbums(target);
        }
        if (CollectionUtils.isNotEmpty(r13.getArtists())
                && CollectionUtils.isNotEmpty(r23.getArtists())) {
            Set<String> artirstIdSet
                    = r13.getArtists().stream().map(SearchResult3Response.ArtistItem::getId)
                    .collect(Collectors.toSet());
            List<SearchResult3Response.ArtistItem> target
                    = r23.getArtists().stream().filter(n -> artirstIdSet.contains(n.getId())).toList();
            searchResult3.setArtists(target);

        }

        if (CollectionUtils.isNotEmpty(r13.getSongs())
                && CollectionUtils.isNotEmpty(r23.getSongs())) {
            Set<String> songIdSet
                    = r13.getSongs().stream().map(SearchResult3Response.Song::getId)
                    .collect(Collectors.toSet());
            List<SearchResult3Response.Song> target
                    = r23.getSongs().stream().filter(n -> songIdSet.contains(n.getId())).toList();
            searchResult3.setSongs(target);
        }
        return new SearchResult3Response(searchResult3);

    }

    private void wrapOpenSubsonicExt(SearchResult3Response.SearchResult3 searchResult3) {
        if (CollectionUtils.isEmpty(searchResult3.getAlbums()))  return;
        searchResult3.getAlbums().forEach(album -> {
            var artist = new SearchResult3Response.AlbumArtist();
            artist.setId(album.getArtistId());
            artist.setName(album.getArtistName());
            album.setAlbumArtists(Collections.singletonList(artist));
            album.setDisplayArtist(album.getArtistName());
        });
    }

    private void wrapArtistImgUrl(SearchResult3Response.SearchResult3 searchResult3) {
        if(CollectionUtils.isEmpty(searchResult3.getArtists()))return;
        searchResult3.getArtists().forEach(artist -> {
            ArtistInfo artistInfo = metaDataFetchClientCommander.fetchArtistInfo(artist.getName());
            if (artistInfo != null) {
                artist.setArtistImageUrl(artistInfo.getImageUrl());
            }
        });
    }

    private void wrapStarredTime(SearchResult3Response.SearchResult3 searchResult3Response, Long authedUserId) {
        List<SearchResult3Response.Album> albums = searchResult3Response.getAlbums();
        List<SearchResult3Response.ArtistItem> artists = searchResult3Response.getArtists();

        if (CollectionUtils.isNotEmpty(albums)) {
            BatchStarInfoRequest batchStarInfoRequest = BatchStarInfoRequest.builder().userId(authedUserId)
                    .relationIds(albums.stream().map(n->Long.valueOf(n.getId())).toList()).startType(EnumUserStarType.ALBUM).build();
            Map<Long, Long> starredTimeMap = userStarService.batchQueryStarredTime(batchStarInfoRequest);
            albums.forEach(album -> {
                Long starredTimestamp = starredTimeMap.get(Long.valueOf(album.getId()));
                album.setStarred(starredTimestamp != null ? new Date(starredTimestamp): null);
            });

        }
        if (CollectionUtils.isNotEmpty(artists)) {
            BatchStarInfoRequest batchStarInfoRequest = BatchStarInfoRequest.builder().userId(authedUserId)
                    .relationIds(artists.stream().map(n->Long.valueOf(n.getId())).toList()).startType(EnumUserStarType.ARTIST).build();
            Map<Long, Long> starredTimeMap = userStarService.batchQueryStarredTime(batchStarInfoRequest);
            artists.forEach(artistItem -> {
                Long starredTimestamp = starredTimeMap.get(Long.valueOf(artistItem.getId()));
                artistItem.setStarred(starredTimestamp != null ? new Date(starredTimestamp): null);
            });
        }
    }

}
