package com.github.chenqimiao.qmmusic.app.controller.subsonic;

import com.github.chenqimiao.qmmusic.app.constant.ServerConstants;
import com.github.chenqimiao.qmmusic.app.request.subsonic.ArtistIndexRequest;
import com.github.chenqimiao.qmmusic.app.request.subsonic.ArtistInfoRequest;
import com.github.chenqimiao.qmmusic.app.request.subsonic.ArtistsRequest;
import com.github.chenqimiao.qmmusic.app.response.subsonic.*;
import com.github.chenqimiao.qmmusic.app.util.WebUtils;
import com.github.chenqimiao.qmmusic.core.constant.RateLimiterConstants;
import com.github.chenqimiao.qmmusic.core.constant.UnknownConstant;
import com.github.chenqimiao.qmmusic.core.dto.*;
import com.github.chenqimiao.qmmusic.core.enums.EnumArtistRelationType;
import com.github.chenqimiao.qmmusic.core.enums.EnumUserStarType;
import com.github.chenqimiao.qmmusic.core.exception.ResourceDisappearException;
import com.github.chenqimiao.qmmusic.core.io.net.client.MetaDataFetchClientCommander;
import com.github.chenqimiao.qmmusic.core.io.net.model.Album;
import com.github.chenqimiao.qmmusic.core.io.net.model.ArtistInfo;
import com.github.chenqimiao.qmmusic.core.request.BatchStarInfoRequest;
import com.github.chenqimiao.qmmusic.core.service.*;
import com.github.chenqimiao.qmmusic.core.service.complex.ArtistComplexService;
import com.github.chenqimiao.qmmusic.core.service.complex.SongComplexService;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 20:42
 **/
@RestController
@RequestMapping(value = "/rest")
public class BrowsingController {

    @Autowired
    private ArtistService artistService;

    @Autowired
    private GenreService genreService;

    @Autowired
    private UserStarService userStarService;

    @Autowired
    private SongService songService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SongComplexService songComplexService;

    @Autowired
    private ArtistComplexService artistComplexService;

    @Autowired
    private MetaDataFetchClientCommander metaDataFetchClientCommander;

    @Autowired
    private AlbumService albumService;

    @RequestMapping(value = "/getMusicFolders")
    public SubsonicMusicFolder getMusicFolders() {

        return SubsonicMusicFolder.builder().musicFolders(
                Collections.singletonList(SubsonicMusicFolder.MusicFolder.builder()
                        .id(ServerConstants.FOLDER_ID).name(ServerConstants.FOLDER_NAME).build()))
                .build();
    }

    @RequestMapping(value = "/getIndexes")
    public ArtistIndexResponse getIndexes(ArtistIndexRequest artistIndexRequest) {
        Map<String, List<ArtistDTO>> artistMap = artistService.searchArtistMap(artistIndexRequest.getIfModifiedSince());
        ArtistIndexResponse artistIndexResponse = new ArtistIndexResponse();
        ArtistIndexResponse.Indexes indexes = new ArtistIndexResponse.Indexes();
        artistIndexResponse.setIndexes(indexes);
        indexes.setIgnoredArticles("The El La Los Las Le Les Os As O A");
        List<ArtistIndexResponse.Index> indexList = new ArrayList<>();
        List<Long> artistIds = artistMap.values().stream().flatMap(List::stream).map(ArtistDTO::getId).toList();
        Long authedUserId = WebUtils.currentUserId();
        BatchStarInfoRequest batchStarInfoRequest = BatchStarInfoRequest.builder()
                        .userId(authedUserId).relationIds(artistIds).startType(EnumUserStarType.ARTIST).build();
        Map<Long, Long> starredTimeMap = userStarService.batchQueryStarredTime(batchStarInfoRequest);

        artistMap.forEach((key, value) -> {
            ArtistIndexResponse.Index idx = new ArtistIndexResponse.Index();
            idx.setName(key);
            List<ArtistIndexResponse.ArtistItem> artistItems = value.stream().map(n -> {
                Long id = n.getId();
                String name = n.getName();
                ArtistIndexResponse.ArtistItem artistItem = new ArtistIndexResponse.ArtistItem();
                artistItem.setId(String.valueOf(id));
                artistItem.setName(name);
                Long starredTimestamp = starredTimeMap.get(id);
                if (starredTimestamp != null) {artistItem.setStarred(new Date(starredTimestamp));}
                return artistItem;
            }).collect(Collectors.toList());
            idx.setArtists(artistItems);
            indexList.add(idx);
        });
        if (CollectionUtils.isNotEmpty(indexList)) {
            long maxLastModified = artistMap.values().stream().flatMap(List::stream).mapToLong(ArtistDTO::getGmtModify).max().orElse(System.currentTimeMillis());
            indexes.setLastModified(maxLastModified);
            indexes.setIndexList(indexList);
        }else {
            indexes.setLastModified(artistIndexRequest.getIfModifiedSince() == null
                    ? System.currentTimeMillis(): artistIndexRequest.getIfModifiedSince());
        }
        return artistIndexResponse;
    }

