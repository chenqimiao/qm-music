package com.github.chenqimiao.io.net.model;

import java.util.List;

public record ArtistDetail(
    String id,
    String name,
    String disambiguation,
    List<Tag> tags,
    List<Work> works
) {
    public record Tag(String name, int count) {}
    public record Work(String id, String title) {}
}