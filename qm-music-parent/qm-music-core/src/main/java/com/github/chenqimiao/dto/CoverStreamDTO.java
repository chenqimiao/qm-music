package com.github.chenqimiao.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/1 00:18
 **/
@Setter
@Getter
@Builder
public class CoverStreamDTO {

    private byte[] cover;

    private String mimeType;

}