    @RequestMapping(value = "/getArtists")
    public ArtistsResponse getArtists(ArtistsRequest artistsRequest) {
        Map<String, List<ArtistDTO>> artistGroup =
                artistService.queryAllArtistGroupByFirstLetter(artistsRequest.getMusicFolderId(), EnumArtistRelationType.SONG);
        ArtistsResponse artistsResponse = new ArtistsResponse();
        if (artistGroup.isEmpty()){
            return artistsResponse;
        }
        List<Long> artistIds = artistGroup.values().stream().flatMap(List::stream).map(ArtistDTO::getId).toList();
        List<ComplexArtistDTO> complexArtists = artistComplexService.queryByArtistIds(artistIds, WebUtils.currentUserId());
        Map<Long, ComplexArtistDTO> complexArtistMap = complexArtists.stream().collect(Collectors.toMap(ComplexArtistDTO::getId, n -> n));
        List<ArtistsResponse.Index> indexes = new ArrayList<>();
        artistGroup.forEach((k,v) ->{

            List<ArtistsResponse.Artist> artists = v.stream().map(n -> {
                ComplexArtistDTO complexArtistDTO = complexArtistMap.get(n.getId());
                return ArtistsResponse.Artist
                                .builder()
                                .id(String.valueOf(n.getId()))
                                .name(n.getName())
                                .coverArt(n.getCoverArt())
                        .albumCount(complexArtistDTO.getAlbumCount())
                        .songCount(complexArtistDTO.getSongCount())
                        .starred(modelMapper.map(complexArtistDTO.getStarred(), Date.class))
                        .build();

            }).filter(n -> !Objects.equals(n.getId(), UnknownConstant.UN_KNOWN_ARTIST_ID.toString())).collect(Collectors.toList());

            ArtistsResponse.Index index = ArtistsResponse.Index
                    .builder()
                    .name(k)
                    .artists(artists)
                    .build();
            indexes.add(index);

        });
        ArtistsResponse.Artists artists = ArtistsResponse.Artists
                .builder()
                .ignoredArticles("The El La Los Las Le Les")
                .indexes(indexes)
                .build();
        artistsResponse.setArtists(artists);

        return artistsResponse;
    }

    @RequestMapping(value = "/getGenres")
    public GenresResponse getGenres() {
        List<GenreStatisticsDTO> statistics = genreService.statistics();
        GenresResponse genresResponse = new GenresResponse();
        if (CollectionUtils.isEmpty(statistics)) {
            return genresResponse;
        }
        List<GenresResponse.Genre> genres = statistics.stream().map(n ->
                new GenresResponse.Genre(n.getAlbumCount(), n.getSongCount(), n.getGenreName())).toList();
        genresResponse.setGenres(new GenresResponse.Genres(genres));
        return genresResponse;
    }

    @RequestMapping("/getSong")
    public SongResponse getSong(@RequestParam("id") Long songId) {
        Long authedUserId = WebUtils.currentUserId();
        List<ComplexSongDTO> complexSongs = songComplexService.queryBySongIds(Lists.newArrayList(songId), authedUserId);


        SongResponse response = new SongResponse();
        SongResponse.Song song = modelMapper.map(complexSongs.getFirst(), SongResponse.Song.class);
        song.setArtistName(complexSongs.stream().findFirst().map(ComplexSongDTO::getArtistsName).orElse(""));
        wrapOpenSubsonic(song);
        response.setSong(song);
        return response;
    }

