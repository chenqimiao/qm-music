package com.github.chenqimiao.constant;

import com.google.common.util.concurrent.RateLimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Qimiao Chen
 * @since 2025/4/7 22:39
 **/
public abstract class RateLimiterConstants {

    public static final String COVER_ART_BY_REMOTE_LIMIT_KEY = "COVER_ART_BY_REMOTE_LIMIT_KEY";

    public static final Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();

    public static final String GET_ARTIST_INFO2_BY_REMOTE_LIMIT_KEY = "GET_ARTIST_INFO2_BY_REMOTE_LIMIT_KEY";

    public static final String SPOTIFY_API_LIMIT_KEY = "SPOTIFY_API_LIMIT_KEY";

    public static final String LAST_FM_API_LIMIT_KET = "LAST_FM_API_LIMIT_KET";



}
