package com.github.chenqimiao.third.lastfm.model;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public  class Artist {
    private String name;
    private String url;
    @JSONField(name = "match")
    private float matchScore;
}