package com.github.chenqimiao.app.controller.subsonic;

import com.github.chenqimiao.core.dto.ArtistAggDTO;
import com.github.chenqimiao.core.dto.ComplexArtistDTO;
import com.github.chenqimiao.core.service.ArtistService;
import com.github.chenqimiao.core.service.complex.ArtistComplexService;
import com.github.chenqimiao.app.response.subsonic.ArtistResponse;
import com.github.chenqimiao.app.util.WebUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 20:09
 **/
@RestController
@RequestMapping(value = "/rest")
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    @Autowired
    private ModelMapper modelMapper;

    private static final Type TYPE_LIST_ALBUM = new TypeToken<List<ArtistResponse.Album>>() {}.getType();

    @Autowired
    private ArtistComplexService artistComplexService;



    @RequestMapping(value = "/getArtist")
    public ArtistResponse getArtist(@RequestParam("id") Long artistId) {
        ArtistResponse artistResponse = new ArtistResponse();
        ArtistAggDTO artistAggDTO = artistService.queryArtistWithAlbums(artistId);

        ArtistResponse.Artist artist = artistAggDTO.getArtist() == null ? new ArtistResponse.Artist()
                : modelMapper.map(artistAggDTO.getArtist(), ArtistResponse.Artist.class);
        artist.setAlbumCount(CollectionUtils.size(artistAggDTO.getAlbumList()));
        List<ComplexArtistDTO> complexArtists = artistComplexService.queryByArtistIds(Lists.newArrayList(artistId), WebUtils.currentUserId());
        if (CollectionUtils.isNotEmpty(complexArtists)) {
            ComplexArtistDTO complexArtist = complexArtists.getFirst();
            artist.setSongCount(complexArtist.getSongCount());
            if (complexArtist.getStarred() != null) {
                artist.setStarred(new Date(complexArtist.getStarred()));
            }
        }
        artist.setAlbumList(modelMapper.map(artistAggDTO.getAlbumList(), TYPE_LIST_ALBUM));
        artistResponse.setArtist(artist);
        return artistResponse;
    }

}
