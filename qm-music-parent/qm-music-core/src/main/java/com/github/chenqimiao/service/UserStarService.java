package com.github.chenqimiao.service;

import com.github.chenqimiao.dto.UserStarDTO;
import com.github.chenqimiao.request.BatchStarInfoRequest;
import com.github.chenqimiao.request.StarInfoRequest;
import com.github.chenqimiao.request.StarOrNotRequest;

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
}
