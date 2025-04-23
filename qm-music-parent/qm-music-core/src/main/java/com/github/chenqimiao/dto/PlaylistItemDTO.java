package com.github.chenqimiao.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 19:26
 **/
@Setter
@Getter
public class PlaylistItemDTO {

    private Long id;

    private Long playlistId;

    private Long songId;

    private Integer sortOrder;

    private Long gmtCreate;

    private Long gmtModify;
}
