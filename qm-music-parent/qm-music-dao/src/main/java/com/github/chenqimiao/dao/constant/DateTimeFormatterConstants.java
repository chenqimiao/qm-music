package com.github.chenqimiao.dao.constant;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author Qimiao Chen
 * @since 2025/4/17 16:22
 **/
public abstract class DateTimeFormatterConstants {
    public static final DateTimeFormatter SQLITE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .withZone(ZoneId.systemDefault());
}
