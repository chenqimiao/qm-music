package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.DO.AlbumDO;
import com.github.chenqimiao.DO.ArtistDO;
import com.github.chenqimiao.DO.SongDO;
import com.github.chenqimiao.dto.*;
import com.github.chenqimiao.repository.AlbumRepository;
import com.github.chenqimiao.repository.ArtistRepository;
import com.github.chenqimiao.repository.SongRepository;
import com.github.chenqimiao.service.SongService;
import jakarta.annotation.Resource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public AlbumAggDTO queryByAlbumId(Integer albumId) {

        List<SongDO> songs = songRepository.findByAlbumId(albumId);
        AlbumDO album = albumRepository.findByAlbumById(albumId);
        AlbumDTO albumDTO = ucModelMapper.map(album, AlbumDTO.class);

        ArtistDO artistDO = artistRepository.findByArtistId(albumDTO.getArtistId());

        Map<Integer, ArtistDTO> cache = new HashMap<>();
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
}
