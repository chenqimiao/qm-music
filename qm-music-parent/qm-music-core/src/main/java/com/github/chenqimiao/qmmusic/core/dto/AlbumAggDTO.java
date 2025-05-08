package com.github.chenqimiao.qmmusic.core.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 15:34
 **/

@Getter
@Setter
@Builder
public class AlbumAggDTO {

    private AlbumDTO album;

    private List<SongAggDTO> songs;
}
