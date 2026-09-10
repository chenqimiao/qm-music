package com.github.chenqimiao.qmmusic.app.response.subsonic;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.github.chenqimiao.qmmusic.core.util.DateTimeUtils;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2026/9/10
 **/
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlayQueueResponse extends SubsonicResponse {

    private PlayQueue playQueue;

    @Setter
    @Getter
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class PlayQueue {

        /**
         * 当前播放歌曲 id
         */
        @JacksonXmlProperty(isAttribute = true)
        private String current;

        /**
         * 当前歌曲播放进度（毫秒）
         */
        @JacksonXmlProperty(isAttribute = true)
        private Long position;

        @JacksonXmlProperty(isAttribute = true)
        private String username;

        @JacksonXmlProperty(isAttribute = true)
        @JsonFormat(pattern = DateTimeUtils.yyyyMMddTHHmmss) // jackson xml or json format
        @DateTimeFormat(pattern = DateTimeUtils.yyyyMMddTHHmmss) // fastjson2 json format
        private Date changed;

        /**
         * 最后修改队列的客户端名称
         */
        @JacksonXmlProperty(isAttribute = true)
        private String changedBy;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "entry")
        @JSONField(name = "entry")
        private List<PlaylistResponse.Entry> entries;
    }
}
