package com.github.chenqimiao.util;

import com.github.chenqimiao.qmmusic.core.util.HexToDecimalConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class HexToDecimalConverterTest {

    // ===== 异常输入测试 =====

    @Test
    void convert_NullInput_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> HexToDecimalConverter.convert(null));
    }

    @Test
    void convert_OddLengthHex_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> HexToDecimalConverter.convert("abc"));
        assertThrows(IllegalArgumentException.class, () -> HexToDecimalConverter.convert("1"));
        assertThrows(IllegalArgumentException.class, () -> HexToDecimalConverter.convert("abcde"));
    }

    @Test
    void convert_InvalidHexChar_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> HexToDecimalConverter.convert("gg"));
        assertThrows(IllegalArgumentException.class, () -> HexToDecimalConverter.convert("zz"));
        assertThrows(IllegalArgumentException.class, () -> HexToDecimalConverter.convert("0x"));
    }

    // ===== 正常转换测试 =====

    @Test
    void convert_EmptyString_ReturnsEmptyString() {
        assertEquals("", HexToDecimalConverter.convert(""));
    }

    @Test
    void convert_LowercaseHexHello_ReturnsHello() {
        // "hello" = 68 65 6c 6c 6f
        assertEquals("hello", HexToDecimalConverter.convert("68656c6c6f"));
    }

    @Test
    void convert_UppercaseHexHELLO_ReturnsHELLO() {
        // "HELLO" = 48 45 4C 4C 4F
        assertEquals("HELLO", HexToDecimalConverter.convert("48454C4C4F"));
    }

    @Test
    void convert_MixedCaseHex_ReturnsCorrectResult() {
        // "hello" 小写混合大写 Hex 转换应该一致
        assertEquals("hello", HexToDecimalConverter.convert("68656C6C6F"));
    }

    @Test
    void convert_SingleByte_ReturnsCorrectChar() {
        // 0x41 = 'A'
        assertEquals("A", HexToDecimalConverter.convert("41"));
        // 0x61 = 'a'
        assertEquals("a", HexToDecimalConverter.convert("61"));
        // 0x30 = '0'
        assertEquals("0", HexToDecimalConverter.convert("30"));
    }

    // ===== 参数化测试 =====

    @ParameterizedTest
    @MethodSource("knownHexConversions")
    void convert_KnownHexValues_ReturnsExpectedStrings(String hex, String expected) {
        assertEquals(expected, HexToDecimalConverter.convert(hex));
    }

    private static Stream<Arguments> knownHexConversions() {
        return Stream.of(
            Arguments.of("68656c6c6f", "hello"),           // 小写 hex
            Arguments.of("48454C4C4F", "HELLO"),           // 大写 hex
            Arguments.of("776f726c64", "world"),           // world
            Arguments.of("41424344", "ABCD"),              // ABCD
            Arguments.of("", "")                           // 空字符串
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"gg", "zz", "GG", "ZZ", "1g", "g1", "0x41"})
    void convert_InvalidHexChars_ThrowsIllegalArgumentException(String invalidHex) {
        assertThrows(IllegalArgumentException.class, () -> HexToDecimalConverter.convert(invalidHex));
    }
}
