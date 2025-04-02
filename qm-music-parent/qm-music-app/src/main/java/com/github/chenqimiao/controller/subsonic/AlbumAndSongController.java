package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.constant.ServerConstants;
import com.github.chenqimiao.dto.*;
import com.github.chenqimiao.enums.EnumSubsonicAuthCode;
import com.github.chenqimiao.enums.EnumUserStarType;
import com.github.chenqimiao.exception.SubsonicUnauthorizedException;
import com.github.chenqimiao.request.AlbumSearchRequest;
import com.github.chenqimiao.request.BatchStarInfoRequest;
import com.github.chenqimiao.request.subsonic.AlbumList2Request;
import com.github.chenqimiao.request.subsonic.SearchRequest;
import com.github.chenqimiao.response.subsonic.*;
import com.github.chenqimiao.service.AlbumService;
import com.github.chenqimiao.service.ArtistService;
import com.github.chenqimiao.service.SongService;
import com.github.chenqimiao.service.complex.MediaAnnotationService;
import com.github.chenqimiao.service.complex.SongComplexService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 19:19
 **/
@RestController
@RequestMapping(value = "/rest")
public class AlbumAndSongController {

    @Autowired
    private AlbumService albumService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SongService songService;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private MediaAnnotationService mediaAnnotationService;

    @Autowired
    private SongComplexService songComplexService;


    private static final Type TYPE_LIST_ALBUM = new TypeToken<List<AlbumList2Response.Album>>() {}.getType();
    private static final Type TYPE_LIST_SONG = new TypeToken<List<AlbumResponse.Song>>() {}.getType();



    @GetMapping(value = "/getAlbumList2")
    public AlbumList2Response getAlbumList2(AlbumList2Request albumList2Request) {
        String type = albumList2Request.getType();

        if ((albumList2Request.getSize() != null && albumList2Request.getSize() > 500) || type == null) {
            throw new SubsonicUnauthorizedException(EnumSubsonicAuthCode.E_0);
        }

        Integer fromYear = albumList2Request.getFromYear();
        Integer toYear = albumList2Request.getToYear();
        String sortDirection = fromYear == null || toYear == null || fromYear <= toYear ? "asc" : "desc";

        AlbumSearchRequest albumSearchRequest = AlbumSearchRequest.builder()
                                .sortColumn(StringUtils.equals(type, "byYear")? "release_year": "gmt_modify")
                                .sortDirection(sortDirection)
                                .offset(albumList2Request.getOffset() == null ? 0 : albumList2Request.getOffset())
                                .size(albumList2Request.getSize() == null ? 10 : albumList2Request.getSize())
                                .genre(albumList2Request.getGenre())
                                .fromYear(sortDirection.equals("desc") ? null: fromYear)
                                .toYear(sortDirection.equals("desc") ? null: toYear).build();


        List<AlbumDTO> albums = albumService.getAlbumList2(albumSearchRequest);
        AlbumList2Response albumList2Response = new AlbumList2Response();

        List<AlbumList2Response.Album> albumList  = modelMapper.map(albums, TYPE_LIST_ALBUM);
        if (CollectionUtils.isNotEmpty(albumList)) {
            albumList2Response.setAlbumList2(AlbumList2Response.AlbumList.builder()
                    .albums(albumList).build());
        }

        return albumList2Response;
    }

    @GetMapping(value = "/getAlbum")
    public AlbumResponse getAlbum(@RequestParam("id") Integer albumId) {
        AlbumAggDTO albumAggDTO = songService.queryByAlbumId(albumId);
        AlbumDTO albumDTO = albumAggDTO.getAlbum();
        List<SongAggDTO> songs = albumAggDTO.getSongs();
        AlbumResponse albumResponse = new AlbumResponse();
        AlbumResponse.Album album = modelMapper.map(albumDTO, AlbumResponse.Album.class);
        List<AlbumResponse.Song> songList = songs.stream().map(songAggDTO -> {
            SongDTO song = songAggDTO.getSong();
            AlbumResponse.Song s = modelMapper.map(song, AlbumResponse.Song.class);
            s.setArtistName(songAggDTO.getArtistName());
            s.setType("music");
            s.setAlbumTitle(albumDTO.getTitle());
            s.setIsDir(Boolean.FALSE);
            s.setIsVideo(false);
            return s;
        }).collect(Collectors.toList());
        album.setSongs(songList);
        albumResponse.setAlbum(album);
        return albumResponse;
    }



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

        Integer authedUserId = (Integer) servletRequest.getAttribute(ServerConstants.AUTHED_USER_ID);

