package com.github.chenqimiao.util;

import com.github.chenqimiao.qmmusic.core.util.MD5Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class MD5UtilsTest {

    // ===== md5(String) 测试 =====

    @Test
    void md5_NullString_ReturnsNull() {
        assertNull(MD5Utils.md5((String) null));
    }

    @Test
    void md5_EmptyString_ReturnsCorrectHash() {
        // echo -n "" | md5 = d41d8cd98f00b204e9800998ecf8427e
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", MD5Utils.md5(""));
    }

    @Test
    void md5_KnownString_ReturnsCorrectHash() {
        // echo -n "hello" | md5 = 5d41402abc4b2a76b9719d911017c592
        assertEquals("5d41402abc4b2a76b9719d911017c592", MD5Utils.md5("hello"));
    }

    @Test
    void md5_SameInput_ReturnsSameHash() {
        assertEquals(MD5Utils.md5("test"), MD5Utils.md5("test"));
    }

    @Test
    void md5_DifferentInputs_ReturnDifferentHashes() {
        assertNotEquals(MD5Utils.md5("hello"), MD5Utils.md5("world"));
    }

    @Test
    void md5_Result_HasLength32() {
        String result = MD5Utils.md5("any string");
        assertNotNull(result);
        assertEquals(32, result.length());
    }

    @Test
    void md5_Result_IsLowercase() {
        String result = MD5Utils.md5("test");
        assertNotNull(result);
        assertEquals(result, result.toLowerCase());
    }

    // ===== md5(byte[]) 测试 =====

    @Test
    void md5_NullByteArray_ReturnsNull() {
        assertNull(MD5Utils.md5((byte[]) null));
    }

    @Test
    void md5_ByteArray_MatchesStringMD5() {
        byte[] bytes = "hello".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        assertEquals(MD5Utils.md5("hello"), MD5Utils.md5(bytes));
    }

    @Test
    void md5_EmptyByteArray_ReturnsCorrectHash() {
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", MD5Utils.md5(new byte[0]));
    }

    // ===== 参数化测试：验证已知 MD5 值 =====

    @ParameterizedTest
    @MethodSource("knownMD5Values")
    void md5_KnownValues_MatchExpected(String input, String expectedHash) {
        assertEquals(expectedHash, MD5Utils.md5(input));
    }

    private static Stream<Arguments> knownMD5Values() {
        return Stream.of(
            Arguments.of("", "d41d8cd98f00b204e9800998ecf8427e"),
            Arguments.of("hello", "5d41402abc4b2a76b9719d911017c592"),
            Arguments.of("world", "7d793037a0760186574b0282f2f435e7"),
            Arguments.of("admin", "21232f297a57a5a743894a0e4a801fc3"),
            Arguments.of("123456", "e10adc3949ba59abbe56e057f20f883e")
        );
    }

    // ===== 并发安全性验证（ThreadLocal 复用） =====

    @Test
    void md5_MultipleCalls_ReturnConsistentResults() {
        // 验证 ThreadLocal 复用 MD5 实例时的正确性
        String expected = MD5Utils.md5("test");
        for (int i = 0; i < 10; i++) {
            assertEquals(expected, MD5Utils.md5("test"));
        }
    }
}
