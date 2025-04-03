package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.DO.AlbumDO;
import com.github.chenqimiao.DO.ArtistDO;
import com.github.chenqimiao.DO.ArtistRelationDO;
import com.github.chenqimiao.constant.ModelMapperTypeConstants;
import com.github.chenqimiao.dto.ArtistAggDTO;
import com.github.chenqimiao.dto.ArtistDTO;
import com.github.chenqimiao.enums.EnumArtistRelationType;
import com.github.chenqimiao.repository.AlbumRepository;
import com.github.chenqimiao.repository.ArtistRelationRepository;
import com.github.chenqimiao.repository.ArtistRepository;
import com.github.chenqimiao.service.ArtistService;
import com.google.common.collect.Maps;
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

    @Resource
    private ArtistRelationRepository artistRelationRepository;


    @Override
    public List<ArtistDTO> searchArtist(Long ifModifiedSince) {

        List<ArtistDO> artistList = null;

        if (ifModifiedSince == null) {
            artistList = artistRepository.findAll();
        }else {
            artistList = artistRepository.findArtistGtUpdateTime(ifModifiedSince);
        }

        return ucModelMapper.map(artistList, ModelMapperTypeConstants.TYPE_LIST_ARTIST_DTO);
    }

    @Override
    public Map<String, List<ArtistDTO>> searchArtistMap(Long ifModifiedSince) {
        List<ArtistDTO> artists = this.searchArtist(ifModifiedSince);
        Map<String, List<ArtistDTO>> artistMap = artists.stream().collect(Collectors.groupingBy(ArtistDTO::getFirstLetter,
                TreeMap::new, Collectors.toList()));
        return artistMap;
    }

    @Override
    public ArtistAggDTO queryArtistWithAlbums(Long artistId) {
        ArtistAggDTO artistAggDTO = new ArtistAggDTO();
        ArtistDO artistDO = artistRepository.findByArtistId(artistId);
        ArtistDTO artistDTO = artistDO == null ? null : ucModelMapper.map(artistDO, ArtistDTO.class);
        if (artistDTO == null) {
            return artistAggDTO;
        }
        artistAggDTO.setArtist(artistDTO);

        Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
        params.put("artistId", artistId);
        params.put("type", EnumArtistRelationType.ALBUM.getCode());
        List<ArtistRelationDO> artistRelationList = artistRelationRepository.search(params);
        if (CollectionUtils.isNotEmpty(artistRelationList)) {
            List<AlbumDO> albumDOS =
                    albumRepository.queryByIds(artistRelationList.stream().map(ArtistRelationDO::getRelation_id).collect(Collectors.toList()));
            artistAggDTO.setAlbumList(ucModelMapper.map(albumDOS, ModelMapperTypeConstants.TYPE_LIST_ALBUM_DTO));
        }
        return artistAggDTO;
    }

    @Override
    public List<ArtistDTO> searchByName(String artistName, Integer pageSize, Integer offset) {
        List<ArtistDO> artistDOS = artistRepository.searchByName(artistName, pageSize, offset);
        return ucModelMapper.map(artistDOS, ModelMapperTypeConstants.TYPE_LIST_ARTIST_DTO);
    }

    @Override
    public Map<String, List<ArtistDTO>> queryAllArtistGroupByFirstLetter(Long musicFolderId, EnumArtistRelationType enumArtistRelationType) {
        List<ArtistDO> artistList = null;
        artistList = artistRepository.findAll();

        if (CollectionUtils.isEmpty(artistList)) {
            return Collections.EMPTY_MAP;
        }
        List<ArtistDTO> artists = ucModelMapper.map(artistList, ModelMapperTypeConstants.TYPE_LIST_ARTIST_DTO);
        return artists.stream().collect(Collectors.groupingBy(ArtistDTO::getFirstLetter, TreeMap::new, Collectors.toList()));
    }

    @Override
    public List<ArtistDTO> batchQueryArtistByArtistIds(List<Long> artistIds) {
        if (CollectionUtils.isEmpty(artistIds)) {
            return new ArrayList<>();
        }
        List<ArtistDO> artistList = artistRepository.findByIds(artistIds);
        return ucModelMapper.map(artistList, ModelMapperTypeConstants.TYPE_LIST_ARTIST_DTO);
    }

}
