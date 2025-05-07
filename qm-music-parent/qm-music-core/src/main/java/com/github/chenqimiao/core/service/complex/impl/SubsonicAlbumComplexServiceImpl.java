package com.github.chenqimiao.core.service.complex.impl;

import com.github.chenqimiao.core.constant.ModelMapperTypeConstants;
import com.github.chenqimiao.core.dto.AlbumDTO;
import com.github.chenqimiao.core.dto.PlayHistoryDTO;
import com.github.chenqimiao.core.dto.SongDTO;
import com.github.chenqimiao.core.dto.UserStarDTO;
import com.github.chenqimiao.core.enums.EnumArtistRelationType;
import com.github.chenqimiao.core.enums.EnumUserStarType;
import com.github.chenqimiao.core.request.AlbumSearchRequest;
import com.github.chenqimiao.core.service.AlbumService;
import com.github.chenqimiao.core.service.PlayHistoryService;
import com.github.chenqimiao.core.service.SongService;
import com.github.chenqimiao.core.service.UserStarService;
import com.github.chenqimiao.core.service.complex.AlbumComplexService;
import com.github.chenqimiao.core.service.complex.SongComplexService;
import com.github.chenqimiao.dao.DO.AlbumDO;
import com.github.chenqimiao.dao.DO.ArtistRelationDO;
import com.github.chenqimiao.dao.repository.AlbumRepository;
import com.github.chenqimiao.dao.repository.ArtistRelationRepository;
import com.github.chenqimiao.dao.repository.SongRepository;
import com.github.chenqimiao.dao.repository.UserStarRepository;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/4/7 16:36
 **/
