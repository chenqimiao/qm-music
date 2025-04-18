package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.constant.CoverArtPrefixConstants;
import com.github.chenqimiao.dto.CoverStreamDTO;
import com.github.chenqimiao.dto.SongStreamDTO;
import com.github.chenqimiao.enums.EnumSubsonicAuthCode;
import com.github.chenqimiao.exception.SubsonicUnauthorizedException;
import com.github.chenqimiao.response.subsonic.LyricsResponse;
import com.github.chenqimiao.service.ArtistService;
import com.github.chenqimiao.service.complex.MediaRetrievalService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 22:51
 **/
@RestController
@RequestMapping(value = "/rest")
public class MediaRetrievalController {

    @Autowired
    private MediaRetrievalService mediaRetrievalService;
    @Autowired
    private ArtistService artistService;

    @RequestMapping(value = "/getCoverArt")
    @SneakyThrows
    public ResponseEntity<byte[]> getCoverArt(@RequestParam("id") String id,
                                              @RequestParam(value = "size", required = false) Integer size) {
        String[] split = id.split("-");
        CoverStreamDTO songCoverStreamDTO = null;

        if (split.length <= 0 ) {
            throw new SubsonicUnauthorizedException(EnumSubsonicAuthCode.E_10);
        }

        long bizId = NumberUtils.toLong(split[split.length-1] , NumberUtils.LONG_ZERO);

        if (bizId <= NumberUtils.LONG_ZERO) {
            throw new SubsonicUnauthorizedException(EnumSubsonicAuthCode.E_10);
        }

        if (id.startsWith(CoverArtPrefixConstants.ALBUM_ID_PREFIX)){
            songCoverStreamDTO = mediaRetrievalService.getAlbumCoverStreamDTO(bizId, size);
        }else if (id.startsWith(CoverArtPrefixConstants.ARTIST_ID_PREFIX)){
            // do not support artist cover art
           // throw new SubsonicUnauthorizedException(EnumSubsonicAuthCode.E_10);
            songCoverStreamDTO = mediaRetrievalService.getArtistCoverStreamDTO(bizId, size);
            if(songCoverStreamDTO == null){
                 throw new SubsonicUnauthorizedException(EnumSubsonicAuthCode.E_10);
            }

            //return ResponseEntity.ok().body(null);
        }else {
            songCoverStreamDTO = mediaRetrievalService.getSongCoverStreamDTO(Long.valueOf(id), size);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(songCoverStreamDTO.getMimeType() == null
                        ? MediaType.IMAGE_PNG_VALUE : songCoverStreamDTO.getMimeType()))
                .body(songCoverStreamDTO.getCover());
    }


    @RequestMapping(value = "/getLyrics")
    public LyricsResponse getLyrics(@RequestParam(name ="artist") String artistName
                                    , @RequestParam("title") String songTitle) {
       String lyric = mediaRetrievalService.getLyrics(artistName, songTitle);
       LyricsResponse lyricsResponse = new LyricsResponse();
       lyricsResponse.setLyrics(LyricsResponse.Lyrics
               .builder()
                       .artistName(artistName)
                       .text(lyric)
               .build());
       return lyricsResponse;
    }


    @RequestMapping(value = "/stream")
    @SneakyThrows
    public ResponseEntity<InputStreamResource> stream(@RequestParam("id") Long songId,
                                                      Integer maxBitRate, String format,
                                                      Integer estimateContentLength) {

        SongStreamDTO songStream = mediaRetrievalService.getSongStream(songId, maxBitRate, format, estimateContentLength);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(songStream.getMimeType()));
        if (songStream.getSize() != null) {
            headers.setContentLength(songStream.getSize());
        }
        return new ResponseEntity<>(new InputStreamResource(songStream.getSongStream()),
                headers, HttpStatus.OK);
    }



}
