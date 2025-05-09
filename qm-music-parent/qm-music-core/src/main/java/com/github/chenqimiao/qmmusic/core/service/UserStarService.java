package com.github.chenqimiao.qmmusic.core.service;

import com.github.chenqimiao.qmmusic.core.dto.UserStarDTO;
import com.github.chenqimiao.qmmusic.core.enums.EnumUserStarType;
import com.github.chenqimiao.qmmusic.core.request.BatchStarInfoRequest;
import com.github.chenqimiao.qmmusic.core.request.StarInfoRequest;
import com.github.chenqimiao.qmmusic.core.request.StarOrNotRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 17:38
 **/
public interface UserStarService {

     void starOrNot(StarOrNotRequest starOrNotRequest);


     boolean isStar(StarInfoRequest starInfoRequest);


     Long starredTime(StarInfoRequest starInfoRequest);


     Map<Long, Long> batchQueryStarredTime(BatchStarInfoRequest batchStarInfoRequest);


     List<UserStarDTO> queryUserStarByUserId(Long userId);

     List<UserStarDTO> queryUserStarByUserIdAndType(Long userId, EnumUserStarType type);

}
