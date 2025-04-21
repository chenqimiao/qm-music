package com.github.chenqimiao.service.complex.impl;

import com.github.chenqimiao.DO.ArtistRelationDO;
import com.github.chenqimiao.DO.SongDO;
import com.github.chenqimiao.dto.*;
import com.github.chenqimiao.enums.EnumArtistRelationType;
import com.github.chenqimiao.enums.EnumUserStarType;
import com.github.chenqimiao.io.net.client.MetaDataFetchClientCommander;
import com.github.chenqimiao.repository.*;
import com.github.chenqimiao.request.BatchStarInfoRequest;
import com.github.chenqimiao.service.*;
import com.github.chenqimiao.service.complex.SongComplexService;
import com.github.chenqimiao.util.TransliteratorUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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

    @Autowired
    private PlayHistoryRepository playHistoryRepository;
    @Autowired
    private PlayHistoryService playHistoryService;


    @Override
    public List<ComplexSongDTO> queryBySongIds(List<Long> songIds, @Nullable Long userId) {
        if (CollectionUtils.isEmpty(songIds)) {
            return new ArrayList<>();
        }
        List<SongDTO> songs = songService.batchQuerySongBySongIds(songIds);

        final Map<Long, Long> starredTimeMap = new HashMap<>();
        final Map<Long, Integer> playCountMap = new HashMap<>();
        if (userId != null) {
            BatchStarInfoRequest batchStarInfoRequest = BatchStarInfoRequest.builder().userId(userId)
                    .relationIds(songIds).startType(EnumUserStarType.SONG).build();
            starredTimeMap.putAll(userStarService.batchQueryStarredTime(batchStarInfoRequest));
            List<PlayHistoryDTO> playHistories = playHistoryService.queryUserSpecifiedSongPlayHistoryList(userId, songIds);
            if (CollectionUtils.isNotEmpty(playHistories)) {
                Map<Long, Integer> userPlayCountMap = playHistories.stream().collect(Collectors.toMap(PlayHistoryDTO::getSongId
                        , PlayHistoryDTO::getPlayCount));
                playCountMap.putAll(userPlayCountMap);
            }
        }
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
        params.put("relationIds", songIds);
        params.put("type", EnumArtistRelationType.SONG.getCode());
        List<ArtistRelationDO> songArtists = artistRelationRepository.search(params);
        Map<Long, List<ArtistRelationDO>> artistRelationMap = songArtists.stream().collect(Collectors.groupingBy(ArtistRelationDO::getRelation_id));

        List<Long> extArtistIds = new ArrayList<>();
        artistRelationMap.forEach((songId, v) -> {
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
            complexSongDTO.setStarred(starredTimeMap.get(n.getId()));
            complexSongDTO.setIsStar(complexSongDTO.getStarred() != null);
            complexSongDTO.setPlayCount(userId != null ?
                    playCountMap.getOrDefault(n.getId(), NumberUtils.INTEGER_ZERO) : playCountMap.get(n.getId()));
            List<ArtistRelationDO> artistsWithSong = artistRelationMap.get(n.getId());
            if (CollectionUtils.size(artistsWithSong) <= 1) {
                complexSongDTO.setArtistsName(n.getArtistName());
            }else {
                List<String> artistNameList = artistsWithSong.stream()
                        .map(a -> artistMap.get(a.getArtist_id())).collect(Collectors.toList());
                complexSongDTO.setArtistsName(String.join("&", artistNameList));
                if (StringUtils.isBlank(complexSongDTO.getArtistsName())) {
                    complexSongDTO.setArtistsName(n.getArtistName());
                }
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

        return this.queryBySongIds(this.findSongIdsByArtistId(artistIds), null);
    }

    @Override
    public List<Long> findSongIdsByArtistId(List<Long> artistIds) {

        Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
        params.put("artistIds", artistIds);
        params.put("type", EnumArtistRelationType.SONG.getCode());
        List<ArtistRelationDO> artistRelationList = artistRelationRepository.search(params);

        if (CollectionUtils.isEmpty(artistRelationList)) {
            return Collections.emptyList();
        }

        return artistRelationList.stream().map(ArtistRelationDO::getRelation_id).toList();
    }


    @Override
    public void cleanSongs(List<Long> songIds) {
        userStarRepository.delByRelationIdsAndStartType(songIds, EnumUserStarType.SONG.getCode());
        playlistService.deleteItemsBySongIds(songIds);
        songRepository.deleteByIds(songIds);
        playHistoryRepository.deleteBySongIds(songIds);

    }

    @Override
    public List<Long> searchSongs(String query, Integer songCount, Integer songOffset) {
        List<Long> songIds = this.searchSongs(query, songCount, songOffset, null, null);
        if(CollectionUtils.isNotEmpty(songIds)) {
            return songIds;
        }
        if (StringUtils.isNotBlank(query)){
            String reversedQueryText = TransliteratorUtils.reverseSimpleTraditional(query);
            if (!Objects.equals(reversedQueryText, query)) {
                return this.searchSongs(reversedQueryText, songCount, songOffset, null, null);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<Long> searchSongs(String query, Integer songCount, Integer songOffset, @Nullable List<AlbumDTO> albums,
                                  @Nullable List<ArtistDTO> artists) {
        List<Long> songIds = songService.searchSongIdsByTitle(query, songCount
                , songOffset);
        if (StringUtils.isBlank(query)) {
            return songIds;
        }
        if (CollectionUtils.size(songIds) >= songCount) {
            return songIds;
        }

        songIds.addAll(this.searchSongsByArtists(query, artists));

        if (CollectionUtils.size(songIds) >= songCount) {
            return songIds;
        }

        songIds.addAll(this.searchSongsByAlbums(query, albums));

        songIds = songIds.stream().distinct().collect(Collectors.toList());
        if (CollectionUtils.size(songIds) > songCount) {
            songIds = Lists.partition(songIds, songCount).getFirst();
        }
        return songIds.stream().toList();
    }

    @Override
    public List<Long> searchSongsByArtists(@Nullable String query, @Nullable List<ArtistDTO> artists) {
        if (query == null && artists == null) {return Collections.emptyList();}
        artists =  (artists == null ? artistService.searchByName(query,1, 0) : artists);
        if (CollectionUtils.isNotEmpty(artists)) {
            List<ArtistRelationDO> relationDOList = artistRelationRepository.findByArtistIdAndType(artists.getFirst().getId(), EnumArtistRelationType.SONG.getCode());
            return relationDOList.stream().map(ArtistRelationDO::getRelation_id).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public List<Long> searchSongsByAlbums(@Nullable String query, @Nullable List<AlbumDTO> albums) {
        if (query == null && albums == null) {return Collections.emptyList();}
        albums =  (albums == null ? albumService.searchByName(query,1, 0) : albums);
        if(CollectionUtils.isNotEmpty(albums)) {
            Long albumId = albums.getFirst().getId();
            List<SongDO> songsSearchByAlbumName = songRepository.findByAlbumId(albumId);
            return songsSearchByAlbumName.stream().map(SongDO::getId).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public List<ComplexSongDTO> findSimilarSongs(Long songId, Long artistId, Long count) {
        if(songId == null && artistId == null ) {
            return Collections.emptyList();
        }
        List<ComplexSongDTO> complexSongs = new ArrayList<>();
        SongDTO song = songService.queryBySongId(songId);
        if (song != null) {
            List<String> songNames = metaDataFetchClientCommander.scrapeSimilarTrack(song.getTitle(), song.getArtistName(), count.intValue() * 2);
            if (CollectionUtils.isNotEmpty(songNames)) {
                List<String> reverseSongNames = songNames.stream().map(TransliteratorUtils::reverseSimpleTraditional).toList();
                songNames.addAll(reverseSongNames);
                Map<String, Object> params = new HashMap<>();
                params.put("titles", songNames);
                List<Long> songIds = songRepository.search(params);
                if (CollectionUtils.isNotEmpty(songIds)) {
                    complexSongs.addAll(this.queryBySongIds(songIds, null)) ;
                }
            }
        }

       if (CollectionUtils.size(complexSongs) > count) {
           return Lists.partition(complexSongs, count.intValue()).getFirst();

       }else if(CollectionUtils.size(complexSongs) == count) {
           return complexSongs;

       }else {

           List<ComplexSongDTO> similarSongsByArtistId = this.findSimilarSongsByArtistId(artistId, count - complexSongs.size());
           complexSongs.addAll(similarSongsByArtistId);
           Set<Long> seen = new HashSet<>();
           return complexSongs.stream().filter(n -> seen.add(n.getId())).collect(Collectors.toList());
       }

    }

    @Override
    public List<ComplexSongDTO> getTopSongsIds(String artistName, Integer count, @Nullable Long userId) {
        List<ArtistDTO> artists = artistService.searchByName(artistName, NumberUtils.INTEGER_ONE, NumberUtils.INTEGER_ZERO);
        if (CollectionUtils.isEmpty(artists)) {
            return Collections.emptyList();
        }
        ArtistDTO artistDTO = artists.getFirst();
        List<Long> songIds = this.findSongIdsByArtistId(Lists.newArrayList(artistDTO.getId()));
        if (CollectionUtils.isEmpty(songIds)) {
            return Collections.emptyList();
        }

        if (CollectionUtils.size(songIds) <= 1) {
            return this.queryBySongIds(songIds, userId);
        }
        Integer properRemoteCount = Math.min((int)(songIds.size() * 1.5), count);
        List<String> songTitles = metaDataFetchClientCommander.topTrack(artistName, properRemoteCount);

        List<ComplexSongDTO> complexSongs = this.queryBySongIds(songIds, userId);
        if (CollectionUtils.isNotEmpty(songTitles)) {
            Map<String, Integer> sortValue = Maps.newHashMapWithExpectedSize(songTitles.size() * 2);
            for (int i = 0; i < songTitles.size(); i++) {
                sortValue.put(songTitles.get(i), i);
            }
            List<String> reverseSongTitles = songTitles.stream().map(TransliteratorUtils::reverseSimpleTraditional).toList();
            for (int i = 0; i < reverseSongTitles.size(); i++) {
                sortValue.put(reverseSongTitles.get(i), i);
            }
            return complexSongs.stream().sorted((n1,n2) -> {
                int val1 = sortValue.getOrDefault(n1.getTitle(), Integer.MAX_VALUE);
                int val2 = sortValue.getOrDefault(n2.getTitle(), Integer.MAX_VALUE);
                if (val1 != val2) {
                    return val1 - val2;
                }
                if (Objects.nonNull(n1.getPlayCount()) && Objects.nonNull(n2.getPlayCount())) {
                    return n2.getPlayCount() - n1.getPlayCount();
                }
                return n2.getGmtCreate().compareTo(n1.getGmtCreate());
            }).toList();
        }

        return complexSongs.stream().sorted((n1,n2) -> {
            if (Objects.nonNull(n1.getPlayCount()) && Objects.nonNull(n2.getPlayCount())) {
                return n2.getPlayCount() - n1.getPlayCount();
            }
            return n2.getGmtCreate().compareTo(n1.getGmtCreate());
        }).toList();

    }


}
