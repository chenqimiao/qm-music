package com.github.chenqimiao.request.subsonic;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/2
 **/
@Setter
@Getter
public class StarRequest {

    private Long id;
    private Long albumId;
    private Long artistId;
}
