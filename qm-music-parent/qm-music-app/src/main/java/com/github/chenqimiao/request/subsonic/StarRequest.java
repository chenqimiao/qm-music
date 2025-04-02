package com.github.chenqimiao.request.subsonic;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 17:34
 **/
@Setter
@Getter
public class StarRequest {

    private Integer id;
    private Integer albumId;
    private Integer artistId;
}
