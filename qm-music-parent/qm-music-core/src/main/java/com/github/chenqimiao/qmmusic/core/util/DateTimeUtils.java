package com.github.chenqimiao.qmmusic.core.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 22:18
 **/
public abstract class DateTimeUtils {

    public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";

    public static final String yyyyMMddTHHmmss = "yyyy-MM-dd'T'HH:mm:ss";

    public static final DateTimeFormatter YMDHMS = DateTimeFormatter.ofPattern(yyyyMMddHHmmss);

    public static final DateTimeFormatter YMDTHMS = DateTimeFormatter.ofPattern(yyyyMMddTHHmmss);


    public static String format(Date date, DateTimeFormatter formatter) {
        Instant instant = date.toInstant();
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        // 格式化为字符串
        return formatter.format(zonedDateTime);

    }

    public static String getCurTimezone() {
        return ZoneId.systemDefault().getId();
    }
}
