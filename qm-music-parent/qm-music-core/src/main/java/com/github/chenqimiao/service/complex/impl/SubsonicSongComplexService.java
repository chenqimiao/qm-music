package com.github.chenqimiao.service.complex.impl;

import com.github.chenqimiao.DO.ArtistRelationDO;
import com.github.chenqimiao.dto.ArtistDTO;
import com.github.chenqimiao.dto.ComplexSongDTO;
import com.github.chenqimiao.dto.SongDTO;
import com.github.chenqimiao.enums.EnumArtistRelationType;
import com.github.chenqimiao.enums.EnumUserStarType;
import com.github.chenqimiao.io.net.client.MetaDataFetchClientCommander;
import com.github.chenqimiao.repository.ArtistRelationRepository;
import com.github.chenqimiao.repository.PlaylistItemRepository;
import com.github.chenqimiao.repository.SongRepository;
import com.github.chenqimiao.repository.UserStarRepository;
import com.github.chenqimiao.request.BatchStarInfoRequest;
import com.github.chenqimiao.service.*;
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

    @Autowired
    private UserStarRepository userStarRepository;

    @Autowired
    private PlaylistItemRepository playlistItemRepository;

    @Autowired
    private SongRepository songRepository;
    @Autowired
    private PlaylistService playlistService;


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
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
        params.put("relationIds", songIds);
        params.put("type", EnumArtistRelationType.SONG.getCode());
        List<ArtistRelationDO> songArtists = artistRelationRepository.search(params);
        Map<Long, List<ArtistRelationDO>> artiastMap = songArtists.stream().collect(Collectors.groupingBy(ArtistRelationDO::getRelation_id));

        List<Long> extArtistIds = new ArrayList<>();
        artiastMap.forEach((k, v) -> {
            if (CollectionUtils.size(v) > 1) {
                extArtistIds.addAll(v.stream().map(ArtistRelationDO::getArtist_id).toList());
            }
        });
        Map<Long, String> artistMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(extArtistIds)) {
            List<ArtistDTO> artists = artistService.batchQueryArtistByArtistIds(extArtistIds);
            artistMap.putAll(artists.stream().collect(Collectors.toMap(ArtistDTO::getId, ArtistDTO::getName)));
        }

        return songs.stream().map(n -> {
            ComplexSongDTO complexSongDTO = modelMapper.map(n, ComplexSongDTO.class);
            complexSongDTO.setStarred(starredTimeMap.get(n.getAlbumId()));
            complexSongDTO.setIsStar(complexSongDTO.getStarred() != null);
            List<ArtistRelationDO> artistsWithSong = artiastMap.get(n.getId());
            if (CollectionUtils.size(artistsWithSong) <= 1) {
                complexSongDTO.setArtistsName(n.getArtistName());
            }else {
                List<String> artistNameList = artistsWithSong.stream()
                        .map(a -> artistMap.get(a.getArtist_id())).collect(Collectors.toList());
                complexSongDTO.setArtistsName(String.join("&", artistNameList));
            }
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

    @Override
    public void cleanSongs(List<Long> songIds) {
        userStarRepository.delByRelationIdsAndStartType(songIds, EnumUserStarType.SONG.getCode());
        playlistService.deleteItemsBySongIds(songIds);
        songRepository.deleteByIds(songIds);
    }


}
