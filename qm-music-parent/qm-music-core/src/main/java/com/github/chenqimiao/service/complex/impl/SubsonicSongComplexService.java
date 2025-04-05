package com.github.chenqimiao.service.complex.impl;

import com.github.chenqimiao.DO.ArtistRelationDO;
import com.github.chenqimiao.dto.ArtistDTO;
import com.github.chenqimiao.dto.ComplexSongDTO;
import com.github.chenqimiao.dto.SongDTO;
import com.github.chenqimiao.enums.EnumArtistRelationType;
import com.github.chenqimiao.enums.EnumUserStarType;
import com.github.chenqimiao.io.net.client.MetaDataFetchClientCommander;
import com.github.chenqimiao.repository.ArtistRelationRepository;
import com.github.chenqimiao.request.BatchStarInfoRequest;
import com.github.chenqimiao.service.AlbumService;
import com.github.chenqimiao.service.ArtistService;
import com.github.chenqimiao.service.SongService;
import com.github.chenqimiao.service.UserStarService;
import com.github.chenqimiao.service.complex.SongComplexService;
import com.github.chenqimiao.util.TransliteratorUtils;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 23:41
 **/
@Service("subsonicSongComplexService")
@Slf4j
public class SubsonicSongComplexService implements SongComplexService {

    @Autowired
    private SongService songService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserStarService userStarService;

    @Resource
    private MetaDataFetchClientCommander metaDataFetchClientCommander;
    @Autowired
    private ArtistService artistService;

    @Autowired
    private ArtistRelationRepository artistRelationRepository;

    @Override
    public List<ComplexSongDTO> queryBySongIds(List<Long> songIds, @Nullable Long userId) {
        if (CollectionUtils.isEmpty(songIds)) {
            return new ArrayList<>();
        }
        List<SongDTO> songs = songService.batchQuerySongBySongIds(songIds);

        final Map<Long, Long> starredTimeMap = new HashMap<>();
        if (userId != null) {
            BatchStarInfoRequest batchStarInfoRequest = BatchStarInfoRequest.builder().userId(userId)
                    .relationIds(songIds).startType(EnumUserStarType.SONG).build();
            starredTimeMap.putAll(userStarService.batchQueryStarredTime(batchStarInfoRequest));
        }

        return songs.stream().map(n -> {
            ComplexSongDTO complexSongDTO = modelMapper.map(n, ComplexSongDTO.class);
            complexSongDTO.setStarred(starredTimeMap.get(n.getAlbumId()));
            complexSongDTO.setIsStar(complexSongDTO.getStarred() != null);
            return complexSongDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ComplexSongDTO> findSimilarSongsByArtistId(Long artistId, Long count) {
        ArtistDTO artistDTO = artistService.queryArtistByArtistId(artistId);
        List<String> similarArtists = metaDataFetchClientCommander.scrapeSimilarArtists(artistDTO.getName());

        if (CollectionUtils.isEmpty(similarArtists)) {
            return Collections.emptyList();
        }

        List<ArtistDTO> artists= artistService.searchByNames(similarArtists);

        if (CollectionUtils.isEmpty(artists)) {
            similarArtists = similarArtists.stream().map(TransliteratorUtils::reverseSimpleTraditional).toList();
            // retry
            artists= artistService.searchByNames(similarArtists);

        }

        if (CollectionUtils.isEmpty(artists)) {
            return Collections.emptyList();
        }
       return this.findSongsByArtistId(artists.stream().map(ArtistDTO::getId).toList());
    }

    @Override
    public List<ComplexSongDTO> findSongsByArtistId(List<Long> artistIds) {

        Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
        params.put("artistIds", artistIds);
        params.put("type", EnumArtistRelationType.SONG.getCode());
        List<ArtistRelationDO> artistRelationList = artistRelationRepository.search(params);

        if (CollectionUtils.isEmpty(artistRelationList)) {
            return Collections.emptyList();
        }

        List<Long> songIds = artistRelationList.stream().map(ArtistRelationDO::getRelation_id).toList();
        return this.queryBySongIds(songIds, null);
    }


}
