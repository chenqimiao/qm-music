package com.github.chenqimiao.request.subsonic;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 13:04
 **/
@Getter
@Setter
public class ArtistInfoRequest {

    private Long id;

    private Integer count = 20;

    private Boolean includeNotPresent = Boolean.FALSE;
}
