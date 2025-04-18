package com.github.chenqimiao.request;

import com.github.chenqimiao.enums.EnumStarActionType;
import com.github.chenqimiao.enums.EnumUserStarType;
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
     * @see  com.github.chenqimiao.enums.EnumStarActionType
     */
    private EnumStarActionType actionType;

}
