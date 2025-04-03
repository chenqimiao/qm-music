package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.constant.ServerConstants;
import com.github.chenqimiao.dto.*;
import com.github.chenqimiao.enums.EnumSubsonicAuthCode;
import com.github.chenqimiao.exception.SubsonicUnauthorizedException;
import com.github.chenqimiao.request.AlbumSearchRequest;
import com.github.chenqimiao.request.subsonic.AlbumList2Request;
import com.github.chenqimiao.response.subsonic.*;
import com.github.chenqimiao.service.AlbumService;
import com.github.chenqimiao.service.complex.MediaAnnotationService;
import com.github.chenqimiao.service.complex.MediaRetrievalService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 19:19
 **/
@RestController
@RequestMapping(value = "/rest")
public class AlbumSongController {

    @Autowired
    private AlbumService albumService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MediaAnnotationService mediaRetrievalService;


    private static final Type TYPE_LIST_ALBUM = new TypeToken<List<AlbumList2Response.Album>>() {}.getType();
    private static final Type TYPE_LIST_SONG = new TypeToken<List<AlbumResponse.Song>>() {}.getType();
    @Autowired
    private ResourcePatternResolver resourcePatternResolver;


    @GetMapping(value = "/getAlbumList2")
    public AlbumList2Response getAlbumList2(AlbumList2Request albumList2Request) {
        String type = albumList2Request.getType();

        if ((albumList2Request.getSize() != null && albumList2Request.getSize() > 500) || type == null) {
            throw new SubsonicUnauthorizedException(EnumSubsonicAuthCode.E_0);
        }

        Integer fromYear = albumList2Request.getFromYear();
        Integer toYear = albumList2Request.getToYear();
        String sortDirection = fromYear == null || toYear == null || fromYear <= toYear ? "asc" : "desc";

        AlbumSearchRequest albumSearchRequest = AlbumSearchRequest.builder()
                .sortColumn(StringUtils.equals(type, "byYear") ? "release_year" : "gmt_modify")
                .sortDirection(sortDirection)
                .offset(albumList2Request.getOffset() == null ? 0 : albumList2Request.getOffset())
                .size(albumList2Request.getSize() == null ? 10 : albumList2Request.getSize())
                .genre(albumList2Request.getGenre())
                .fromYear(sortDirection.equals("desc") ? null : fromYear)
                .toYear(sortDirection.equals("desc") ? null : toYear).build();


        List<AlbumDTO> albums = albumService.getAlbumList2(albumSearchRequest);
        AlbumList2Response albumList2Response = new AlbumList2Response();

        List<AlbumList2Response.Album> albumList = modelMapper.map(albums, TYPE_LIST_ALBUM);
        if (CollectionUtils.isNotEmpty(albumList)) {
            albumList2Response.setAlbumList2(AlbumList2Response.AlbumList.builder()
                    .albums(albumList).build());
        }

        return albumList2Response;
    }

    private static final Type TYPE_STAR_LIST_ALBUM = new TypeToken<List<StarredResponse.Album>>() {}.getType();
    private static final Type TYPE_STAR_LIST_SONG = new TypeToken<List<StarredResponse.Song>>() {}.getType();
    private static final Type TYPE_STAR_LIST_ARTIST = new TypeToken<List<StarredResponse.Artist>>() {}.getType();

    @GetMapping("/getStarred")
    public StarredResponse getStarred(@RequestParam(required = false) Long musicFolderId,
                           HttpServletRequest servletRequest) {

        Integer authedUserId = (Integer) servletRequest.getAttribute(ServerConstants.AUTHED_USER_ID);

        UserStarResourceDTO resource = mediaRetrievalService.getUserStarResourceDTO(authedUserId);

        return new StarredResponse(StarredResponse.Starred
                .builder()
                .albums(modelMapper.map(resource.getAlbums(), TYPE_STAR_LIST_ALBUM))
                .songs(modelMapper.map(resource.getSongs(), TYPE_STAR_LIST_SONG))
                .artists( modelMapper.map(resource.getArtists(), TYPE_STAR_LIST_ARTIST))
                .build());


    }
}

