package com.github.chenqimiao.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

/**
 * @author Qimiao Chen
 * @since 2025/3/31 14:11
 **/
@Getter
@Setter
@Builder
public class SongStreamDTO {

    private InputStream songStream;
    private String filePath;
    private Long size;
    private String mimeType;
}
