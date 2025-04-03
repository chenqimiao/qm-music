package com.github.chenqimiao.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/3 16:05
 **/
@Getter
@Setter
public abstract class PageRequest {

    private Integer offset;

    private Integer pageSize;


}
