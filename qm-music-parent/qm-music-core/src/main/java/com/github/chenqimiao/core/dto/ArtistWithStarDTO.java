package com.github.chenqimiao.core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/3 15:23
 **/
@Getter
@Setter
public class ArtistWithStarDTO extends ArtistDTO {
    private Long starred;

}
