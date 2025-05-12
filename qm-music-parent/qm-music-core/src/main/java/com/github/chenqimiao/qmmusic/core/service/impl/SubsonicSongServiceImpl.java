package com.github.chenqimiao.qmmusic.core.service.impl;

import com.github.chenqimiao.qmmusic.core.constant.ModelMapperTypeConstants;
import com.github.chenqimiao.qmmusic.core.dto.*;
import com.github.chenqimiao.qmmusic.core.request.SongSearchRequest;
import com.github.chenqimiao.qmmusic.core.service.SongService;
import com.github.chenqimiao.qmmusic.dao.DO.AlbumDO;
import com.github.chenqimiao.qmmusic.dao.DO.ArtistDO;
import com.github.chenqimiao.qmmusic.dao.DO.SongDO;
import com.github.chenqimiao.qmmusic.dao.repository.AlbumRepository;
import com.github.chenqimiao.qmmusic.dao.repository.ArtistRepository;
import com.github.chenqimiao.qmmusic.dao.repository.SongRepository;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
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

        List<SongDTO> songs = this.queryByAlbumIdOrderByTrack(albumId);
        AlbumDO album = albumRepository.findByAlbumId(albumId);
        AlbumDTO albumDTO = ucModelMapper.map(album, AlbumDTO.class);

        ArtistDO artistDO = artistRepository.findByArtistId(albumDTO.getArtistId());

        Map<Long, ArtistDTO> cache = new HashMap<>();
        if (artistDO != null) {
            cache.put(albumDTO.getArtistId(), ucModelMapper.map(artistDO, ArtistDTO.class));
        }

        List<SongAggDTO> songAggDTOS = songs.stream().map(songDTO -> {
            SongAggDTO aggDTO = new SongAggDTO();
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
    public List<SongDTO> queryByAlbumIdOrderByTrack(Long albumId) {
        List<SongDO> songs = songRepository.findByAlbumId(albumId);
        songs.sort( (n1,n2) -> {
            String track1 = n1.getTrack();
            String track2 = n2.getTrack();
            boolean digits1 = NumberUtils.isDigits(track1);
            boolean digits2 = NumberUtils.isDigits(track2);
            if(!digits1 && digits2) {
                return NumberUtils.INTEGER_ONE;
            }
            if(digits1 && !digits2) {
                return NumberUtils.INTEGER_MINUS_ONE;
            }
            if(!digits1 && !digits2) {
                return NumberUtils.INTEGER_ZERO;
            }
            return Integer.parseInt(track1) - Integer.parseInt(track2);
        });
        return ucModelMapper.map(songs, ModelMapperTypeConstants.TYPE_LIST_SONG_DTO);
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
