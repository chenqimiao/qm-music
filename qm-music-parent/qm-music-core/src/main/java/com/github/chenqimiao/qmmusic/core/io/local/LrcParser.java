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
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class LrcParser {
    private static final Pattern METADATA_PATTERN = Pattern.compile(
            "\\[(?<tag>ar|ti|offset):(?<value>.+)\\]",
            Pattern.CASE_INSENSITIVE
    );

    // 更新正则表达式支持小时段和灵活位数
    private static final Pattern LYRIC_LINE_PATTERN = Pattern.compile(
            "(\\[\\d{1,2}:\\d{1,2}(?::\\d{1,2})?(?:[.:]\\d{1,3})?\\])+\\s*(?<text>.+)"
    );

    // 新增时间解析正则（支持小时段和灵活位数）
    private static final Pattern TIME_PATTERN = Pattern.compile(
            "(?:(?<hours>\\d{1,2}):)?(?<minutes>\\d{1,2}):(?<seconds>\\d{1,2})(?:[.:](?<millis>\\d{1,3}))?"
    );

    // 结构化歌词类（保持不变）
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

    // 歌词行类（保持不变）
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LyricLine {
        private Integer start;
        private String text;
    }

    public static StructuredLyrics parseLrc(Path lrcPath, String artist, String title) throws IOException {
        List<String> lines = Files.readAllLines(lrcPath);
        return parseLrc(lines, artist, title);
    }

    public static StructuredLyrics parseLrc(List<String> lines, @Nullable String artist, @Nullable String title) {
        int offset = 0;
        List<LyricLine> lyricLines = new ArrayList<>();
        String lang = "zh";
        boolean langDetected = false;

        for (String line : lines) {
            line = line.trim();

            // 解析元数据（保持不变）
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

            // 解析歌词行（更新为更灵活的时间格式）
            Matcher lineMatcher = LYRIC_LINE_PATTERN.matcher(line);
            if (lineMatcher.find()) {
                String text = lineMatcher.group("text");
                if (!langDetected) {
                    langDetected = true;
                    boolean chineseString = TransliteratorUtils.isChineseString(text);
                    lang = chineseString ? "zh" : "en";
                }

                // 提取所有时间标签
                Matcher timeMatcher = Pattern.compile("\\[([^]]+)\\]").matcher(line);
                while (timeMatcher.find()) {
                    String timeStr = timeMatcher.group(1);
                    try {
                        int milliseconds = parseTime(timeStr);
                        lyricLines.add(new LyricLine(milliseconds, text));
                    } catch (NumberFormatException e) {
                        // 忽略无效时间格式
                    }
                }
            }
        }

        // 构建响应结构（添加排序）
        StructuredLyrics lyrics = new StructuredLyrics(
                artist,
                title,
                lang,
                offset,
                true,
                lyricLines.stream()
                        .sorted(Comparator.comparingInt(LyricLine::getStart))
                        .toList()
        );

        return lyrics;
    }

    // 完全重写的时间解析方法（支持小时段和灵活位数）
    public static int parseTime(String time) {
        Matcher matcher = TIME_PATTERN.matcher(time);
        if (!matcher.find()) {
            throw new NumberFormatException("Invalid time format: " + time);
        }

        // 解析各个时间组件
        String hoursPart = matcher.group("hours");
        String minutesPart = matcher.group("minutes");
        String secondsPart = matcher.group("seconds");
        String millisPart = matcher.group("millis");

        int hours = hoursPart != null ? Integer.parseInt(hoursPart) : 0;
        int minutes = minutesPart != null ? Integer.parseInt(minutesPart) : 0;
        int seconds = secondsPart != null ? Integer.parseInt(secondsPart) : 0;
        int millis = 0;

        // 解析毫秒部分
        if (millisPart != null) {
            int msValue = Integer.parseInt(millisPart);
            switch (millisPart.length()) {
                case 1 -> millis = msValue * 100;  // 1位：十分之一秒 → 毫秒
                case 2 -> millis = msValue * 10;   // 2位：百分之一秒 → 毫秒
                case 3 -> millis = msValue;        // 3位：直接作为毫秒
                default -> millis = msValue;        // 默认直接使用（可能超过3位）
            }
        }

        // 验证时间范围
        validateTimeRange(minutes, "minutes", 0, 59);
        validateTimeRange(seconds, "seconds", 0, 59);

        // 计算总毫秒数
        return (hours * 3600 * 1000) +
                (minutes * 60 * 1000) +
                (seconds * 1000) +
                millis;
    }

    // 添加时间范围验证
    private static void validateTimeRange(int value, String name, int min, int max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    "Invalid " + name + " value: " + value +
                            " (must be between " + min + " and " + max + ")"
            );
        }
    }
}