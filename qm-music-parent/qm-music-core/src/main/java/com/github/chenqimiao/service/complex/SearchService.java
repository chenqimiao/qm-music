package com.github.chenqimiao.service.complex;

import com.github.chenqimiao.dto.SearchResultDTO;
import com.github.chenqimiao.request.CommonSearchRequest;

/**
 * @author Qimiao Chen
 * @since 2025/4/10 21:39
 **/
public interface SearchService {

    SearchResultDTO search(CommonSearchRequest request);
}
