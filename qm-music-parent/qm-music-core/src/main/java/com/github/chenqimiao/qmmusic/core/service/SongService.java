package com.github.chenqimiao.qmmusic.core.service;

import com.github.chenqimiao.qmmusic.core.dto.AlbumAggDTO;
import com.github.chenqimiao.qmmusic.core.dto.SongDTO;
import com.github.chenqimiao.qmmusic.core.request.SongSearchRequest;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 15:24
 **/
public interface SongService {


    AlbumAggDTO queryByAlbumId(Long albumId);

    List<SongDTO> queryByAlbumIdOrderByTrack(Long albumId);

    List<SongDTO> searchByTitle(String songTitle, Integer pageSize, Integer offset);


    SongDTO queryBySongId(Long songId);

    List<SongDTO> batchQuerySongBySongIds(List<Long> songIds);


    List<Long> searchSongIdsByTitle(String songTitle, Integer pageSize, Integer offset);


    List<Long> search(SongSearchRequest searchRequest);

}
