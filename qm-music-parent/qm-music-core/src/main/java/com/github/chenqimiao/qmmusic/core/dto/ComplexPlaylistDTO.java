package com.github.chenqimiao.qmmusic.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 20:55
 **/
@Setter
@Getter
public class ComplexPlaylistDTO extends PlaylistDTO {


    List<ComplexSongDTO> complexSongs;
}
