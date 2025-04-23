package com.github.chenqimiao.dto;

import lombok.*;

/**
 * @author Qimiao Chen
 * @since 2025/4/1 00:18
 **/
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoverStreamDTO {

    private byte[] cover;

    private String mimeType;

}
