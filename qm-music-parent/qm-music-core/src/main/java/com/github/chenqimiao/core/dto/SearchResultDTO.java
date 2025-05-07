package com.github.chenqimiao.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/10 21:40
 **/
@Setter
@Getter
public class SearchResultDTO {

    private List<ArtistDTO> artists;

    private List<AlbumDTO> albums;

    private List<ComplexSongDTO> complexSongs;
}
