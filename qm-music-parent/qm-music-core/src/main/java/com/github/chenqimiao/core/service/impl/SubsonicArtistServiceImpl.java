package com.github.chenqimiao.core.service.impl;

import com.github.chenqimiao.core.constant.CommonConstants;
import com.github.chenqimiao.core.constant.ModelMapperTypeConstants;
import com.github.chenqimiao.core.dto.ArtistAggDTO;
import com.github.chenqimiao.core.dto.ArtistDTO;
import com.github.chenqimiao.core.enums.EnumArtistRelationType;
import com.github.chenqimiao.core.io.net.client.MetaDataFetchClientCommander;
import com.github.chenqimiao.core.io.net.model.ArtistInfo;
import com.github.chenqimiao.core.service.ArtistService;
import com.github.chenqimiao.core.util.TransliteratorUtils;
import com.github.chenqimiao.dao.DO.AlbumDO;
import com.github.chenqimiao.dao.DO.ArtistDO;
import com.github.chenqimiao.dao.DO.ArtistRelationDO;
import com.github.chenqimiao.dao.repository.AlbumRepository;
import com.github.chenqimiao.dao.repository.ArtistRelationRepository;
import com.github.chenqimiao.dao.repository.ArtistRepository;
import com.google.common.collect.Lists;
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

    @Resource
    private MetaDataFetchClientCommander metaDataFetchClientCommander;


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
        ArtistInfo artistInfo = metaDataFetchClientCommander.fetchArtistInfo(artistDTO.getName());

        if(artistInfo != null) {
            artistAggDTO.setArtistImageUrl(artistInfo.getImageUrl());
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
        List<ArtistDO> artistList = artistRepository.findAll();
        if (CollectionUtils.isEmpty(artistList)) {
            return Collections.EMPTY_MAP;
        }
        List<ArtistDTO> artists = ucModelMapper.map(artistList, ModelMapperTypeConstants.TYPE_LIST_ARTIST_DTO);
        return artists.stream().collect(Collectors.groupingBy(ArtistDTO::getFirstLetter,  () -> new TreeMap<>(
                Comparator.comparing((String s) -> Objects.equals(s, CommonConstants.UN_KNOWN_FIRST_LETTER))
                        .thenComparing(Comparator.naturalOrder())
        ), Collectors.toList()));
    }

    @Override
    public List<ArtistDTO> batchQueryArtistByArtistIds(List<Long> artistIds) {
        if (CollectionUtils.isEmpty(artistIds)) {
            return new ArrayList<>();
        }
        List<ArtistDO> artistList = artistRepository.findByIds(artistIds);
        return ucModelMapper.map(artistList, ModelMapperTypeConstants.TYPE_LIST_ARTIST_DTO);
    }

    @Override
    public List<ArtistDTO> searchByNames(List<String> artistNames) {
        if (CollectionUtils.isEmpty(artistNames)) {
            return Collections.emptyList();
        }
        List<ArtistDO> artists = artistRepository.queryByUniqueKeys(artistNames);

        Set<String> existArtistNameSet = artists.stream().map(ArtistDO::getName).collect(Collectors.toSet());

        List<String> similarArtistNames = artistNames.stream().filter(n -> !existArtistNameSet.contains(n))
                .map(TransliteratorUtils::reverseSimpleTraditional).filter(Objects::nonNull).toList();

        if (CollectionUtils.isNotEmpty(similarArtistNames)) {
            List<ArtistDO> similarArtists = artistRepository.queryByUniqueKeys(similarArtistNames);
            artists.addAll(similarArtists);
            Set<String> seen = new HashSet<>(); // FieldType 是去重字段的类型，如 String、Long
            artists = artists.stream()
                    .filter(artist -> seen.add(artist.getName())) // 替换为对应字段的 getter
                    .collect(Collectors.toList());;
        }
        return ucModelMapper.map(artists, ModelMapperTypeConstants.TYPE_LIST_ARTIST_DTO);
    }

    @Override
    public ArtistDTO queryArtistByArtistId(Long artistId) {
        List<ArtistDTO> artists = this.batchQueryArtistByArtistIds(Lists.newArrayList(artistId));
        if (CollectionUtils.isNotEmpty(artists)) {
            return artists.getFirst();
        }
        return null;
    }

}
