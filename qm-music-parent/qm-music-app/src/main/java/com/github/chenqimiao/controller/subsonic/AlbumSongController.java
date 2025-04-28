package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.constant.CoverArtPrefixConstants;
import com.github.chenqimiao.constant.ServerConstants;
import com.github.chenqimiao.dto.AlbumDTO;
import com.github.chenqimiao.dto.ComplexSongDTO;
import com.github.chenqimiao.dto.SongDTO;
import com.github.chenqimiao.dto.UserStarResourceDTO;
import com.github.chenqimiao.enums.EnumSubsonicErrorCode;
import com.github.chenqimiao.exception.SubsonicCommonErrorException;
import com.github.chenqimiao.request.AlbumSearchRequest;
import com.github.chenqimiao.request.SongSearchRequest;
import com.github.chenqimiao.request.subsonic.AlbumList2Request;
import com.github.chenqimiao.request.subsonic.RandomSongsRequest;
import com.github.chenqimiao.request.subsonic.SongsByGenreRequest;
import com.github.chenqimiao.response.subsonic.*;
import com.github.chenqimiao.service.AlbumService;
import com.github.chenqimiao.service.SongService;
import com.github.chenqimiao.service.complex.AlbumComplexService;
import com.github.chenqimiao.service.complex.MediaAnnotationService;
import com.github.chenqimiao.service.complex.SongComplexService;
import com.github.chenqimiao.util.WebUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.ArrayList;
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

    @Autowired
    private AlbumComplexService albumComplexService;

    @GetMapping(value = "/getAlbumList2")
    public AlbumList2Response getAlbumList2(AlbumList2Request albumList2Request) {
        String type = albumList2Request.getType();

        if ((albumList2Request.getSize() != null && albumList2Request.getSize() > 500) || type == null) {
            throw new SubsonicCommonErrorException(EnumSubsonicErrorCode.E_0);
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
                .type(type)
                .userId(WebUtils.currentUserId())
                .toYear(sortDirection.equals("desc") ? null : toYear).build();


        List<AlbumDTO> albums = albumComplexService.getAlbumList2(albumSearchRequest);
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

    private static final Type TYPE_STAR2_LIST_ALBUM = new TypeToken<List<StarredResponse.Album>>() {}.getType();
    private static final Type TYPE_STAR2_LIST_SONG = new TypeToken<List<StarredResponse.Song>>() {}.getType();
    private static final Type TYPE_STAR2_LIST_ARTIST = new TypeToken<List<StarredResponse.Artist>>() {}.getType();

    @GetMapping("/getStarred2")
    public Starred2Response getStarred2(@RequestParam(required = false) Long musicFolderId) {

        Long authedUserId = WebUtils.currentUserId();
        UserStarResourceDTO resource = mediaRetrievalService.getUserStarResourceDTO(authedUserId);

        return new Starred2Response(Starred2Response.Starred2
                .builder()
                .albums(modelMapper.map(resource.getAlbums(), TYPE_STAR2_LIST_ALBUM))
                .songs(modelMapper.map(resource.getSongs(), TYPE_STAR2_LIST_SONG))
                .artists( modelMapper.map(resource.getArtists(), TYPE_STAR2_LIST_ARTIST))
                .build());


    }

    @GetMapping("/setRating")
    public SubsonicPong setRating(String id, Integer rating) {
        // mock
        return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
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
        searchRequest.setIsRandom(Boolean.TRUE);
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
            throw new SubsonicCommonErrorException(EnumSubsonicErrorCode.E_0);
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


    public static final Type TYPE_LIST_SIMILAR_SONG = new TypeToken<List<GetSimilarSongsResponse.Song>>() {}.getType();

    @RequestMapping("/getSimilarSongs")
    public GetSimilarSongsResponse getSimilarSongs(@RequestParam("id") String vagueId
            , @RequestParam(required = false, name = "count",defaultValue = "50") Long count) {

        String[] split = vagueId.split("-");

        if (split.length <= 0 ) {
            throw new SubsonicCommonErrorException(EnumSubsonicErrorCode.E_10);
        }

        long bizId = NumberUtils.toLong(split[split.length-1] , NumberUtils.LONG_ZERO);

        if (bizId <= NumberUtils.LONG_ZERO) {
            throw new SubsonicCommonErrorException(EnumSubsonicErrorCode.E_10);
        }

        List<ComplexSongDTO> similarSongs = new ArrayList<>();

        Long artistId = bizId;
        Long songId = null;
        if (vagueId.startsWith(CoverArtPrefixConstants.ALBUM_ID_PREFIX)){
            AlbumDTO albumDTO = albumService.queryAlbumByAlbumId(bizId);

            artistId = albumDTO.getArtistId();

        }else if (vagueId.startsWith(CoverArtPrefixConstants.ARTIST_ID_PREFIX)){
            // do nothing
        }else {
            SongDTO songDTO = songService.queryBySongId(bizId);
            artistId = songDTO.getArtistId();
            songId = bizId;
        }

        similarSongs = complexSongService.findSimilarSongs(songId, artistId, count);

        if (CollectionUtils.size(similarSongs) > count.intValue()) {
            similarSongs = Lists.partition(similarSongs, count.intValue()).getFirst();
        }

        List<GetSimilarSongsResponse.Song> similarSongList = modelMapper.map(similarSongs, TYPE_LIST_SIMILAR_SONG);

        return new GetSimilarSongsResponse(new GetSimilarSongsResponse.SimilarSongs(similarSongList));
    }

    private static final Type TYPE_LIST_SIMILAR2_SONG = new TypeToken<List<GetSimilarSongs2Response.Song>>() {}.getType();


    @RequestMapping("/getSimilarSongs2")
    public GetSimilarSongs2Response getSimilarSongs2(@RequestParam("id") Long artistId
            , @RequestParam(required = false, name = "count",defaultValue = "50") Long count) {
       List<ComplexSongDTO> similarSongs = complexSongService.findSimilarSongsByArtistId(artistId, count);


        List<GetSimilarSongs2Response.Song> similarSongList = modelMapper.map(similarSongs, TYPE_LIST_SIMILAR2_SONG);

        return new GetSimilarSongs2Response(new GetSimilarSongs2Response.SimilarSongs(similarSongList));
    }

}

