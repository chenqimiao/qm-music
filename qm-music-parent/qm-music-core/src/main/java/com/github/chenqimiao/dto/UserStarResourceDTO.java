package com.github.chenqimiao.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/3 11:30
 **/
@Setter
@Getter
@Builder
public class UserStarResourceDTO {

    private Integer userId;

    private List<AlbumDTO> albums;

    private List<ArtistDTO> artists;

    private List<SongDTO> songs;
}
