package com.github.chenqimiao.core.third.lastfm.model;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public  class Track {

        private String name;

        private  String url;

        @JSONField(name = "match")
        private float matchScore;

        private Artist artist;

}