        if (searchRequest.getSongCount() != null && searchRequest.getSongCount() > 0 ) {

            List<Integer> songIds = songService.searchSongIdsByTitle(searchRequest.getQuery(), searchRequest.getSongCount()
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

    private void wrapStarredTime(SearchResult2Response.SearchResult2 searchResult2Response, Integer authedUserId) {
        List<SearchResult2Response.Album> albums = searchResult2Response.getAlbums();
        List<SearchResult2Response.ArtistItem> artists = searchResult2Response.getArtists();

        if (CollectionUtils.isNotEmpty(albums)) {
            BatchStarInfoRequest batchStarInfoRequest = BatchStarInfoRequest.builder().userId(authedUserId)
                    .relationIds(albums.stream().map(SearchResult2Response.Album::getId).toList()).startType(EnumUserStarType.ALBUM).build();
            Map<Integer, Long> starredTimeMap = mediaAnnotationService.batchQueryStarredTime(batchStarInfoRequest);
            albums.forEach(album -> {
                Long starredTimestamp = starredTimeMap.get(album.getId());
                album.setStarred(starredTimestamp != null ? new Date(starredTimestamp): null);
            });

        }
        if (CollectionUtils.isNotEmpty(artists)) {
            BatchStarInfoRequest batchStarInfoRequest = BatchStarInfoRequest.builder().userId(authedUserId)
                    .relationIds(artists.stream().map(SearchResult2Response.ArtistItem::getId).toList()).startType(EnumUserStarType.ARTIST).build();
            Map<Integer, Long> starredTimeMap = mediaAnnotationService.batchQueryStarredTime(batchStarInfoRequest);
            artists.forEach(artistItem -> {
                Long starredTimestamp = starredTimeMap.get(artistItem.getId());
                artistItem.setStarred(starredTimestamp != null ? new Date(starredTimestamp): null);
            });
        }
    }



    @GetMapping("getSong")
    public SongResponse getSong(@RequestParam("id") Integer songId) {

        SongDTO songDTO = songService.queryBySongId(songId);

        SongResponse response = new SongResponse();

        response.setSong(modelMapper.map(songDTO, SongResponse.Song.class));

        return response;
    }




    public static Type TYPE_LIST_ALBUM_3 = new TypeToken<List<SearchResult3Response.Album>>() {}.getType();

    public static Type TYPE_LIST_ARTIST_3 = new TypeToken<List<SearchResult3Response.ArtistItem>>() {}.getType();

    public static Type TYPE_LIST_SONG_3 = new TypeToken<List<SearchResult3Response.Song>>() {}.getType();

    @GetMapping("/search3")
    public SearchResult3Response search3(SearchRequest searchRequest, HttpServletRequest servletRequest) {

        searchRequest.setQuery(searchRequest.getQuery().replace("\"",""));

        SearchResult3Response.SearchResult3.SearchResult3Builder builder = SearchResult3Response.SearchResult3.builder();

        Integer authedUserId = (Integer) servletRequest.getAttribute(ServerConstants.AUTHED_USER_ID);

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
            List<Integer> songIds = songService.searchSongIdsByTitle(searchRequest.getQuery(), searchRequest.getSongCount()
                    , searchRequest.getAlbumOffset());
            List<ComplexSongDTO> complexSongs = songComplexService.queryBySongIds(songIds, authedUserId);
            List<SearchResult3Response.Song> songList = modelMapper.map(complexSongs, TYPE_LIST_SONG_3);
            builder.songs(songList);
        }

        SearchResult3Response.SearchResult3 searchResult3 = builder.build();
        this.wrapStarredTime(searchResult3, authedUserId);
        SearchResult3Response response = new SearchResult3Response();
        response.setSearchResult3(searchResult3);
        return response;

    }

    private void wrapStarredTime(SearchResult3Response.SearchResult3 searchResult3Response, Integer authedUserId) {
        List<SearchResult3Response.Album> albums = searchResult3Response.getAlbums();
        List<SearchResult3Response.ArtistItem> artists = searchResult3Response.getArtists();

        if (CollectionUtils.isNotEmpty(albums)) {
            BatchStarInfoRequest batchStarInfoRequest = BatchStarInfoRequest.builder().userId(authedUserId)
                    .relationIds(albums.stream().map(SearchResult3Response.Album::getId).toList()).startType(EnumUserStarType.ALBUM).build();
            Map<Integer, Long> starredTimeMap = mediaAnnotationService.batchQueryStarredTime(batchStarInfoRequest);
            albums.forEach(album -> {
                Long starredTimestamp = starredTimeMap.get(album.getId());
                album.setStarred(starredTimestamp != null ? new Date(starredTimestamp): null);
            });

        }
        if (CollectionUtils.isNotEmpty(artists)) {
            BatchStarInfoRequest batchStarInfoRequest = BatchStarInfoRequest.builder().userId(authedUserId)
                    .relationIds(artists.stream().map(SearchResult3Response.ArtistItem::getId).toList()).startType(EnumUserStarType.ARTIST).build();
            Map<Integer, Long> starredTimeMap = mediaAnnotationService.batchQueryStarredTime(batchStarInfoRequest);
            artists.forEach(artistItem -> {
                Long starredTimestamp = starredTimeMap.get(artistItem.getId());
                artistItem.setStarred(starredTimestamp != null ? new Date(starredTimestamp): null);
            });
        }
    }

}

