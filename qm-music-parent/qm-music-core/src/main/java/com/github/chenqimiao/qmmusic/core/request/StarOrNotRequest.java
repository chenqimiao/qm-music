package com.github.chenqimiao.qmmusic.core.request;

import com.github.chenqimiao.qmmusic.core.enums.EnumStarActionType;
import com.github.chenqimiao.qmmusic.core.enums.EnumUserStarType;
import lombok.*;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 17:39
 **/
@Setter
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class StarOrNotRequest {

    private Long relationId;

    private EnumUserStarType startType;

    private Long userId;

    /**
     * @see  EnumStarActionType
     */
    private EnumStarActionType actionType;

}
