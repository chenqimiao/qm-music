package com.github.chenqimiao.util;

import com.github.chenqimiao.qmmusic.core.util.FirstLetterUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class FirstLetterUtilTest {

    private static final String UNKNOWN = "#";

    // ===== null / 空字符串 =====

    @Test
    void getFirstLetter_Null_ReturnsHash() {
        assertEquals(UNKNOWN, FirstLetterUtil.getFirstLetter(null));
    }

    @Test
    void getFirstLetter_EmptyString_ReturnsHash() {
        assertEquals(UNKNOWN, FirstLetterUtil.getFirstLetter(""));
    }

    // ===== 英文字母 =====

    @Test
    void getFirstLetter_LowercaseEnglish_ReturnsUppercase() {
        assertEquals("H", FirstLetterUtil.getFirstLetter("hello"));
        assertEquals("W", FirstLetterUtil.getFirstLetter("world"));
    }

    @Test
    void getFirstLetter_UppercaseEnglish_ReturnsSameUppercase() {
        assertEquals("A", FirstLetterUtil.getFirstLetter("Apple"));
        assertEquals("Z", FirstLetterUtil.getFirstLetter("Zoo"));
    }

    @Test
    void getFirstLetter_SingleLetter_ReturnsUppercase() {
        assertEquals("A", FirstLetterUtil.getFirstLetter("a"));
        assertEquals("Z", FirstLetterUtil.getFirstLetter("z"));
    }

    // ===== 非字母非中文字符 =====

    @Test
    void getFirstLetter_StartsWithDigit_ReturnsHash() {
        assertEquals(UNKNOWN, FirstLetterUtil.getFirstLetter("123abc"));
        assertEquals(UNKNOWN, FirstLetterUtil.getFirstLetter("1"));
    }

    @Test
    void getFirstLetter_StartsWithSpecialChar_ReturnsHash() {
        assertEquals(UNKNOWN, FirstLetterUtil.getFirstLetter("!hello"));
        assertEquals(UNKNOWN, FirstLetterUtil.getFirstLetter("@test"));
        assertEquals(UNKNOWN, FirstLetterUtil.getFirstLetter(" hello")); // 空格开头
    }

    // ===== 中文字符 =====

    @Test
    void getFirstLetter_ChineseZhong_ReturnsZ() {
        // "中" -> 拼音 zhong -> 首字母 Z
        assertEquals("Z", FirstLetterUtil.getFirstLetter("中国"));
    }

    @Test
    void getFirstLetter_ChineseBei_ReturnsB() {
        // "北" -> 拼音 bei -> 首字母 B
        assertEquals("B", FirstLetterUtil.getFirstLetter("北京"));
    }

    @Test
    void getFirstLetter_ChineseSingleChar_ReturnsPinyinFirstLetter() {
        assertEquals("A", FirstLetterUtil.getFirstLetter("爱"));   // ai -> A
        assertEquals("N", FirstLetterUtil.getFirstLetter("你"));   // ni -> N
    }

    // ===== 参数化测试 =====

    @ParameterizedTest
    @MethodSource("englishFirstLetterCases")
    void getFirstLetter_EnglishInputs(String input, String expectedLetter) {
        assertEquals(expectedLetter, FirstLetterUtil.getFirstLetter(input));
    }

    private static Stream<Arguments> englishFirstLetterCases() {
        return Stream.of(
            Arguments.of("abc", "A"),
            Arguments.of("Bcd", "B"),
            Arguments.of("hello world", "H"),
            Arguments.of("jazz", "J"),
            Arguments.of("Queen", "Q"),
            Arguments.of("Rock", "R")
        );
    }

    @ParameterizedTest
    @MethodSource("chineseFirstLetterCases")
    void getFirstLetter_ChineseInputs_ReturnsCorrectPinyinFirstLetter(String input, String expectedLetter) {
        assertEquals(expectedLetter, FirstLetterUtil.getFirstLetter(input));
    }

    private static Stream<Arguments> chineseFirstLetterCases() {
        return Stream.of(
            Arguments.of("爱", "A"),
            Arguments.of("北京", "B"),
            Arguments.of("中国", "Z"),
            Arguments.of("你好", "N")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "!@#", " hello", "\t", "_test"})
    void getFirstLetter_NonAlphanumericStart_ReturnsHash(String input) {
        assertEquals(UNKNOWN, FirstLetterUtil.getFirstLetter(input));
    }
}
