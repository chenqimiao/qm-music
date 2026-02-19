package com.github.chenqimiao.util;

import com.github.chenqimiao.qmmusic.core.util.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RandomStringUtilsTest {

    // ===== 默认长度测试 =====

    @Test
    void generate_NoArgs_Returns32CharString() {
        String result = RandomStringUtils.generate();
        assertNotNull(result);
        assertEquals(32, result.length());
    }

    // ===== 指定长度测试 =====

    @Test
    void generate_Length1_ReturnsSingleChar() {
        assertEquals(1, RandomStringUtils.generate(1).length());
    }

    @Test
    void generate_LargeLength_ReturnsCorrectLength() {
        assertEquals(128, RandomStringUtils.generate(128).length());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 16, 32, 64, 128, 256})
    void generate_VariousLengths_ReturnsCorrectLength(int length) {
        assertEquals(length, RandomStringUtils.generate(length).length());
    }

    // ===== 非法参数测试 =====

    @Test
    void generate_ZeroLength_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.generate(0));
    }

    @Test
    void generate_NegativeLength_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.generate(-1));
        assertThrows(IllegalArgumentException.class, () -> RandomStringUtils.generate(-100));
    }

    // ===== 字符集验证 =====

    @Test
    void generate_ResultContainsOnlyAlphanumericChars() {
        String result = RandomStringUtils.generate(100);
        assertTrue(result.matches("[A-Za-z0-9]+"),
            "Generated string should only contain [A-Za-z0-9], but was: " + result);
    }

    @Test
    void generate_LongString_CoversMostAlphanumericChars() {
        // 生成足够长的字符串，验证字符集覆盖
        String result = RandomStringUtils.generate(1000);
        Set<Character> chars = new HashSet<>();
        for (char c : result.toCharArray()) {
            chars.add(c);
        }
        // 1000个字符应该覆盖到62个可用字符中的大多数
        assertTrue(chars.size() > 40,
            "Should cover most of 62 alphanumeric chars, but only covered: " + chars.size());
    }

    // ===== 随机性验证 =====

    @Test
    void generate_TwoCalls_ProduceDifferentResults() {
        // 极低概率相同（1/62^32），实际测试中必然不同
        String result1 = RandomStringUtils.generate(32);
        String result2 = RandomStringUtils.generate(32);
        assertNotEquals(result1, result2,
            "Two independent calls should produce different strings");
    }

    @Test
    void generate_MultipleCalls_AllResultsAreUnique() {
        Set<String> results = new HashSet<>();
        int count = 20;
        for (int i = 0; i < count; i++) {
            results.add(RandomStringUtils.generate(32));
        }
        assertEquals(count, results.size(),
            "All generated strings should be unique");
    }
}
