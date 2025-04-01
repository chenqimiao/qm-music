package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.dto.CoverStreamDTO;
import com.github.chenqimiao.dto.SongStreamDTO;
import com.github.chenqimiao.response.subsonic.LyricsResponse;
import com.github.chenqimiao.service.MediaRetrievalService;
import lombok.SneakyThrows;
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

    @RequestMapping(value = "/getCoverArt")
    @SneakyThrows
    public ResponseEntity<byte[]> getCoverArt(@RequestParam("id") String id,
                                              @RequestParam(value = "size", required = false) Integer size) {
        CoverStreamDTO songCoverStreamDTO = null;
        if (id.startsWith("al-")){
            songCoverStreamDTO = mediaRetrievalService.getAlbumCoverStreamDTO(Integer.valueOf(id.replace("al-", "")), size);
        }else if (id.startsWith("ar-")){
            songCoverStreamDTO = mediaRetrievalService.getArtistCoverStreamDTO(Integer.valueOf(id.replace("ar-","")), size);
        }else {
            songCoverStreamDTO = mediaRetrievalService.getSongCoverStreamDTO(Integer.valueOf(id), size);
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
    public ResponseEntity<InputStreamResource> stream(@RequestParam("id") Integer songId,
                                                      Integer maxBitRate, String format,
                                                      Integer estimateContentLength) {

        SongStreamDTO songStream = mediaRetrievalService.getSongStream(songId, maxBitRate, format, estimateContentLength);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(songStream.getSize()); // 手动设置长度
        return new ResponseEntity<>(new InputStreamResource(songStream.getSongStream()), headers, HttpStatus.OK);
    }

}
