package com.github.chenqimiao.third.lastfm.model;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public  class Artist {
    String name;
    String url;
    @JSONField(name = "match")
    float matchScore;
}