package com.github.chenqimiao.qmmusic.core.service.complex;

import com.github.chenqimiao.qmmusic.core.dto.UserStarResourceDTO;

/**
 * @author Qimiao Chen
 * @since 2025/4/3 11:28
 **/
public interface MediaAnnotationService {

    UserStarResourceDTO getUserStarResourceDTO(Long userId);
}
