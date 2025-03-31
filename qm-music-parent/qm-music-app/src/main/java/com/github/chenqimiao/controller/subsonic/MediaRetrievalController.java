package com.github.chenqimiao.controller.subsonic;

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

import java.io.File;

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
        File file = null;
        byte[] coverArt = null;
        if (id.startsWith("al-")){
            // file = mediaRetrievalService.getSongCoverArt(Integer.valueOf(id.replace("al-","")), size);
            coverArt = mediaRetrievalService.getSongCoverArtByte(Integer.valueOf(id.replace("al-","")), size);

        }else if (id.startsWith("ar-")){
            //file = mediaRetrievalService.getArtistCoverArt(Integer.valueOf(id.replace("ar-","")), size);
        }else {
            //file = mediaRetrievalService.getSongCoverArt(Integer.valueOf(id), size);
             coverArt = mediaRetrievalService.getSongCoverArtByte(Integer.valueOf(id), size);
        }
        // 将File对象转换为Path
//        Path path = file.toPath();
//        // 直接读取所有字节
//        return ResponseEntity.ok()
//               // .contentType(MediaType.APPLICATION_PDF)
//               // .header("Content-Disposition", "inline; filename=\"dynamic.pdf\"") // 内联显示
//                .body(Files.readAllBytes(path));

        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(coverArt);
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
