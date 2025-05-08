package com.github.chenqimiao.qmmusic.core.job;

import com.github.chenqimiao.qmmusic.core.dto.UserDTO;
import com.github.chenqimiao.qmmusic.core.service.PlayHistoryService;
import com.github.chenqimiao.qmmusic.core.service.UserService;
import com.github.chenqimiao.qmmusic.core.service.complex.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Qimiao Chen
 * @since 2025/4/12 15:19
 **/
@Component
@Slf4j
public class CronJob {

    @Autowired
    private SystemService systemService;

    @Autowired
    private UserService userService;

    @Value("${qm.user.default.username}")
    private String defaultUserName;

    @Autowired
    private PlayHistoryService playHistoryService;

    @Value("${qm.refresh.auto}")
    private Boolean autoRefresh;

    @Scheduled(cron = "0 8 */2 * * *")
    public void refreshSongsCronJob() {
        UserDTO userDTO = userService.findByUsername(defaultUserName);
        if (userDTO != null && Boolean.TRUE.equals(userDTO.getForcePasswordChange())) {
            // 初始化完成后，才会定时刷新歌曲
            return;
        }
        if (Boolean.FALSE.equals(autoRefresh)) {
            // 默认会自动刷新曲库，需要强制关闭
            return;
        }
        systemService.refreshSongs();
    }


    @Scheduled(cron = "0 0 6 * * WED")
    public void cleanPlayHistory() {

        playHistoryService.cleanPlayHistory();

        log.info("clean play history ...");
    }

}
