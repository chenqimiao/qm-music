package com.github.chenqimiao.qmmusic.core.service;

import com.github.chenqimiao.qmmusic.core.dto.PlayQueueDTO;
import com.github.chenqimiao.qmmusic.core.request.SavePlayQueueRequest;

/**
 * @author Qimiao Chen
 * @since 2026/9/10
 **/
public interface PlayQueueService {

    /**
     * 保存用户的播放队列，已存在则整体覆盖；songIds 为空时清空队列
     */
    void save(SavePlayQueueRequest request);

    /**
     * 查询用户的播放队列，从未保存过返回 null
     */
    PlayQueueDTO queryByUserId(Long userId);

    /**
     * 删除用户的播放队列（删除用户时清理）
     */
    void deleteByUserId(Long userId);
}
