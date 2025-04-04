package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.dto.AlbumDTO;
import com.github.chenqimiao.dto.ComplexSongDTO;
import com.github.chenqimiao.dto.UserStarResourceDTO;
import com.github.chenqimiao.enums.EnumSubsonicAuthCode;
import com.github.chenqimiao.exception.SubsonicUnauthorizedException;
import com.github.chenqimiao.request.AlbumSearchRequest;
import com.github.chenqimiao.request.SongSearchRequest;
import com.github.chenqimiao.request.subsonic.AlbumList2Request;
import com.github.chenqimiao.request.subsonic.RandomSongsRequest;
import com.github.chenqimiao.request.subsonic.SongsByGenreRequest;
import com.github.chenqimiao.response.subsonic.*;
import com.github.chenqimiao.service.AlbumService;
import com.github.chenqimiao.service.SongService;
import com.github.chenqimiao.service.complex.MediaAnnotationService;
import com.github.chenqimiao.service.complex.SongComplexService;
import com.github.chenqimiao.util.WebUtils;
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
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 19:19
 **/
@RestController
@RequestMapping(value = "/rest")
public class AlbumSongController {

    @Autowired
    private AlbumService albumService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MediaAnnotationService mediaRetrievalService;


    @Autowired
    private SongService songService;

    @Autowired
    private SongComplexService complexSongService;

    private static final Type TYPE_LIST_ALBUM = new TypeToken<List<AlbumList2Response.Album>>() {}.getType();
    private static final Type TYPE_LIST_SONG = new TypeToken<List<AlbumResponse.Song>>() {}.getType();

    @Autowired
    private SongComplexService songComplexService;

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
                .sortColumn(StringUtils.equals(type, "byYear") ? "release_year" : "gmt_modify")
                .sortDirection(sortDirection)
                .offset(albumList2Request.getOffset() == null ? 0 : albumList2Request.getOffset())
                .size(albumList2Request.getSize() == null ? 10 : albumList2Request.getSize())
                .genre(albumList2Request.getGenre())
                .fromYear(sortDirection.equals("desc") ? null : fromYear)
                .toYear(sortDirection.equals("desc") ? null : toYear).build();


        List<AlbumDTO> albums = albumService.getAlbumList2(albumSearchRequest);
        AlbumList2Response albumList2Response = new AlbumList2Response();

        List<AlbumList2Response.Album> albumList = modelMapper.map(albums, TYPE_LIST_ALBUM);
        if (CollectionUtils.isNotEmpty(albumList)) {
            albumList2Response.setAlbumList2(AlbumList2Response.AlbumList.builder()
                    .albums(albumList).build());
        }

        return albumList2Response;
    }

    private static final Type TYPE_STAR_LIST_ALBUM = new TypeToken<List<StarredResponse.Album>>() {}.getType();
    private static final Type TYPE_STAR_LIST_SONG = new TypeToken<List<StarredResponse.Song>>() {}.getType();
    private static final Type TYPE_STAR_LIST_ARTIST = new TypeToken<List<StarredResponse.Artist>>() {}.getType();

    @GetMapping("/getStarred")
    public StarredResponse getStarred(@RequestParam(required = false) Long musicFolderId) {

        Long authedUserId = WebUtils.currentUserId();
        UserStarResourceDTO resource = mediaRetrievalService.getUserStarResourceDTO(authedUserId);

        return new StarredResponse(StarredResponse.Starred
                .builder()
                .albums(modelMapper.map(resource.getAlbums(), TYPE_STAR_LIST_ALBUM))
                .songs(modelMapper.map(resource.getSongs(), TYPE_STAR_LIST_SONG))
                .artists( modelMapper.map(resource.getArtists(), TYPE_STAR_LIST_ARTIST))
                .build());


    }

    @GetMapping("/setRating")
    public SubsonicPong setRating(String id, Integer rating) {
        // mock
        return new SubsonicPong();
    }

    private static final Type TYPE_LIST_RANDOM_SONG = new TypeToken<List<RandSongsResponse.Song>>() {}.getType();

    @GetMapping("/getRandomSongs")
    public RandSongsResponse getRandomSongs(RandomSongsRequest request) {
        SongSearchRequest searchRequest = new SongSearchRequest();
        searchRequest.setToYear(request.getToYear());
        searchRequest.setFromYear(request.getFromYear());
        searchRequest.setSimilarGenre(request.getGenre());
        searchRequest.setOffset(0);
        searchRequest.setPageSize(request.getSize());
        List<Long> songIds = songService.search(searchRequest);
        if (CollectionUtils.isEmpty(songIds)) {
            return new RandSongsResponse();
        }

        Long authedUserId = WebUtils.currentUserId();

        List<ComplexSongDTO> complexSongs = songComplexService.queryBySongIds(songIds, authedUserId);
        RandSongsResponse.RandomSongs randomSongs = RandSongsResponse.RandomSongs.builder().songs(modelMapper.map(complexSongs, TYPE_LIST_RANDOM_SONG)).build();
        return new RandSongsResponse(randomSongs);
    }

    private static final Type c = new TypeToken<List<SongsByGenreResponse.Song>>() {}.getType();


    @GetMapping("/getSongsByGenre")
    public SongsByGenreResponse getSongsByGenre(SongsByGenreRequest songsByGenreRequest) {
        String genre = songsByGenreRequest.getGenre();
        if (StringUtils.isBlank(genre)) {
            throw new SubsonicUnauthorizedException(EnumSubsonicAuthCode.E_0);
        }
        SongSearchRequest searchRequest = new SongSearchRequest();
        searchRequest.setOffset(songsByGenreRequest.getOffset());
        searchRequest.setPageSize(songsByGenreRequest.getCount());
        searchRequest.setGenre(songsByGenreRequest.getGenre());
        List<Long> songIds = songService.search(searchRequest);
        if (CollectionUtils.isEmpty(songIds)) {
            return new SongsByGenreResponse();
        }
        List<ComplexSongDTO> complexSongs = complexSongService.queryBySongIds(songIds, WebUtils.currentUserId());

        SongsByGenreResponse.SongsByGenre songsByGenre = SongsByGenreResponse.SongsByGenre.builder().songs(modelMapper.map(complexSongs, TYPE_LIST_RANDOM_SONG)).build();
        return new SongsByGenreResponse(songsByGenre);
    }

}

