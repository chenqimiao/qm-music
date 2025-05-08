package com.github.chenqimiao.qmmusic.core.request;

import com.github.chenqimiao.qmmusic.core.enums.EnumUserStarType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 21:35
 **/
@Setter
@Getter
@Builder
public class BatchStarInfoRequest {

    private EnumUserStarType startType;

    private Long userId;

    private List<Long> relationIds;

}
