package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.core.constant.CoverArtPrefixConstants;
import com.github.chenqimiao.core.dto.CoverStreamDTO;
import com.github.chenqimiao.core.dto.SongStreamDTO;
import com.github.chenqimiao.core.service.ArtistService;
import com.github.chenqimiao.core.service.complex.MediaRetrievalService;
import com.github.chenqimiao.enums.EnumSubsonicErrorCode;
import com.github.chenqimiao.exception.SubsonicCommonErrorException;
import com.github.chenqimiao.response.subsonic.LyricsResponse;
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
                                              @RequestParam(value = "size", required = false, defaultValue = "100") Integer size) {
        String[] split = id.split("-");
        CoverStreamDTO songCoverStreamDTO = null;

        if (split.length <= 0 ) {
            throw new SubsonicCommonErrorException(EnumSubsonicErrorCode.E_10);
        }

        long bizId = NumberUtils.toLong(split[split.length-1] , NumberUtils.LONG_ZERO);

        if (bizId <= NumberUtils.LONG_ZERO) {
            throw new SubsonicCommonErrorException(EnumSubsonicErrorCode.E_10);
        }

        if (id.startsWith(CoverArtPrefixConstants.ALBUM_ID_PREFIX)){
            songCoverStreamDTO = mediaRetrievalService.getAlbumCoverStreamDTO(bizId, size);
        }else if (id.startsWith(CoverArtPrefixConstants.ARTIST_ID_PREFIX)){

            songCoverStreamDTO = mediaRetrievalService.getArtistCoverStreamDTO(bizId, size);
            if(songCoverStreamDTO == null){
                 throw new SubsonicCommonErrorException(EnumSubsonicErrorCode.E_10);
            }

        }else if (id.startsWith(CoverArtPrefixConstants.SONG_COVER_ART_PREFIX)) {

            songCoverStreamDTO = mediaRetrievalService.getSongCoverStreamDTO(bizId, size);

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
                                                      @RequestParam(defaultValue = "false") Boolean estimateContentLength) {

        SongStreamDTO songStream = mediaRetrievalService.getSongStream(songId, maxBitRate, format, estimateContentLength);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(songStream.getMimeType()));
        if (songStream.getSize() != null) {
            headers.setContentLength(songStream.getSize());
        }
        return new ResponseEntity<>(new InputStreamResource(songStream.getSongStream()),
                headers, HttpStatus.OK);

    }
    @RequestMapping(value = "/download")
    @SneakyThrows
    public ResponseEntity<InputStreamResource> download(@RequestParam("id") Long songId) {

        SongStreamDTO songStream = mediaRetrievalService.getRawSongStream(songId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(songStream.getMimeType()));
        if (songStream.getSize() != null) {
            headers.setContentLength(songStream.getSize());
        }
        return new ResponseEntity<>(new InputStreamResource(songStream.getSongStream()),
                headers, HttpStatus.OK);

    }


}
