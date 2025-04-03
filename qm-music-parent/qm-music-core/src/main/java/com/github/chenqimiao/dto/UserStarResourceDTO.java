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

    private Long userId;

    private List<AlbumWithStarDTO> albums;

    private List<ArtistWithStarDTO> artists;

    private List<SongWithStarDTO> songs;

}
