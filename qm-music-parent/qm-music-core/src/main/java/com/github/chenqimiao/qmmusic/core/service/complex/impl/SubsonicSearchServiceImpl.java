package com.github.chenqimiao.qmmusic.core.service.complex.impl;

import com.github.chenqimiao.qmmusic.core.dto.AlbumDTO;
import com.github.chenqimiao.qmmusic.core.dto.ArtistDTO;
import com.github.chenqimiao.qmmusic.core.dto.ComplexSongDTO;
import com.github.chenqimiao.qmmusic.core.dto.SearchResultDTO;
import com.github.chenqimiao.qmmusic.core.request.CommonSearchRequest;
import com.github.chenqimiao.qmmusic.core.service.AlbumService;
import com.github.chenqimiao.qmmusic.core.service.ArtistService;
import com.github.chenqimiao.qmmusic.core.service.SongService;
import com.github.chenqimiao.qmmusic.core.service.complex.AlbumComplexService;
import com.github.chenqimiao.qmmusic.core.service.complex.SearchService;
import com.github.chenqimiao.qmmusic.core.service.complex.SongComplexService;
import com.github.chenqimiao.qmmusic.core.util.TransliteratorUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private SongService songService;


    @Autowired
    private AlbumComplexService albumComplexService;


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

        boolean searchedArtist = false;

        List<AlbumDTO> albums = new ArrayList<>();

        boolean searchedAlbum = false;


        Set<Long> songIds = new HashSet<>();

        String reversedQuery = TransliteratorUtils.reverseSimpleTraditional(query);;
        boolean retryReverse = StringUtils.isNotBlank(query) &&
                !Objects.equals(reversedQuery, query);
        SearchResultDTO searchResultDTO = new SearchResultDTO();
        if (artistCount != null && artistCount > 0) {
            artists = this.searchArtists(query, artistCount, artistOffset, retryReverse, reversedQuery);
            searchedArtist = true;
            searchResultDTO.setArtists(artists);

        }
        if (albumCount != null && albumCount > 0) {
            searchedAlbum = true;
            albums = this.searchAlbums(query, albumCount, albumOffset, retryReverse, reversedQuery);
            if (CollectionUtils.size(albums) < albumCount) {
                // retry
                if (!searchedArtist) {
                    artists = this.searchArtists(query, NumberUtils.INTEGER_ONE, NumberUtils.INTEGER_ZERO, retryReverse, reversedQuery);
                    searchedArtist = true;
                }
                if (CollectionUtils.isNotEmpty(artists)) {
                    List<AlbumDTO> albumList = albumComplexService.searchAlbumByArtist(artists.getFirst().getId());
                    if (CollectionUtils.isNotEmpty(albumList)) {
                        albums = Stream.concat(albums.stream(), albumList.stream())
                                .collect(Collectors.toMap(
                                        AlbumDTO::getId, // 以id作为去重依据
                                        Function.identity(),
                                        (existing, replacement) -> existing, // 遇到重复id时保留已存在的元素
                                        LinkedHashMap::new // 使用LinkedHashMap保持插入顺序
                                ))
                                .values()
                                .stream()
                                .toList();
                    }
                }
            }
            searchResultDTO.setAlbums(albums);
        }
        if (songCount != null && songCount > 0) {
            songIds.addAll(songService.searchSongIdsByTitle(query, songCount
                    , songOffset));
            if (songIds.size() < songCount && retryReverse) {
                songIds.addAll(songService.searchSongIdsByTitle(reversedQuery, songCount - songIds.size(), songOffset));
            }
            if (songIds.size() < songCount) {
                if (!searchedArtist) {
                    artists = this.searchArtists(query, NumberUtils.INTEGER_ONE, NumberUtils.INTEGER_ZERO, retryReverse, reversedQuery);
                    searchedArtist = true;
                }
                if (CollectionUtils.isNotEmpty(artists)) {
                    songIds.addAll(songComplexService.searchSongsByArtists(null, artists));
                }
            }
            if (songIds.size() < songCount) {
                if (!searchedAlbum) {
                    albums = this.searchAlbums(query, NumberUtils.INTEGER_ONE, NumberUtils.INTEGER_ZERO, retryReverse, reversedQuery);
                }
                if (CollectionUtils.isNotEmpty(albums)) {
                    songIds.addAll(songComplexService.searchSongsByAlbums(null, albums));
                }
            }
            if (!songIds.isEmpty()) {
                List<Long> songIdList = new ArrayList<>(songIds);
                if (CollectionUtils.size(songIdList) > songCount) {
                    songIdList = Lists.partition(songIdList, songCount).getFirst();
                }
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
