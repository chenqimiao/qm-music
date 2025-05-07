package com.github.chenqimiao.core.service.complex.impl;

import com.github.chenqimiao.DO.ArtistRelationDO;
import com.github.chenqimiao.core.dto.ArtistDTO;
import com.github.chenqimiao.core.dto.ComplexArtistDTO;
import com.github.chenqimiao.core.enums.EnumArtistRelationType;
import com.github.chenqimiao.core.enums.EnumUserStarType;
import com.github.chenqimiao.repository.*;
import com.github.chenqimiao.core.request.BatchStarInfoRequest;
import com.github.chenqimiao.core.service.ArtistService;
import com.github.chenqimiao.core.service.UserStarService;
import com.github.chenqimiao.core.service.complex.ArtistComplexService;
import jakarta.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/4/3 23:57
 **/
@Service("subsonicArtistComplexService")
public class SubsonicArtistComplexServiceImpl implements ArtistComplexService {

    @Autowired
    private ArtistService artistService;


    @Autowired
    private UserStarService userStarService;

    @Autowired
    private ArtistRelationRepository artistRelationRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private UserStarRepository userStarRepository;

    @Override
    public List<ComplexArtistDTO> queryByArtistIds(List<Long> artistIds, @Nullable Long userId) {
        List<ArtistDTO> artists = artistService.batchQueryArtistByArtistIds(artistIds);
        if (CollectionUtils.isEmpty(artists)) {
            return Collections.emptyList();
        }

        final Map<Long, Long> starredMap = new HashMap<>();
        if (userId != null) {
            BatchStarInfoRequest batchStarInfoRequest = BatchStarInfoRequest
                    .builder()
                    .userId(userId)
                    .startType(EnumUserStarType.ARTIST)
                    .relationIds(artistIds)
                    .build();
            starredMap.putAll(userStarService.batchQueryStarredTime(batchStarInfoRequest));
        }

        Map<Long, Integer> songRelationMap = artistRelationRepository.countByArtistIdsAndType(artistIds, EnumArtistRelationType.SONG.getCode());

        Map<Long, Integer> albumRelationMap = artistRelationRepository.countByArtistIdsAndType(artistIds, EnumArtistRelationType.ALBUM.getCode());

       return artists.stream().map(n -> {
            ComplexArtistDTO complexArtistDTO = modelMapper.map(n, ComplexArtistDTO.class);
            complexArtistDTO.setStarred(starredMap.get(n.getId()));
            complexArtistDTO.setIsStar(Objects.nonNull(complexArtistDTO.getStarred()));
            complexArtistDTO.setSongCount(songRelationMap.getOrDefault(n.getId(), NumberUtils.INTEGER_ZERO));
            complexArtistDTO.setAlbumCount(albumRelationMap.getOrDefault(n.getId(), NumberUtils.INTEGER_ZERO));
            return complexArtistDTO;
        }).toList();
    }

    @Override
    public void organizeArtists() {
        List<Long> artistIds = artistRepository.findAllArtistIds();
        Map<String, Object> params = new HashMap<>();
        params.put("artistIds", artistIds);
        List<ArtistRelationDO> artistRelationList = artistRelationRepository.search(params);
        Map<Long, List<ArtistRelationDO>> artistRelationMap = artistRelationList.stream().collect(Collectors.groupingBy(ArtistRelationDO::getArtist_id));
        List<Long> toRemoveArtistIds = new ArrayList<>(); // remove artist user_star
        List<Long> toBeRemoveArtistRelationIds = new ArrayList<>();

        artistRelationMap.forEach( (artistId,relations) -> {
            if (CollectionUtils.isEmpty(relations)) {
                toRemoveArtistIds.add(artistId);
            }
            List<Long> songIds = relations.stream().filter( relation ->
                    EnumArtistRelationType.SONG.getCode().equals(relation.getType())).map(ArtistRelationDO::getRelation_id).toList();
            List<Long> albumIds = relations.stream().filter( relation ->
                    EnumArtistRelationType.ALBUM.getCode().equals(relation.getType())).map(ArtistRelationDO::getRelation_id).toList();

            List<Long> songIdsWithDb = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(songIds)) {
                songIdsWithDb.addAll(songRepository.findSongIdsByIds(songIds));
                List<Long> diffSongIds = songIds.stream().filter(n -> {
                    return !songIdsWithDb.contains(n);
                }).toList();
                List<Long> artistRelationIds = relations.stream().filter(n -> {
                    return Objects.equals(n.getType(), EnumArtistRelationType.SONG.getCode())
                            && diffSongIds.contains(n.getRelation_id());
                }).map(ArtistRelationDO::getId).toList();
                toBeRemoveArtistRelationIds.addAll(artistRelationIds);

            }
            List<Long> albumsWithDb = new ArrayList<>();;
            if (CollectionUtils.isNotEmpty(albumIds)) {
                albumsWithDb.addAll(albumRepository.findAlbumIdsByAlbumIds(albumIds));
                List<Long> diffAlbumIds = albumIds.stream().filter( n -> {
                    return ! albumsWithDb.contains(n);
                }).toList();
                List<Long> artistRelationIds = relations.stream().filter(n -> {
                    return Objects.equals(n.getType(), EnumArtistRelationType.ALBUM.getCode())
                            && diffAlbumIds.contains(n.getRelation_id());
                }).map(ArtistRelationDO::getId).toList();
                toBeRemoveArtistRelationIds.addAll(artistRelationIds);
            }

            if (CollectionUtils.isEmpty(albumsWithDb)
                    && CollectionUtils.isEmpty(songIdsWithDb)) {
                toRemoveArtistIds.add(artistId);
            }
        });

        this.doOrganizeArtists(toRemoveArtistIds, toBeRemoveArtistRelationIds);

    }

    private void doOrganizeArtists(List<Long> toRemoveArtistIds, List<Long> toBeRemoveArtistRelationIds) {
        if (CollectionUtils.isNotEmpty(toBeRemoveArtistRelationIds)) {
            artistRelationRepository.delByIds(toBeRemoveArtistRelationIds);
        }
        if (CollectionUtils.isNotEmpty(toRemoveArtistIds)) {
            userStarRepository.delByRelationIdsAndStartType(toRemoveArtistIds,
                    EnumUserStarType.ARTIST.getCode());
            artistRepository.delByIds(toRemoveArtistIds);
        }
    }
}
