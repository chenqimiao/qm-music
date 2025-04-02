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
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Override
    public List<ArtistDTO> searchByName(String artistName, Integer pageSize, Integer offset) {
        List<ArtistDO> artistDOS = artistRepository.searchByName(artistName, pageSize, offset);
        return ucModelMapper.map(artistDOS, ModelMapperTypeConfig.TYPE_LIST_ARTIST_DTO);
    }

    @Override
    public Map<String, List<ArtistDTO>> queryAllArtistGroupByFirstLetter(Long musicFolderId) {
        List<ArtistDO> artistList = artistRepository.findAll();
        if (CollectionUtils.isEmpty(artistList)) {
            return Collections.EMPTY_MAP;
        }
        List<ArtistDTO> artists = ucModelMapper.map(artistList, ModelMapperTypeConfig.TYPE_LIST_ARTIST_DTO);
        return artists.stream().collect(Collectors.groupingBy(ArtistDTO::getFirstLetter, TreeMap::new, Collectors.toList()));
    }

    @Override
    public List<ArtistDTO> batchQueryArtist(List<Integer> artistIds) {
        if (CollectionUtils.isEmpty(artistIds)) {
            return new ArrayList<>();
        }
        List<ArtistDO> artistList = artistRepository.findByIds(artistIds);
        return ucModelMapper.map(artistList, ModelMapperTypeConfig.TYPE_LIST_ARTIST_DTO);
    }
}
