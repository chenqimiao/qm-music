package com.github.chenqimiao.service.complex.impl;

import com.github.chenqimiao.dto.AlbumDTO;
import com.github.chenqimiao.dto.ArtistDTO;
import com.github.chenqimiao.dto.ComplexSongDTO;
import com.github.chenqimiao.dto.SearchResultDTO;
import com.github.chenqimiao.repository.ArtistRelationRepository;
import com.github.chenqimiao.repository.SongRepository;
import com.github.chenqimiao.request.CommonSearchRequest;
import com.github.chenqimiao.service.AlbumService;
import com.github.chenqimiao.service.ArtistService;
import com.github.chenqimiao.service.SongService;
import com.github.chenqimiao.service.complex.SearchService;
import com.github.chenqimiao.service.complex.SongComplexService;
import com.github.chenqimiao.util.TransliteratorUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Qimiao Chen
 * @since 2025/4/10 21:39
 **/
@Service
public class SubsonicSearchServiceImpl implements SearchService {

    @Autowired
    private ArtistService artistService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private SongComplexService songComplexService;

    @Autowired
    private ArtistRelationRepository artistRelationRepository;

    @Autowired
    private SongService songService;

    @Autowired
    private SongRepository songRepository;


    @Override
    public SearchResultDTO search(CommonSearchRequest request) {

        String query = StringUtils.isNotBlank(request.getQuery()) ? request.getQuery().trim() : "";

        Integer artistCount = request.getArtistCount();

        Integer artistOffset = request.getArtistOffset();

        Integer albumCount = request.getAlbumCount();

        Integer albumOffset = request.getAlbumOffset();

        Integer songCount = request.getSongCount();

        Integer songOffset = request.getSongOffset();

        Long authedUserId = request.getAuthedUserId();


        List<ArtistDTO> artists = new ArrayList<>();


        List<AlbumDTO> albums = new ArrayList<>();

        Set<Long> songIds = new HashSet<>();

        String reversedQuery = TransliteratorUtils.reverseSimpleTraditional(query);;
        boolean retryReverse = StringUtils.isNotBlank(query) &&
                !Objects.equals(reversedQuery, query);
        SearchResultDTO searchResultDTO = new SearchResultDTO();
        if (artistCount != null && artistCount > 0) {
            artists = searchArtists(query, artistCount, artistOffset, retryReverse, reversedQuery);
            searchResultDTO.setArtists(artists);

        }
        if (albumCount != null && albumCount > 0) {
            albums = searchAlbums(query, albumCount, albumOffset, retryReverse, reversedQuery);
            searchResultDTO.setAlbums(albums);
        }
        if (songCount != null && songCount > 0) {
            songIds.addAll(songService.searchSongIdsByTitle(query, songCount
                    , songOffset));
            if (songIds.size() < songCount && retryReverse) {
                songIds.addAll(songService.searchSongIdsByTitle(reversedQuery, songCount - songIds.size(), songOffset));
            }
            if (songIds.size() < songCount) {
                if (artistCount == null
                        || artistCount.equals(NumberUtils.INTEGER_ZERO)) {
                    artists = this.searchArtists(query, NumberUtils.INTEGER_ONE, NumberUtils.INTEGER_ZERO, retryReverse, reversedQuery);
                }
                if (CollectionUtils.isNotEmpty(artists)) {
                    songIds.addAll(songComplexService.searchSongsByArtists(null, artists));
                }
            }
            if (songIds.size() < songCount) {
                if (albumCount == null
                        || albumCount.equals(NumberUtils.INTEGER_ZERO)) {
                    albums = searchAlbums(query, NumberUtils.INTEGER_ONE, NumberUtils.INTEGER_ZERO, retryReverse, reversedQuery);
                }
                if (CollectionUtils.isNotEmpty(albums)) {
                    songIds.addAll(songComplexService.searchSongsByAlbums(null, albums));
                }
            }
            if (!songIds.isEmpty()) {
                List<Long> songIdList = Lists.partition(new ArrayList<>(songIds), songCount).getFirst();
                List<ComplexSongDTO> complexSongs = songComplexService.queryBySongIds(songIdList, authedUserId);
                searchResultDTO.setComplexSongs(complexSongs);
            }
        }

        return searchResultDTO;
    }

    private List<AlbumDTO> searchAlbums(String query, Integer albumCount, Integer albumOffset, boolean retryReverse, String reversedQuery) {
        List<AlbumDTO> albums;
        albums = albumService.searchByName(query, albumCount, albumOffset);
        if (albums.size() <= 0 && retryReverse) {
            albums = albumService.searchByName(reversedQuery, albumCount, albumOffset);

        }
        return albums;
    }

    private List<ArtistDTO> searchArtists(String query, Integer artistCount, Integer artistOffset, boolean retryReverse, String reversedQuery) {
        List<ArtistDTO> artists;
        artists = artistService.searchByName(query, artistCount, artistOffset);
        if (artists.size() <= 0 && retryReverse) {
            artists = artistService.searchByName(reversedQuery, artistCount, artistOffset);
        }
        return artists;
    }

}
