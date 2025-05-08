package com.github.chenqimiao.qmmusic.core.service.complex;

import com.github.chenqimiao.qmmusic.core.dto.AlbumDTO;
import com.github.chenqimiao.qmmusic.core.dto.ArtistDTO;
import com.github.chenqimiao.qmmusic.core.dto.ComplexSongDTO;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 23:37
 **/
public interface SongComplexService {

    List<ComplexSongDTO> queryBySongIds(List<Long> songIds, @Nullable Long userId);

    List<ComplexSongDTO> findSimilarSongsByArtistId(Long artistId, Long count);

    List<ComplexSongDTO> findSongsByArtistId(List<Long> artistIds);

    void cleanSongs(List<Long> songIds);

    List<Long> searchSongs(String query, Integer songCount, Integer songOffset);

    List<Long> searchSongs(String query, Integer songCount, Integer songOffset,
                           @Nullable List<AlbumDTO> albums, @Nullable List<ArtistDTO> artists);

    List<Long> searchSongsByArtists(String query, @Nullable List<ArtistDTO> artists);

    List<Long> searchSongsByAlbums(String query, @Nullable List<AlbumDTO> albums) ;


    List<ComplexSongDTO> findSimilarSongs(@Nullable Long songId, @Nullable Long artistId, Long count);

    List<ComplexSongDTO> getTopSongs(String artistName, Integer count, @Nullable Long userId);

    List<Long> findSongIdsByArtistId(List<Long> artistIds);
}