    private void wrapOpenSubsonic(SongResponse.Song song) {
        if (song == null) return;
        song.setDisplayArtist(song.getArtistName());
        song.setDisplayAlbumArtist(song.getArtistName());
        song.setAlbumArtists(song.getArtists());
        song.setSortName(song.getTitle());

        var artist = new SongResponse.Artist();
        artist.setId(song.getArtistId());
        artist.setName(song.getArtistName());
        song.setDisplayArtist(song.getArtistName());
        ArrayList<SongResponse.Artist> artists = Lists.newArrayList(artist);
        song.setArtists(artists);
        song.setAlbumArtists(artists);

    }

    @RequestMapping(value = "/getAlbum")
    public AlbumResponse getAlbum(@RequestParam("id") Long albumId) {
        AlbumAggDTO albumAggDTO = songService.queryByAlbumId(albumId);
        AlbumDTO albumDTO = albumAggDTO.getAlbum();
        List<SongAggDTO> songs = albumAggDTO.getSongs();
        AlbumResponse albumResponse = new AlbumResponse();
        AlbumResponse.Album album = modelMapper.map(albumDTO, AlbumResponse.Album.class);
        List<AlbumResponse.Song> songList = songs.stream().map(songAggDTO -> {
            SongDTO song = songAggDTO.getSong();
            AlbumResponse.Song s = modelMapper.map(song, AlbumResponse.Song.class);
            s.setArtistName(songAggDTO.getArtistName());
            s.setAlbumTitle(albumDTO.getTitle());
            return s;
        }).collect(Collectors.toList());
        album.setSongs(songList);
        album.setSongCount(CollectionUtils.size(songList));
        albumResponse.setAlbum(album);
        this.wrapOpenSubsonic(albumResponse);
        return albumResponse;
    }

    private void wrapOpenSubsonic(AlbumResponse albumResponse) {
        AlbumResponse.Album album = albumResponse.getAlbum();

        if (album != null) {
            var artist = new AlbumResponse.Artist();
            artist.setId(album.getArtistId());
            artist.setName(album.getArtistName());
            var albumArtists = Lists.newArrayList(artist);

            album.setSortName(album.getTitle());
            album.setDisplayArtist(album.getArtistName());
            album.setArtists(albumArtists);
        }
        if (album != null && album.getSongs() != null) {
            album.getSongs().stream().filter(n -> album.getArtistId() != null).forEach(n -> {
                n.setDisplayArtist(n.getArtistName());
                n.setDisplayAlbumArtist(n.getArtistName());
                n.setAlbumArtists(album.getArtists());
                n.setSortName(n.getTitle());
                var artist = new AlbumResponse.Artist();
                artist.setId(n.getArtistId());
                artist.setName(n.getArtistName());
                n.setDisplayArtist(album.getArtistName());
                n.setArtists( Lists.newArrayList(artist));
            });
        }
    }

    private static final Type TYPE_LIST_ARTIST_INFO2_RESPONSE_ARTIST = new TypeToken<List<ArtistInfo2Response.Artist>>() {}.getType();

