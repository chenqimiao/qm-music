package com.github.chenqimiao.core.dto;

import lombok.Data;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 16:11
 **/
@Data
public class GenreStatisticsDTO {

    private String genreName;

    private Integer albumCount;

    private Integer songCount;
}
