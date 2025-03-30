package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.dto.AlbumAggDTO;
import com.github.chenqimiao.dto.AlbumDTO;
import com.github.chenqimiao.dto.SongAggDTO;
import com.github.chenqimiao.dto.SongDTO;
import com.github.chenqimiao.enums.EnumSubsonicAuthCode;
import com.github.chenqimiao.exception.SubsonicUnauthorizedException;
import com.github.chenqimiao.request.AlbumSearchRequest;
import com.github.chenqimiao.request.subsonic.AlbumList2Request;
import com.github.chenqimiao.response.subsonic.AlbumList2Response;
import com.github.chenqimiao.response.subsonic.AlbumResponse;
import com.github.chenqimiao.service.AlbumService;
import com.github.chenqimiao.service.SongService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
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

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SongService songService;

    private static final Type TYPE_LIST_ALBUM = new TypeToken<List<AlbumList2Response.Album>>() {}.getType();
    private static final Type TYPE_LIST_SONG = new TypeToken<List<AlbumResponse.Song>>() {}.getType();

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

        List<AlbumList2Response.Album> albumList  = modelMapper.map(albums, TYPE_LIST_ALBUM);
        if (CollectionUtils.isNotEmpty(albumList)) {
            albumList2Response.setAlbumList2(AlbumList2Response.AlbumList.builder()
                    .albums(albumList).build());
        }

        return albumList2Response;
    }

    @GetMapping(value = "/getAlbum")
    public AlbumResponse getAlbum(@RequestParam("id") Integer albumId) {
        AlbumAggDTO albumAggDTO = songService.queryByAlbumId(albumId);
        AlbumDTO albumDTO = albumAggDTO.getAlbum();
        List<SongAggDTO> songs = albumAggDTO.getSongs();
        AlbumResponse albumResponse = new AlbumResponse();
        AlbumResponse.Album album = modelMapper.map(albumDTO, AlbumResponse.Album.class);
        List<AlbumResponse.Song> songList = songs.stream().map(songAggDTO -> {
            SongDTO song = songAggDTO.getSong();
            AlbumResponse.Song s = modelMapper.map(song, AlbumResponse.Song.class);
            s.setArtistName(songAggDTO.getArtistName());
            s.setType("music");
            s.setAlbumTitle(albumDTO.getTitle());
            s.setIsDir(Boolean.FALSE);
            s.setIsVideo(false);
            return s;
        }).collect(Collectors.toList());
        album.setSongs(songList);
        albumResponse.setAlbum(album);
        return albumResponse;
    }
}

