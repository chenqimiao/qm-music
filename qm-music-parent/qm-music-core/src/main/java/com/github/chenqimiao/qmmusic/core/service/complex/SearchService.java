package com.github.chenqimiao.qmmusic.core.service.complex;

import com.github.chenqimiao.qmmusic.core.dto.SearchResultDTO;
import com.github.chenqimiao.qmmusic.core.request.CommonSearchRequest;

/**
 * @author Qimiao Chen
 * @since 2025/4/10 21:39
 **/
public interface SearchService {

    SearchResultDTO search(CommonSearchRequest request);
}
