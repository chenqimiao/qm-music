package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.constant.ServerConstants;
import com.github.chenqimiao.dto.*;
import com.github.chenqimiao.enums.EnumArtistRelationType;
import com.github.chenqimiao.enums.EnumUserStarType;
import com.github.chenqimiao.request.BatchStarInfoRequest;
import com.github.chenqimiao.request.subsonic.ArtistIndexRequest;
import com.github.chenqimiao.request.subsonic.ArtistsRequest;
import com.github.chenqimiao.response.subsonic.*;
import com.github.chenqimiao.service.ArtistService;
import com.github.chenqimiao.service.GenreService;
import com.github.chenqimiao.service.SongService;
import com.github.chenqimiao.service.UserStarService;
import com.github.chenqimiao.service.complex.ArtistComplexService;
import com.github.chenqimiao.service.complex.SongComplexService;
import com.github.chenqimiao.util.WebUtils;
import com.google.common.collect.Lists;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 11:42
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

    @GetMapping(value = "/getMusicFolders")
    public SubsonicMusicFolder getMusicFolders() {

        return SubsonicMusicFolder.builder().musicFolders(
                Collections.singletonList(SubsonicMusicFolder.MusicFolder.builder()
                        .id(ServerConstants.FOLDER_ID).name(ServerConstants.FOLDER_NAME).build()))
                .build();
    }

    @GetMapping(value = "/getIndexes")
    public ArtistIndexResponse getIndexes(ArtistIndexRequest artistIndexRequest, HttpServletRequest servletRequest) {
        Map<String, List<ArtistDTO>> artistMap = artistService.searchArtistMap(artistIndexRequest.getIfModifiedSince());
        ArtistIndexResponse artistIndexResponse = new ArtistIndexResponse();
        ArtistIndexResponse.Indexes indexes = new ArtistIndexResponse.Indexes();
        artistIndexResponse.setIndexes(indexes);
        indexes.setIgnoredArticles("The El La Los Las Le Les Os As O A");
        List<ArtistIndexResponse.Index> indexList = new ArrayList<>();
        List<Long> artistIds = artistMap.values().stream().flatMap(List::stream).map(ArtistDTO::getId).toList();
        Long authedUserId = WebUtils.currentUserId(servletRequest);
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
                artistItem.setId(id);
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

    @GetMapping(value = "/getArtists")
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
                                .id(n.getId())
                                .name(n.getName())
                                .coverArt(n.getCoverArt())
                        .albumCount(complexArtistDTO.getAlbumCount())
                        .albumCount(complexArtistDTO.getAlbumCount())
                        .starred(modelMapper.map(complexArtistDTO.getStarred(), Date.class))
                        .build();

            }).collect(Collectors.toList());

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

    @GetMapping(value = "/getGenres")
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

    @GetMapping("getSong")
    public SongResponse getSong(@RequestParam("id") Long songId, HttpServletRequest servletRequest) {
        Long authedUserId = WebUtils.currentUserId(servletRequest);
        List<ComplexSongDTO> complexSongs = songComplexService.queryBySongIds(Lists.newArrayList(songId), authedUserId);


        SongResponse response = new SongResponse();

        response.setSong(modelMapper.map(complexSongs.getFirst(), SongResponse.Song.class));

        return response;
    }

    @GetMapping(value = "/getAlbum")
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
        albumResponse.setAlbum(album);
        return albumResponse;
    }

}
