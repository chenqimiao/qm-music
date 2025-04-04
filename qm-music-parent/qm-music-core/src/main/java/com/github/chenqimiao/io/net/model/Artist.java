package com.github.chenqimiao.io.net.model;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 13:17
 **/

public record Artist(
    String id,
    String name,
    String country,
    List<String> tags
) {}