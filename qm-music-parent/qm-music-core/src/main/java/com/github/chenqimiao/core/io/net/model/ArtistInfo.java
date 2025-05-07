package com.github.chenqimiao.core.io.net.model;

import lombok.*;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 14:13
 **/
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtistInfo {


    private String artistName;

    private String biography;

    private String imageUrl;

    private String mediumImageUrl;

    private String smallImageUrl;

    private String largeImageUrl;

}
