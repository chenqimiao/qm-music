package com.github.chenqimiao.io.model;


import com.alibaba.fastjson2.annotation.JSONField;
import lombok.*;
import org.jaudiotagger.tag.images.Artwork;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/24 14:26
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MusicAlbumMeta {

    private String album;

    private String albumArtist;

    private String originalYear;

    @JSONField(serialize = false)
    private List<Artwork> artworks;

    /**
     * 专辑类型 : 合集（album;compilation）...
     */
    private String musicbrainzReleaseType;


    private String genre;

}
