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
import org.apache.commons.lang3.StringUtils;
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
            artists = artistService.searchByName(query, artistCount, artistOffset);
            if (artists.size() <= 0 && retryReverse) {
                artists = artistService.searchByName(reversedQuery, artistCount, artistOffset);
            }
            searchResultDTO.setArtists(artists);

        }
        if (albumCount != null && albumCount > 0) {
            albums = albumService.searchByName(query, albumCount, albumOffset);
            if (albums.size() <= 0 && retryReverse) {
                albums = albumService.searchByName(reversedQuery, albumCount, albumOffset);

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
                songIds.addAll(songComplexService.searchSongsByArtists(null, artists)) ;
            }
            if (songIds.size() < songCount) {
                songIds.addAll(songComplexService.searchSongsByAlbums(null, albums));
            }
            if (!songIds.isEmpty()) {
                List<ComplexSongDTO> complexSongs = songComplexService.queryBySongIds(new ArrayList<>(songIds), authedUserId);
                searchResultDTO.setComplexSongs(complexSongs);
            }
        }

        return searchResultDTO;
    }

}