    @RequestMapping(value = "/getArtistInfo2")
    public ArtistInfo2Response getArtistInfo2 (ArtistInfoRequest artistInfoRequest) {
        RateLimiter limiter = RateLimiterConstants.limiters.computeIfAbsent(RateLimiterConstants.GET_ARTIST_INFO2_BY_REMOTE_LIMIT_KEY,
                key -> RateLimiter.create(3));

        // 尝试获取令牌
        if (!limiter.tryAcquire(1, TimeUnit.MILLISECONDS)) {
            return new ArtistInfo2Response();
        }

        List<ArtistDTO> artists = artistService.batchQueryArtistByArtistIds(Lists.newArrayList(artistInfoRequest.getId()));
        if (CollectionUtils.isEmpty(artists)) {
            return new ArtistInfo2Response();
        }
        ArtistDTO artistDTO = artists.getFirst();
        ArtistInfo artistInfo = metaDataFetchClientCommander.fetchArtistInfo(artistDTO.getName());
        String musicBrainzId = null;
        // this is too slow
       // String musicBrainzId = metaDataFetchClientCommander.getMusicBrainzId(artistDTO.getName());
        String lastFmUrl = metaDataFetchClientCommander.getLastFmUrl(artistDTO.getName());
        List<String> similarArtistsName = metaDataFetchClientCommander.scrapeSimilarArtists(artistDTO.getName());
        ArtistInfo2Response.ArtistInfo2 artistInfo2 = ArtistInfo2Response.ArtistInfo2.builder()
                .biography(Optional.ofNullable(artistInfo).map(ArtistInfo::getBiography).orElse(null))
                .musicBrainzId(musicBrainzId)
                .lastFmUrl(lastFmUrl)
                .smallImageUrl(Optional.ofNullable(artistInfo).map(ArtistInfo::getSmallImageUrl).orElse(null))
                .mediumImageUrl(artistInfo != null ? StringUtils.isBlank(artistInfo.getMediumImageUrl())? artistInfo.getImageUrl():artistInfo.getMediumImageUrl() : null)
                .largeImageUrl(Optional.ofNullable(artistInfo).map(ArtistInfo::getLargeImageUrl).orElse(null))
                .build();
        if (CollectionUtils.isNotEmpty(similarArtistsName)) {
            // similarArtistsName = Lists.partition(similarArtistsName, artistInfoRequest.getCount()).getFirst();
            List<ArtistDTO> similarArtists = artistService.searchByNames(similarArtistsName);
            List<String> localArtistNames = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(similarArtists)) {
                similarArtists = Lists.partition(similarArtists, artistInfoRequest.getCount()).getFirst();
                List<Long> artistIds = similarArtists.stream().map(ArtistDTO::getId).toList();
                List<ComplexArtistDTO> complexArtists = artistComplexService.queryByArtistIds(artistIds, WebUtils.currentUserId());
                localArtistNames.addAll(complexArtists.stream().map(ComplexArtistDTO::getName).toList());
                artistInfo2.setSimilarArtists(modelMapper.map(complexArtists, TYPE_LIST_ARTIST_INFO2_RESPONSE_ARTIST));
            }

            if (Boolean.TRUE.equals(artistInfoRequest.getIncludeNotPresent())) {
                List<String> diffSimilarArtistsNames = similarArtistsName
                        .stream().filter(n -> !localArtistNames.contains(n)).toList();
                List<ArtistInfo2Response.Artist> similarArtistsNotLocal = diffSimilarArtistsNames.stream().map(n -> {
                    ArtistInfo2Response.Artist artist = new ArtistInfo2Response.Artist();
                    artist.setName(n);
                    artist.setAlbumCount(0);
                    return artist;
                }).toList();
                if (artistInfo2.getSimilarArtists() == null) {
                    artistInfo2.setSimilarArtists(similarArtistsNotLocal);
                }else  {
                    artistInfo2.getSimilarArtists().addAll(similarArtistsNotLocal);
                }
            }

        }

