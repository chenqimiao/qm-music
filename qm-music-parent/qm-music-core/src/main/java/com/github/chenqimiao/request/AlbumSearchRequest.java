package com.github.chenqimiao.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 19:43
 **/
@Setter
@Getter
@Builder
public class AlbumSearchRequest {

    private String sortColumn;

    /**
     * desc/asc
     */
    private String sortDirection;

    private Integer fromYear;

    private Integer toYear;

    private String genre;

    private Integer offset;

    private Integer size;

    private String type;

    private Long userId;

}
