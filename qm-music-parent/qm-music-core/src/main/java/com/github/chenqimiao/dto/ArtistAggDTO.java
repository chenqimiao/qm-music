package com.github.chenqimiao.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 20:57
 **/
@Setter
@Getter
@Builder
public class ArtistAggDTO {

    private ArtistDTO artist;

    private List<AlbumDTO> albumList;
}
