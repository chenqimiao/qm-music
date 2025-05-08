package com.github.chenqimiao.qmmusic.core.request;

import com.github.chenqimiao.qmmusic.core.enums.EnumUserStarType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 21:19
 **/
@Setter
@Getter
@Builder
public class StarInfoRequest {

    private Long relationId;

    private EnumUserStarType startType;

    private Long userId;
}