        return new ArtistInfo2Response(artistInfo2);
    }


    private static final Type TYPE_LIST_ARTIST_INFO_RESPONSE_ARTIST = new TypeToken<List<ArtistInfoResponse.Artist>>() {}.getType();

    @RequestMapping(value = "/getArtistInfo")
    public ArtistInfoResponse getArtistInfo (ArtistInfoRequest artistInfoRequest) {
        RateLimiter limiter = RateLimiterConstants.limiters.computeIfAbsent(RateLimiterConstants.GET_ARTIST_INFO_BY_REMOTE_LIMIT_KEY,
                key -> RateLimiter.create(3));

        // 尝试获取令牌
        if (!limiter.tryAcquire(1, TimeUnit.MILLISECONDS)) {
            return new ArtistInfoResponse();
        }

        List<ArtistDTO> artists = artistService.batchQueryArtistByArtistIds(Lists.newArrayList(artistInfoRequest.getId()));
        if (CollectionUtils.isEmpty(artists)) {
            return new ArtistInfoResponse();
        }
        ArtistDTO artistDTO = artists.getFirst();
        ArtistInfo artistInfo = metaDataFetchClientCommander.fetchArtistInfo(artistDTO.getName());
        String musicBrainzId = null;
        // this is too slow
        // String musicBrainzId = metaDataFetchClientCommander.getMusicBrainzId(artistDTO.getName());
        String lastFmUrl = metaDataFetchClientCommander.getLastFmUrl(artistDTO.getName());
        List<String> similarArtistsName = metaDataFetchClientCommander.scrapeSimilarArtists(artistDTO.getName());
        ArtistInfoResponse.ArtistInfo subArtistInfo = ArtistInfoResponse.ArtistInfo.builder()
                .biography(Optional.ofNullable(artistInfo).map(ArtistInfo::getBiography).orElse(null))
                .musicBrainzId(musicBrainzId)
                .lastFmUrl(lastFmUrl)
                .smallImageUrl(Optional.ofNullable(artistInfo).map(ArtistInfo::getSmallImageUrl).orElse(null))
                .mediumImageUrl(artistInfo != null ? StringUtils.isBlank(artistInfo.getMediumImageUrl()) ? artistInfo.getImageUrl() : artistInfo.getMediumImageUrl() : null)
                .largeImageUrl(Optional.ofNullable(artistInfo).map(ArtistInfo::getLargeImageUrl).orElse(null))
                .build();
        if (CollectionUtils.isNotEmpty(similarArtistsName)) {
            // similarArtistsName = Lists.partition(similarArtistsName, artistInfoRequest.getCount()).getFirst();
            List<ArtistDTO> similarArtists = artistService.searchByNames(similarArtistsName);
            List<String> localArtistNames = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(similarArtists)) {
                similarArtists = Lists.partition(similarArtists, artistInfoRequest.getCount()).getFirst();
                var artistIds = similarArtists.stream().map(ArtistDTO::getId).toList();
                var complexArtists = artistComplexService.queryByArtistIds(artistIds, WebUtils.currentUserId());
                localArtistNames.addAll(complexArtists.stream().map(ComplexArtistDTO::getName).toList());
                subArtistInfo.setSimilarArtists(modelMapper.map(complexArtists, TYPE_LIST_ARTIST_INFO_RESPONSE_ARTIST));
            }

            if (Boolean.TRUE.equals(artistInfoRequest.getIncludeNotPresent())) {
                var diffSimilarArtistsNames = similarArtistsName
                        .stream().filter(n -> !localArtistNames.contains(n)).toList();
                var similarArtistsNotLocal = diffSimilarArtistsNames.stream().map(n -> {
                    var artist = new ArtistInfoResponse.Artist();
                    artist.setName(n);
                    artist.setAlbumCount(0);
                    return artist;
                }).toList();
                if (subArtistInfo.getSimilarArtists() == null) {
                    
                    subArtistInfo.setSimilarArtists(similarArtistsNotLocal);
                } else {
                    subArtistInfo.getSimilarArtists().addAll(similarArtistsNotLocal);
                }
            }

        }

        return new ArtistInfoResponse(subArtistInfo);
    }
    private static final Type TYPE_LIST_TOP_SONG = new TypeToken<List<TopSongsResponse.Song>>() {}.getType();

    @RequestMapping("/getTopSongs")
    public TopSongsResponse getTopSongs(@RequestParam(name = "artist", required = true) String artistName,
                            @RequestParam(defaultValue = "50", required = false) Integer count) {

        List<ComplexSongDTO> complexSongs = songComplexService.getTopSongs(artistName, count, WebUtils.currentUserId());

        if (CollectionUtils.isEmpty(complexSongs)) {
            return new TopSongsResponse();
        }

        List<TopSongsResponse.Song> songs = modelMapper.map(complexSongs, TYPE_LIST_TOP_SONG);

        return new TopSongsResponse(new TopSongsResponse.TopSongs(songs));
    }

    @RequestMapping("/getAlbumInfo2")
    public GetAlbumInfo2Response getAlbumInfo2(@RequestParam(name = "id") Long albumId) {
        AlbumDTO albumDTO = albumService.queryAlbumByAlbumId(albumId);
        if (albumDTO == null) {
            throw new ResourceDisappearException("album id do not exit");
        }
        Album album = metaDataFetchClientCommander.searchAlbum(albumDTO.getTitle(), albumDTO.getArtistName());
        if (album == null) return new GetAlbumInfo2Response();
        return new GetAlbumInfo2Response(GetAlbumInfo2Response
                                            .AlbumInfo
                                            .builder()
                                            .smallImageUrl(album.getSmallImageUrl())
                                            .mediumImageUrl(album.getMediumImageUrl())
                                            .largeImageUrl(album.getLargeImageUrl()).build());


    }
}