@Service("subsonicAlbumComplexService")
public class SubsonicAlbumComplexServiceImpl implements AlbumComplexService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserStarRepository userStarRepository;

    @Resource
    private ModelMapper ucModelMapper;

    @Autowired
    private PlayHistoryService playHistoryService;

    @Autowired
    private SongComplexService songComplexService;

    @Autowired
    private SongService songService;
    @Autowired
    private AlbumService albumService;

    @Autowired
    private ArtistRelationRepository artistRelationRepository;
    @Autowired
    private UserStarService userStarService;


    @Override
    public void organizeAlbums() {
        List<Long> albumIds = albumRepository.queryAllAlbumId();
        if (CollectionUtils.isEmpty(albumIds)) {
            return;
        }
        Map<Long, Integer> songCountMap = songRepository.countGroupByAlbumIds(albumIds);

        List<Long> toBeCleanAlbumIds = albumIds.stream().filter(n -> {
            Integer count = songCountMap.getOrDefault(n, NumberUtils.INTEGER_ZERO);
            return count <= NumberUtils.INTEGER_ZERO;
        }).toList();

        this.doOrganizeAlbums(toBeCleanAlbumIds);

    }

    private void doOrganizeAlbums(List<Long> toBeCleanAlbumIds) {
        if (CollectionUtils.isEmpty(toBeCleanAlbumIds)) {
            return;
        }
        // 1 clean star
        userStarRepository.delByRelationIdsAndStartType(toBeCleanAlbumIds, EnumUserStarType.ALBUM.getCode());
        // 2 clean album
        albumRepository.delByIds(toBeCleanAlbumIds);
    }


    @Override
    public List<AlbumDTO> getAlbumList2(AlbumSearchRequest albumSearchRequest) {
        String type = albumSearchRequest.getType();
        if(Objects.equals("random" ,type)){
            return this.getRandomAlbumList2(albumSearchRequest);
        }else if(Objects.equals("recent" ,type)) {
            return this.getRecentAlbumList2(albumSearchRequest);
        }else if(Objects.equals("newest" ,type)) {
            return this.getNewestAlbumList2(albumSearchRequest);
        }else if(Objects.equals("frequent" ,type)) {
            return this.getFrequentAlbumList2(albumSearchRequest);
        }else if(Objects.equals("starred" ,type)) {
            return this.getStarredAlbumList2(albumSearchRequest);
        }else if(Objects.equals("alphabeticalByName" ,type)) {
            return this.getAlbumList2OrderByAlbumName(albumSearchRequest);
        }else if(Objects.equals("alphabeticalByArtist" ,type)) {
            return this.getAlbumList2OrderByArtistName(albumSearchRequest);
        }else if(Objects.equals("byGenre" ,type)) {
            return this.getAlbumList2ByGenre(albumSearchRequest);
        }else if(Objects.equals("byYear" ,type)) {
            return this.getDefaultAlbumList2(albumSearchRequest);
        }
        return this.getDefaultAlbumList2(albumSearchRequest);
    }

    private List<AlbumDTO> getAlbumList2ByGenre(AlbumSearchRequest albumSearchRequest) {

        return this.getDefaultAlbumList2(albumSearchRequest);
    }

    private List<AlbumDTO> getAlbumList2OrderByArtistName(AlbumSearchRequest albumSearchRequest) {

        albumSearchRequest.setOrderBySql("""
                                         CASE
                                            WHEN first_letter_artist_name = '#' THEN 1
                                            ELSE 0
                                         END,
                                         first_letter_artist_name
                                      """);

        return this.getDefaultAlbumList2(albumSearchRequest);
    }

    private List<AlbumDTO> getAlbumList2OrderByAlbumName(AlbumSearchRequest albumSearchRequest) {

        albumSearchRequest.setOrderBySql("""
                                        CASE
                                            WHEN first_letter_title = '#' THEN 1
                                            ELSE 0
                                         END,
                                         first_letter_title
                                    """);
        return this.getDefaultAlbumList2(albumSearchRequest);
    }

    private List<AlbumDTO> getStarredAlbumList2(AlbumSearchRequest albumSearchRequest) {
        List<UserStarDTO> userStars
                = userStarService.queryUserStarByUserIdAndType(albumSearchRequest.getUserId(), EnumUserStarType.ALBUM);

        if(CollectionUtils.isEmpty(userStars)) {
            return Collections.emptyList();
        }
        List<Long> albumIds = userStars.stream().map(UserStarDTO::getRelationId).toList();

        return albumService.batchQueryAlbumByAlbumIds(albumIds);

    }

    @Override
    public List<AlbumDTO> searchAlbumByArtist(Long artistId) {
        List<ArtistRelationDO> artistRelationList =
                artistRelationRepository.findByArtistIdAndType(artistId, EnumArtistRelationType.ALBUM.getCode());
        List<Long> albumIds = artistRelationList.stream().map(ArtistRelationDO::getRelation_id).toList();
        return albumService.batchQueryAlbumByAlbumIds(albumIds);
    }

    private List<AlbumDTO> getFrequentAlbumList2(AlbumSearchRequest albumSearchRequest) {
        List<PlayHistoryDTO> playHistoryList = playHistoryService.queryFrequentPlayHistoryList(albumSearchRequest.getUserId(),
                albumSearchRequest.getOffset(), albumSearchRequest.getSize() * 2);

        return this.getAlbumList2(playHistoryList, albumSearchRequest);
    }

    private List<AlbumDTO> getRecentAlbumList2(AlbumSearchRequest albumSearchRequest) {
        List<PlayHistoryDTO> playHistoryList = playHistoryService.queryRecentPlayHistoryList(albumSearchRequest.getUserId(),
                albumSearchRequest.getOffset(), albumSearchRequest.getSize() * 2);

        return this.getAlbumList2(playHistoryList, albumSearchRequest);

    }

    private List<AlbumDTO> getAlbumList2(List<PlayHistoryDTO> playHistoryList,  AlbumSearchRequest albumSearchRequest) {
        if (CollectionUtils.isEmpty(playHistoryList)) {
            return this.getDefaultAlbumList2(albumSearchRequest);
        }
        List<Long> songIds = playHistoryList.stream().map(PlayHistoryDTO::getSongId).distinct().toList();

        List<SongDTO> songs = songService.batchQuerySongBySongIds(songIds);

        songs.sort(Comparator.comparingInt(n -> songIds.indexOf(n.getId())));

        List<Long> albumIds = songs.stream().map(SongDTO::getAlbumId).distinct().collect(Collectors.toList());

        List<AlbumDTO> result = new ArrayList<>();

        if(CollectionUtils.size(albumIds) > albumSearchRequest.getSize()) {
            albumIds = Lists.partition(albumIds,albumSearchRequest.getSize()).getFirst();
        }

        if(CollectionUtils.size(albumIds) > NumberUtils.INTEGER_ZERO) {
            List<AlbumDTO> albums = albumService.batchQueryAlbumByAlbumIds(albumIds);
            final List<Long> finalAlbumIds = albumIds;
            albums.sort(Comparator.comparingInt(n -> finalAlbumIds.indexOf(n.getId())));
            result.addAll(albums);
        }

        if(CollectionUtils.size(result) < albumSearchRequest.getSize()) {
            albumSearchRequest.setSize(albumSearchRequest.getSize() - CollectionUtils.size(result));
            List<AlbumDTO> defaultAlbumList2 = this.getDefaultAlbumList2(albumSearchRequest);
            result.addAll(defaultAlbumList2);

        }

        // fill artistName.

        return result;
    }

    private List<AlbumDTO> getNewestAlbumList2(AlbumSearchRequest albumSearchRequest) {
        StringBuilder stringBuilder = new StringBuilder();
        if (albumSearchRequest.getFromYear() != null
                || albumSearchRequest.getToYear() != null
                || albumSearchRequest.getGenre() != null) {
            stringBuilder.append("where");
            stringBuilder.append(" 1=1 ");
            if (albumSearchRequest.getFromYear() != null) {
                stringBuilder.append(" and release_year >= ").append(albumSearchRequest.getFromYear());
            }
            if (albumSearchRequest.getToYear() != null) {
                stringBuilder.append(" and release_year <= ").append(albumSearchRequest.getToYear());
            }
            if (albumSearchRequest.getGenre() != null) {
                stringBuilder.append(" and genre = '").append(albumSearchRequest.getGenre()).append("'");
            }
        }
        stringBuilder.append(" order by ")
                .append(" gmt_create desc ");

        stringBuilder.append(" limit ").append(albumSearchRequest.getOffset()).append(",")
                .append(albumSearchRequest.getSize());


        List<AlbumDO> albumList = albumRepository.searchAlbumList(stringBuilder.toString());
        return ucModelMapper.map(albumList, ModelMapperTypeConstants.TYPE_LIST_ALBUM_DTO);
    }

    private List<AlbumDTO> getRandomAlbumList2(AlbumSearchRequest albumSearchRequest) {
        StringBuilder stringBuilder = new StringBuilder();
        if (albumSearchRequest.getFromYear() != null
                || albumSearchRequest.getToYear() != null
                || albumSearchRequest.getGenre() != null) {
            stringBuilder.append("where");
            stringBuilder.append(" 1=1 ");
            if (albumSearchRequest.getFromYear() != null) {
                stringBuilder.append(" and release_year >= ").append(albumSearchRequest.getFromYear());
            }
            if (albumSearchRequest.getToYear() != null) {
                stringBuilder.append(" and release_year <= ").append(albumSearchRequest.getToYear());
            }
            if (albumSearchRequest.getGenre() != null) {
                stringBuilder.append(" and genre = '").append(albumSearchRequest.getGenre()).append("'");
            }
        }
        stringBuilder.append(" ORDER BY RANDOM() ").append(" ");

        stringBuilder.append(" limit ").append(albumSearchRequest.getOffset()).append(",")
                .append(albumSearchRequest.getSize());


        List<AlbumDO> albumList = albumRepository.searchAlbumList(stringBuilder.toString());
        return ucModelMapper.map(albumList, ModelMapperTypeConstants.TYPE_LIST_ALBUM_DTO);
    }

    private  List<AlbumDTO> getDefaultAlbumList2(AlbumSearchRequest albumSearchRequest) {
        StringBuilder stringBuilder = new StringBuilder();
        if (albumSearchRequest.getFromYear() != null
                || albumSearchRequest.getToYear() != null
                || albumSearchRequest.getGenre() != null) {
            stringBuilder.append("where");
            stringBuilder.append(" 1=1 ");
            if (albumSearchRequest.getFromYear() != null) {
                stringBuilder.append(" and release_year >= ").append(albumSearchRequest.getFromYear());
            }
            if (albumSearchRequest.getToYear() != null) {
                stringBuilder.append(" and release_year <= ").append(albumSearchRequest.getToYear());
            }
            if (albumSearchRequest.getGenre() != null) {
                stringBuilder.append(" and genre = '").append(albumSearchRequest.getGenre()).append("'");
            }
        }
        if (albumSearchRequest.getOrderBySql() != null) {
            stringBuilder.append(" order by ")
                    .append(albumSearchRequest.getOrderBySql()).append(" ");
        }else if (albumSearchRequest.getSortColumn() != null) {
             stringBuilder.append(" order by ")
             .append(albumSearchRequest.getSortColumn()).append(" ")
                    .append(albumSearchRequest.getSortDirection());
        }
        stringBuilder.append(" limit ").append(albumSearchRequest.getOffset()).append(",")
                .append(albumSearchRequest.getSize());


        List<AlbumDO> albumList = albumRepository.searchAlbumList(stringBuilder.toString());
        return ucModelMapper.map(albumList, ModelMapperTypeConstants.TYPE_LIST_ALBUM_DTO);
    }

}
