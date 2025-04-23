package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.DO.AlbumDO;
import com.github.chenqimiao.DO.ArtistDO;
import com.github.chenqimiao.DO.SongDO;
import com.github.chenqimiao.constant.ModelMapperTypeConstants;
import com.github.chenqimiao.dto.*;
import com.github.chenqimiao.repository.AlbumRepository;
import com.github.chenqimiao.repository.ArtistRepository;
import com.github.chenqimiao.repository.SongRepository;
import com.github.chenqimiao.request.SongSearchRequest;
import com.github.chenqimiao.service.SongService;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 15:24
 **/
@Service("subsonicSongService")
public class SubsonicSongServiceImpl implements SongService {


    @Autowired
    private SongRepository songRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Resource
    private ModelMapper ucModelMapper;

    @Override
    public AlbumAggDTO queryByAlbumId(Long albumId) {

        List<SongDO> songs = songRepository.findByAlbumId(albumId);
        AlbumDO album = albumRepository.findByAlbumId(albumId);
        AlbumDTO albumDTO = ucModelMapper.map(album, AlbumDTO.class);

        ArtistDO artistDO = artistRepository.findByArtistId(albumDTO.getArtistId());

        Map<Long, ArtistDTO> cache = new HashMap<>();
        if (artistDO != null) {
            cache.put(albumDTO.getArtistId(), ucModelMapper.map(artistDO, ArtistDTO.class));
        }

        List<SongAggDTO> songAggDTOS = songs.stream().map(n -> {
            SongAggDTO aggDTO = new SongAggDTO();
            SongDTO songDTO = ucModelMapper.map(n, SongDTO.class);
            aggDTO.setSong(songDTO);
            if (songDTO != null && songDTO.getArtistId() != null) {
                ArtistDTO artistDTO = cache.computeIfAbsent(songDTO.getArtistId(),
                        (k) -> ucModelMapper.map(artistRepository.findByArtistId(k), ArtistDTO.class));
                aggDTO.setArtistName(artistDTO.getName());
            }
            return aggDTO;
        }).collect(Collectors.toList());

        return AlbumAggDTO.builder().album(albumDTO).songs(songAggDTOS).build();

    }

    @Override
    public List<SongDTO> searchByTitle(String songTitle, Integer pageSize, Integer offset) {
        List<SongDO> songs = songRepository.searchByTitle(songTitle, pageSize, offset);
        return ucModelMapper.map(songs, ModelMapperTypeConstants.TYPE_LIST_SONG_DTO);
    }

    @Override
    public SongDTO queryBySongId(Long songId) {
        SongDO song = songRepository.findBySongId(songId);

        return song == null ? null: ucModelMapper.map(song, SongDTO.class);
    }

    @Override
    public List<SongDTO> batchQuerySongBySongIds(List<Long> songIds) {
        if (CollectionUtils.isEmpty(songIds)) {
            return new ArrayList<>();
        }
        List<SongDO> songList = songRepository.findByIds(songIds);
        return ucModelMapper.map(songList, ModelMapperTypeConstants.TYPE_LIST_SONG_DTO);
    }

    @Override
    public List<Long> searchSongIdsByTitle(String songTitle, Integer pageSize, Integer offset) {
        return songRepository.searchSongIdsByTitle(songTitle, pageSize, offset);
    }

    @Override
    public List<Long> search(SongSearchRequest searchRequest) {
        Map<String, Object> params = new HashMap<>();
        params.put("songId", searchRequest.getSongId());
        params.put("fromYear", searchRequest.getFromYear());
        params.put("toYear", searchRequest.getToYear());
        params.put("similarSongTitle", searchRequest.getSimilarSongTitle());
        params.put("offset", searchRequest.getOffset());
        params.put("pageSize", searchRequest.getPageSize());
        params.put("similarGenre", searchRequest.getSimilarGenre());
        params.put("isRandom", Boolean.TRUE.equals(searchRequest.getIsRandom()));
        params.put("genre", searchRequest.getGenre());
        return songRepository.search(params);
    }
}
