package com.github.chenqimiao.io.local;

import com.github.chenqimiao.qmmusic.core.io.local.LrcParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class LrcParserTest {
    private static final List<String> SAMPLE_LRC = List.of(
            "[ar:邓紫棋]",
            "[ti:光年之外]",
            "[offset:500]",
            "[00:01.50]感受停在我发端的指尖",
            "[00:05.20]如何瞬间 冻结时间"
    );

    // 基础解析功能测试
    @Test
    void parseLrc_StandardFormat() {
        LrcParser.StructuredLyrics result = LrcParser.parseLrc(SAMPLE_LRC, null, null);

        // 验证元数据
        assertEquals("邓紫棋", result.getArtist());
        assertEquals("光年之外", result.getTitle());
        assertEquals(500, result.getOffset());

        // 验证歌词行
        List<LrcParser.LyricLine> lines = result.getLines();
        assertEquals(2, lines.size());
        assertEquals(1500, lines.get(0).getStart()); // 时间补偿生效
        assertEquals("感受停在我发端的指尖", lines.get(0).getText());
    }

    // 时间解析边界测试
    @Test
    void parseTime_EdgeCases() {
        assertEquals(0, LrcParser.parseTime("0:0.0"));       // 最小值
        assertEquals(3723000, LrcParser.parseTime("1:02:03")); // 带小时
        assertEquals(59999, LrcParser.parseTime("0:59.999"));  // 毫秒上限
    }

    // 异常格式处理测试
    @Test
    void parseLrc_InvalidTimeFormat() {
        List<String> invalidLrc = List.of("[0x:yz]无效时间戳");
        LrcParser.StructuredLyrics result = LrcParser.parseLrc(invalidLrc, null, null);

        // 应忽略无效行
        assertEquals(0, result.getLines().size());
    }

    // 多时间标签测试
    @Test
    void parseLrc_MultipleTimestamps() {
        List<String> multiTimeLrc = List.of(
                "[00:01][00:02]双时间标签歌词",
                "[00:03]正常歌词"
        );

        LrcParser.StructuredLyrics result = LrcParser.parseLrc(multiTimeLrc, null, null);
        assertEquals(3, result.getLines().size()); // 两个时间点+一个歌词
        assertEquals("双时间标签歌词", result.getLines().get(0).getText());
        assertEquals(2000, result.getLines().get(1).getStart());
    }

    // 性能测试（需JUnit 5.5+）
    @Test
    @Timeout(1) // 1秒超时
    void parseLargeFile_Performance() {
        // 生成1000行测试数据
        List<String> largeLrc = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeLrc.add(String.format("[%02d:%02d.%03d]歌词行%d",
                    i/60, i%60, i%1000, i));
        }

        assertDoesNotThrow(() -> LrcParser.parseLrc(largeLrc, null, null));
    }


    @ParameterizedTest
    @MethodSource("validTimeFormats")
    void parseTime_ValidFormats_ReturnsCorrectMillis(String timeStr, int expectedMillis) {
        assertEquals(expectedMillis, LrcParser.parseTime(timeStr));
    }

    // 参数化测试：异常格式处理
    @ParameterizedTest
    @MethodSource("invalidTimeFormats")
    void parseTime_InvalidFormats_ThrowsException(String timeStr) {
        assertThrows(IllegalArgumentException.class, () -> LrcParser.parseTime(timeStr));
    }

    // 合法时间格式数据源
    private static Stream<Arguments> validTimeFormats() {
        return Stream.of(
                // 分钟/秒单位数测试 [c:1][c:3]
                Arguments.of("0:0.0", 0),             // 最小值
                Arguments.of("9:5.1", 545100),        // 分钟/秒单位数 + 1位毫秒（100ms）
                Arguments.of("0:5", 5000),             // 无毫秒

                // 分钟/秒双位数测试
                Arguments.of("00:00.000", 0),          // 全双位
                Arguments.of("59:59.999", 3599999),   // 最大值边界
                Arguments.of("12:34.567", 754567),    // 3位毫秒

                // 混合位数测试 [c:3]
                Arguments.of("1:02.03", 62030),       // 分钟单位数 + 秒双位 + 毫秒双位
                Arguments.of("01:2.3", 62300),         // 分钟双位 + 秒单位数 + 毫秒单位数（300ms）

                // 包含小时的格式 [c:1][c:3]
                Arguments.of("1:0:0", 3600000),       // 小时单位数
                Arguments.of("12:30:45.200", 45045200), // 小时双位 + 毫秒

                // 毫秒分隔符兼容性 [c:3]
                Arguments.of("0:0:0.123", 123)      // 毫秒用冒号分隔（非标准）
        );
    }

    // 非法时间格式数据源
    private static Stream<Arguments> invalidTimeFormats() {
        return Stream.of(
                Arguments.of("60:00"),                // 分钟超限（>59）
                Arguments.of("00:60"),                // 秒超限（>59）
                Arguments.of("abc:def")            // 非数字
        );
    }
}