package com.github.chenqimiao.core.dto;

import lombok.*;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 20:57
 **/
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistAggDTO {

    private ArtistDTO artist;

    private List<AlbumDTO> albumList;

    private String artistImageUrl;
}
