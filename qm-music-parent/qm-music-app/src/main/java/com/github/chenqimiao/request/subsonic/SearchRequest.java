package com.github.chenqimiao.request.subsonic;

import com.github.chenqimiao.DO.ArtistDO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/3/31 11:05
 **/
@Setter
@Getter
public class SearchRequest {

    private String query;

    private Integer artistCount =20;

    private Integer artistOffset = 0;

    private Integer albumCount =20;

    private Integer albumOffset = 0;

    private Integer songCount = 20;

    private Integer songOffset = 0;


}
