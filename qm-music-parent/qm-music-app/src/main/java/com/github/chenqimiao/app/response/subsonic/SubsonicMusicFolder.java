package com.github.chenqimiao.app.response.subsonic;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 12:08
 **/

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubsonicMusicFolder extends SubsonicResponse {

    private List<MusicFolder> musicFolders;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MusicFolder {

        @JacksonXmlProperty(isAttribute = true)
        private Long id;
        @JacksonXmlProperty(isAttribute = true)
        private String name;
    }
}
