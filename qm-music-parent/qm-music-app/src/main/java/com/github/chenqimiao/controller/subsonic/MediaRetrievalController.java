package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.service.MediaRetrievalService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 22:51
 **/
@RestController
@RequestMapping(value = "/rest")
public class MediaRetrievalController {

    @Autowired
    private MediaRetrievalService mediaRetrievalService;

    @RequestMapping(value = "/getCoverArt", produces = MediaType.IMAGE_JPEG_VALUE)
    @SneakyThrows
    public ResponseEntity<byte[]> getCoverArt(@RequestParam("id") String id, @RequestParam("size") Integer size) {
        File file = null;
        if (id.startsWith("al-")){
            file = mediaRetrievalService.getAlbumCoverArt(Integer.valueOf(id.replace("al-","")), size);
        }else if (id.startsWith("ar-")){
            file = mediaRetrievalService.getArtistCoverArt(Integer.valueOf(id.replace("ar-","")), size);
        }else {
            file = mediaRetrievalService.getSongCoverArt(Integer.valueOf(id), size);
        }
        // 将File对象转换为Path
        Path path = file.toPath();
        // 直接读取所有字节
        return ResponseEntity.ok()
               // .contentType(MediaType.APPLICATION_PDF)
               // .header("Content-Disposition", "inline; filename=\"dynamic.pdf\"") // 内联显示
                .body(Files.readAllBytes(path));
    }
}
