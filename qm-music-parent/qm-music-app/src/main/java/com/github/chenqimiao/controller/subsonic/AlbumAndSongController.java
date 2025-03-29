package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.dto.AlbumDTO;
import com.github.chenqimiao.enums.EnumSubsonicAuthCode;
import com.github.chenqimiao.exception.SubsonicUnauthorizedException;
import com.github.chenqimiao.request.AlbumSearchRequest;
import com.github.chenqimiao.request.subsonic.AlbumList2Request;
import com.github.chenqimiao.response.subsonic.AlbumList2Response;
import com.github.chenqimiao.service.AlbumService;
import com.github.chenqimiao.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 19:19
 **/
@RestController
@RequestMapping(value = "/rest")
public class AlbumAndSongController {

    @Autowired
    private AlbumService albumService;


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
                                .sortColumn(StringUtils.equals(type, "byYear")? "release_year": "gmt_modify")
                                .sortDirection(sortDirection)
                                .offset(albumList2Request.getOffset() == null ? 0 : albumList2Request.getOffset())
                                .size(albumList2Request.getSize() == null ? 10 : albumList2Request.getSize())
                                .genre(albumList2Request.getGenre())
                                .fromYear(sortDirection.equals("desc") ? null: fromYear)
                                .toYear(sortDirection.equals("desc") ? null: toYear).build();


        List<AlbumDTO> albums = albumService.getAlbumList2(albumSearchRequest);
        AlbumList2Response albumList2Response = new AlbumList2Response();
        List<AlbumList2Response.Album> albumList = albums.stream().map(n -> {
            AlbumList2Response.Album album = new AlbumList2Response.Album();
            album.setId(n.getId());
            album.setName(n.getTitle());
            album.setSongCount(n.getSongCount());
            album.setCreated(DateTimeUtils.format(new Date(n.getGmtCreate()), DateTimeUtils.YMDTHMS));
            album.setCoverArt(n.getCoverArt());
            album.setDuration(n.getDuration());
            album.setArtist(n.getArtist());
            album.setArtistId(n.getArtistId());
            return album;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(albumList)) {
            albumList2Response.setAlbumList2(AlbumList2Response.AlbumList.builder()
                    .albums(albumList).build());
        }

        return albumList2Response;
    }
}

