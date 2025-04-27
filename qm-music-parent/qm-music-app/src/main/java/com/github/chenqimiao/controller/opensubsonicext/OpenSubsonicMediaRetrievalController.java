package com.github.chenqimiao.controller.opensubsonicext;

import com.github.chenqimiao.constant.ServerConstants;
import com.github.chenqimiao.io.local.LrcParser;
import com.github.chenqimiao.response.opensubsonic.LyricsBySongIdResponse;
import com.github.chenqimiao.service.complex.MediaRetrievalService;
import jakarta.annotation.Resource;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/26 20:01
 **/
@RestController
@RequestMapping(value = "/rest")
public class OpenSubsonicMediaRetrievalController {

    @Resource
    private MediaRetrievalService subsonicMediaRetrievalService;

    @Autowired
    private ModelMapper modelMapper;

    private static final Type TYPE_LIST_LYRIC_LINE = new TypeToken<List<LyricsBySongIdResponse.LyricLine>>() {}.getType();

    @RequestMapping("/getLyricsBySongId")
    public LyricsBySongIdResponse getLyricsBySongId(@RequestParam("id") Long songId) {

        LrcParser.StructuredLyrics lyrics = subsonicMediaRetrievalService.getLyricsBySongId(songId);

        if (lyrics == null) {
            return (LyricsBySongIdResponse) ServerConstants.OPEN_SUBSONIC_EMPTY_RESPONSE;
        }

        var structuredLyrics = modelMapper.map(lyrics, LyricsBySongIdResponse.StructuredLyrics.class);

        structuredLyrics.setLine(modelMapper.map(lyrics.getLines(), TYPE_LIST_LYRIC_LINE));

        return new LyricsBySongIdResponse(new LyricsBySongIdResponse.LyricsList(List.of(structuredLyrics)));

    }

}
