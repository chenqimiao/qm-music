package com.github.chenqimiao.qmmusic.core.io.local;

import com.github.chenqimiao.qmmusic.core.util.TransliteratorUtils;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Qimiao Chen
 * @since 2025/4/27 11:47
 **/
public abstract class LrcParser {
    private static final Pattern METADATA_PATTERN = Pattern.compile(
            "\\[(?<tag>ar|ti|offset):(?<value>.+)\\]",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern LYRIC_LINE_PATTERN = Pattern.compile(
            "(\\[\\d{2}:\\d{2}\\.\\d{2}\\])+\\s*(?<text>.+)"
    );

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StructuredLyrics {
        private String artist;

        private String title;

        private String language;

        private int offset;

        private boolean synced;

        private List<LyricLine> lines;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LyricLine {
        private Integer start;

        private String text;
    }

    public static StructuredLyrics parseLrc(Path lrcPath, String artist, String  title) throws IOException {
        List<String> lines = Files.readAllLines(lrcPath);
        return parseLrc(lines, artist, title);
    }

        // 高性能解析实现
    public static StructuredLyrics parseLrc(List<String> lines, @Nullable String artist, @Nullable String title) throws IOException {

        int offset = 0;
        List<LyricLine> lyricLines = new ArrayList<>();

        String lang = "zh";

        boolean langDetected = false;

        for (String line : lines) {
            line = line.trim();

            // 解析元数据
            Matcher metadataMatcher = METADATA_PATTERN.matcher(line);
            if (metadataMatcher.find()) {
                String tag = metadataMatcher.group("tag").toLowerCase();
                String value = metadataMatcher.group("value");
                switch (tag) {
                    case "ar" -> artist = value;
                    case "ti" -> title = value;
                    case "offset" -> offset = Integer.parseInt(value);
                }
                continue;
            }

            // 解析歌词行
            Matcher lineMatcher = LYRIC_LINE_PATTERN.matcher(line);
            if (lineMatcher.find()) {
                String text = lineMatcher.group("text");
                if (!langDetected) {
                    langDetected = true;
                    boolean chineseString = TransliteratorUtils.isChineseString(text);
                    if (!chineseString) {
                        lang = "en";
                    }
                }
                String[] timeTags = line.split("\\]\\s*");

                for (String timeTag : timeTags) {
                    if (timeTag.startsWith("[")) {
                        String timeStr = timeTag.substring(1);
                        int milliseconds = parseTime(timeStr);
                        lyricLines.add(new LyricLine(milliseconds, text));
                    }
                }
            }
        }

        // 构建响应结构
        StructuredLyrics lyrics = new StructuredLyrics(
                artist,
                title,
                lang,
                offset,
                true,
                lyricLines.stream()
                        .sorted((a, b) -> a.start.compareTo(b.start))
                        .toList()
        );

        return lyrics;
    }

    // 高性能时间解析（避免对象创建）
    private static int parseTime(String time) {
        String[] parts = time.split("[:.]");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        int hundredths = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

        return (minutes * 60_000) + (seconds * 1_000) + (hundredths * 10);
    }

}
