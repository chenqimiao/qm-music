package com.github.chenqimiao.service;

import com.github.chenqimiao.request.BatchStarInfoRequest;
import com.github.chenqimiao.request.StarInfoRequest;
import com.github.chenqimiao.request.StarOrNotRequest;

import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 17:38
 **/
public interface MediaAnnotationService {

     void starOrNot(StarOrNotRequest starOrNotRequest);


     boolean isStar(StarInfoRequest starInfoRequest);


     Long starredTime(StarInfoRequest starInfoRequest);


     Map<Integer, Long> batchQueryStarredTime(BatchStarInfoRequest batchStarInfoRequest);

}
