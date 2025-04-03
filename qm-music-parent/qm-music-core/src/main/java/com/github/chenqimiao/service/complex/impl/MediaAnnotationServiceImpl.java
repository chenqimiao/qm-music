package com.github.chenqimiao.service.complex.impl;

import com.github.chenqimiao.dto.*;
import com.github.chenqimiao.enums.EnumUserStarType;
import com.github.chenqimiao.service.AlbumService;
import com.github.chenqimiao.service.ArtistService;
import com.github.chenqimiao.service.SongService;
import com.github.chenqimiao.service.UserStarService;
import com.github.chenqimiao.service.complex.MediaAnnotationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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

    @Override
    public UserStarResourceDTO getUserStarResourceDTO(Integer userId) {
        List<UserStarDTO> userStarResources =
                userStarService.queryUserStarByUserId(userId);
        if (CollectionUtils.isEmpty(userStarResources)) {
            return UserStarResourceDTO.builder().userId(userId).build();
        }

        List<Integer> songIds = userStarResources.stream().filter(n ->
                        Objects.equals(n.getStarType(), EnumUserStarType.SONG.getCode()))
                .map(UserStarDTO::getRelationId).toList();

        List<Integer> artistIds = userStarResources.stream().filter(n ->
                        Objects.equals(n.getStarType(), EnumUserStarType.ARTIST.getCode()))
                .map(UserStarDTO::getRelationId).toList();

        List<Integer> albumIds = userStarResources.stream().filter(n ->
                        Objects.equals(n.getStarType(), EnumUserStarType.ALBUM.getCode()))
                .map(UserStarDTO::getRelationId).toList();

        List<SongDTO> songs = songService.batchQuerySongBySongIds(songIds);

        List<AlbumDTO> albums = albumService.batchQueryAlbumByAlbumIds(albumIds);

        List<ArtistDTO> artists = artistService.batchQueryArtistByArtistIds(artistIds);


        return UserStarResourceDTO.builder().userId(userId)
                .songs(songs).albums(albums).artists(artists).build();
    }
}
