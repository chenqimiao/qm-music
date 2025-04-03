package com.github.chenqimiao.service.complex.impl;

import com.github.chenqimiao.config.ModelMapperTypeConfig;
import com.github.chenqimiao.dto.*;
import com.github.chenqimiao.enums.EnumUserStarType;
import com.github.chenqimiao.service.AlbumService;
import com.github.chenqimiao.service.ArtistService;
import com.github.chenqimiao.service.SongService;
import com.github.chenqimiao.service.UserStarService;
import com.github.chenqimiao.service.complex.MediaAnnotationService;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/4/3 11:28
 **/
@Service("subsonicMediaAnnotationServiceImpl")
public class MediaAnnotationServiceImpl implements MediaAnnotationService {

    @Autowired
    private UserStarService userStarService;

    @Autowired
    private SongService songService;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private AlbumService albumService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserStarResourceDTO getUserStarResourceDTO(Integer userId) {
        List<UserStarDTO> userStarRecords =
                userStarService.queryUserStarByUserId(userId);
        if (CollectionUtils.isEmpty(userStarRecords)) {
            return UserStarResourceDTO.builder().userId(userId).build();
        }

        Map<String, UserStarDTO> userStarMap = userStarRecords.stream().collect(
                Collectors.toMap(n -> this.buildUniqueKey(n.getStarType(), n.getRelationId()), n -> n));

        List<Integer> songIds = userStarRecords.stream().filter(n ->
                        Objects.equals(n.getStarType(), EnumUserStarType.SONG.getCode()))
                .map(UserStarDTO::getRelationId).toList();

        List<Integer> artistIds = userStarRecords.stream().filter(n ->
                        Objects.equals(n.getStarType(), EnumUserStarType.ARTIST.getCode()))
                .map(UserStarDTO::getRelationId).toList();

        List<Integer> albumIds = userStarRecords.stream().filter(n ->
                        Objects.equals(n.getStarType(), EnumUserStarType.ALBUM.getCode()))
                .map(UserStarDTO::getRelationId).toList();

        List<SongDTO> songs = songService.batchQuerySongBySongIds(songIds);

        List<AlbumDTO> albums = albumService.batchQueryAlbumByAlbumIds(albumIds);

        List<ArtistDTO> artists = artistService.batchQueryArtistByArtistIds(artistIds);

        List<SongWithStarDTO> songWithStars = modelMapper.map(songs, ModelMapperTypeConfig.TYPE_LIST_SONG_WITH_STAR_DTO);

        List<AlbumWithStarDTO> albumWithStars = modelMapper.map(albums, ModelMapperTypeConfig.TYPE_LIST_ALBUM_WITH_STAR_DTO);

        List<ArtistWithStarDTO> artistWithStars = modelMapper.map(artists, ModelMapperTypeConfig.TYPE_LIST_ARTIST_WITH_STAR_DTO);

        songWithStars.forEach(n -> {
            UserStarDTO userStarDTO = userStarMap.get(this.buildUniqueKey(EnumUserStarType.SONG.getCode(), n.getId()));
            n.setStarred(userStarDTO.getGmtCreate());
        });
        albumWithStars.forEach(n -> {
            UserStarDTO userStarDTO = userStarMap.get(this.buildUniqueKey(EnumUserStarType.ALBUM.getCode(), n.getId()));
            n.setStarred(userStarDTO.getGmtCreate());
        });
        artistWithStars.forEach(n -> {
            UserStarDTO userStarDTO = userStarMap.get(this.buildUniqueKey(EnumUserStarType.ARTIST.getCode(), n.getId()));
            n.setStarred(userStarDTO.getGmtCreate());
        });



        return UserStarResourceDTO.builder().userId(userId)
                .songs(songWithStars).albums(albumWithStars).artists(artistWithStars).build();
    }

    private String buildUniqueKey(Integer userStarTypeCode, Integer relationId) {
        return userStarTypeCode + "-" + relationId;
    }
}
