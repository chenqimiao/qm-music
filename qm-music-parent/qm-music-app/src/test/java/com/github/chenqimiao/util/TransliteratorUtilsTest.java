package com.github.chenqimiao.util;

import com.github.chenqimiao.qmmusic.core.util.TransliteratorUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TransliteratorUtilsTest {

    // ===== isChineseCharacter 测试 =====

    @Test
    void isChineseCharacter_ChineseChar_ReturnsTrue() {
        assertTrue(TransliteratorUtils.isChineseCharacter('中'));
        assertTrue(TransliteratorUtils.isChineseCharacter('国'));
        assertTrue(TransliteratorUtils.isChineseCharacter('爱'));
    }

    @Test
    void isChineseCharacter_EnglishChar_ReturnsFalse() {
        assertFalse(TransliteratorUtils.isChineseCharacter('a'));
        assertFalse(TransliteratorUtils.isChineseCharacter('Z'));
    }

    @Test
    void isChineseCharacter_NumberChar_ReturnsFalse() {
        assertFalse(TransliteratorUtils.isChineseCharacter('1'));
        assertFalse(TransliteratorUtils.isChineseCharacter('9'));
    }

    @Test
    void isChineseCharacter_SpecialChar_ReturnsFalse() {
        assertFalse(TransliteratorUtils.isChineseCharacter('@'));
        assertFalse(TransliteratorUtils.isChineseCharacter(' '));
    }

    // ===== isChineseString 测试 =====

    @Test
    void isChineseString_PureChinese_ReturnsTrue() {
        assertTrue(TransliteratorUtils.isChineseString("你好世界"));
    }

    @Test
    void isChineseString_PureEnglish_ReturnsFalse() {
        assertFalse(TransliteratorUtils.isChineseString("hello world"));
    }

    @Test
    void isChineseString_MixedTextWithChinese_ReturnsTrue() {
        assertTrue(TransliteratorUtils.isChineseString("hello 你好"));
    }

    @Test
    void isChineseString_PureNumbers_ReturnsFalse() {
        assertFalse(TransliteratorUtils.isChineseString("12345"));
    }

    // ===== toTraditional 测试 =====

    @Test
    void toTraditional_SimplifiedChinese_ConvertsToTraditional() {
        // 简体"爱" -> 繁体"愛"
        String result = TransliteratorUtils.toTraditional("爱");
        assertEquals("愛", result);
    }

    @Test
    void toTraditional_SimplifiedSentence_ConvertsCorrectly() {
        // 简体"国" -> 繁体"國"
        String result = TransliteratorUtils.toTraditional("国");
        assertEquals("國", result);
    }

    @Test
    void toTraditional_NonChinese_ReturnsSame() {
        String result = TransliteratorUtils.toTraditional("hello");
        assertEquals("hello", result);
    }

    // ===== toSimplified 测试 =====

    @Test
    void toSimplified_BlankString_ReturnsSame() {
        String blank = "  ";
        assertEquals(blank, TransliteratorUtils.toSimplified(blank));
    }

    @Test
    void toSimplified_AlreadySimplified_ReturnsSameOrEqual() {
        // 已是简体的字符应该保持不变（"中国"均为共通用字）
        String simplified = "中国";
        assertEquals(simplified, TransliteratorUtils.toSimplified(simplified));
    }

    @Test
    void toSimplified_TraditionalChinese_ConvertsToSimplified() {
        // 繁体"愛" -> 简体"爱"
        String result = TransliteratorUtils.toSimplified("愛");
        assertEquals("爱", result);
    }

    // ===== detectChineseType 测试 =====

    @Test
    void detectChineseType_NonChineseText_ReturnsNotChinese() {
        TransliteratorUtils.ChineseType type = TransliteratorUtils.detectChineseType("hello");
        assertEquals(TransliteratorUtils.ChineseType.NOT_CHINESE, type);
    }

    @Test
    void detectChineseType_SimplifiedChinese_ReturnsSimplified() {
        // "爱"是简体字
        TransliteratorUtils.ChineseType type = TransliteratorUtils.detectChineseType("爱");
        // 简体检测
        assertNotEquals(TransliteratorUtils.ChineseType.NOT_CHINESE, type);
    }

    @Test
    void detectChineseType_TraditionalChinese_ReturnsTraditional() {
        // "愛"是繁体字
        TransliteratorUtils.ChineseType type = TransliteratorUtils.detectChineseType("愛");
        assertNotEquals(TransliteratorUtils.ChineseType.NOT_CHINESE, type);
    }

    // ===== reverseSimpleTraditional 测试 =====

    @Test
    void reverseSimpleTraditional_NullInput_ReturnsNull() {
        assertNull(TransliteratorUtils.reverseSimpleTraditional(null));
    }

    @Test
    void reverseSimpleTraditional_BlankInput_ReturnsSame() {
        String blank = "  ";
        assertEquals(blank, TransliteratorUtils.reverseSimpleTraditional(blank));
    }

    @Test
    void reverseSimpleTraditional_NonChineseText_ReturnsSame() {
        String english = "hello world";
        assertEquals(english, TransliteratorUtils.reverseSimpleTraditional(english));
    }

    @Test
    void reverseSimpleTraditional_SimplifiedChinese_ConvertsToTraditional() {
        // 以简体字开头，应该转换为繁体
        String simplified = "爱";
        String result = TransliteratorUtils.reverseSimpleTraditional(simplified);
        assertNotNull(result);
        assertNotEquals(simplified, result); // 应有转换
        assertEquals("愛", result);
    }

    @Test
    void reverseSimpleTraditional_TraditionalChinese_ConvertsToSimplified() {
        // 以繁体字开头，应该转换为简体
        String traditional = "愛";
        String result = TransliteratorUtils.reverseSimpleTraditional(traditional);
        assertNotNull(result);
        assertEquals("爱", result);
    }

    // ===== 参数化测试 =====

    @ParameterizedTest
    @MethodSource("simplifiedToTraditionalCases")
    void toTraditional_KnownCases(String input, String expected) {
        assertEquals(expected, TransliteratorUtils.toTraditional(input));
    }

    private static Stream<Arguments> simplifiedToTraditionalCases() {
        return Stream.of(
            Arguments.of("爱", "愛"),
            Arguments.of("国", "國"),
            Arguments.of("长", "長"),
            Arguments.of("说", "說")
        );
    }
}
