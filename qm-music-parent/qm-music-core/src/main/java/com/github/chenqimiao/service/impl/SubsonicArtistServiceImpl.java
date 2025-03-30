package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.DO.AlbumDO;
import com.github.chenqimiao.DO.ArtistDO;
import com.github.chenqimiao.config.ModelMapperTypeConfig;
import com.github.chenqimiao.dto.ArtistAggDTO;
import com.github.chenqimiao.dto.ArtistDTO;
import com.github.chenqimiao.repository.AlbumRepository;
import com.github.chenqimiao.repository.ArtistRepository;
import com.github.chenqimiao.service.ArtistService;
import jakarta.annotation.Resource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 15:33
 **/
@Service("subsonicArtistService")
public class SubsonicArtistServiceImpl implements ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Resource
    private ModelMapper ucModelMapper;



    @Override
    public List<ArtistDTO> searchArtist(Long ifModifiedSince) {

        List<ArtistDO> artistList = null;

        if (ifModifiedSince == null) {
            artistList = artistRepository.findAll();
        }else {
            artistList = artistRepository.findArtistGtUpdateTime(ifModifiedSince);
        }

        return ucModelMapper.map(artistList, ModelMapperTypeConfig.TYPE_LIST_ARTIST_DTO);
    }

    @Override
    public Map<String, List<ArtistDTO>> searchArtistMap(Long ifModifiedSince) {
        List<ArtistDTO> artists = this.searchArtist(ifModifiedSince);
        Map<String, List<ArtistDTO>> artistMap = artists.stream().collect(Collectors.groupingBy(ArtistDTO::getFirstLetter,
                TreeMap::new, Collectors.toList()));
        return artistMap;
    }

    @Override
    public ArtistAggDTO queryArtistWithAlbums(Integer artistId) {
        ArtistDO artistDO = artistRepository.findByArtistId(artistId);
        ArtistDTO artistDTO = artistDO == null ? null : ucModelMapper.map(artistDO, ArtistDTO.class);
        List<AlbumDO> albumDOList = albumRepository.findByArtistId(artistId);
        return ArtistAggDTO.builder()
                .artist(artistDTO)
                .albumList(ucModelMapper.map(albumDOList, ModelMapperTypeConfig.TYPE_LIST_ALBUM_DTO))
                .build();
    }
}
