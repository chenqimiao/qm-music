package com.github.chenqimiao.util;

import java.time.ZoneId;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 13:55
 **/
public class TimeZoneUtils {
    public static String getRegionByTimezone() {
        ZoneId zone = ZoneId.systemDefault();
        String zoneId = zone.getId();

        // 判断时区是否属于中国
        if (zoneId.startsWith("Asia/Shanghai")
                || zoneId.startsWith("Asia/Chongqing")
                     || zoneId.startsWith("Asia/Harbin")
                        || zoneId.startsWith("Asia/Urumqi")
                            || zoneId.startsWith("Asia/Kashgar")) {
            return "CN";
        } else {
            return "OTHER";
        }
    }

    public static Boolean currentRegionIsChina() {
        return "CN".equals(getRegionByTimezone());
    }
}
