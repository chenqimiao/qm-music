package com.github.chenqimiao.service.complex.impl;

import com.github.chenqimiao.dto.ArtistDTO;
import com.github.chenqimiao.dto.ComplexArtistDTO;
import com.github.chenqimiao.enums.EnumArtistRelationType;
import com.github.chenqimiao.enums.EnumUserStarType;
import com.github.chenqimiao.repository.ArtistRelationRepository;
import com.github.chenqimiao.request.BatchStarInfoRequest;
import com.github.chenqimiao.service.ArtistService;
import com.github.chenqimiao.service.UserStarService;
import com.github.chenqimiao.service.complex.ArtistComplexService;
import jakarta.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
            complexArtistDTO.setSongCount(songRelationMap.get(n.getId()));
            complexArtistDTO.setAlbumCount(albumRelationMap.get(n.getId()));
            return complexArtistDTO;
        }).toList();
    }
}
