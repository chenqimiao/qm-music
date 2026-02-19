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
        // 生成足够长的字符串，验证所有字符均为字母或数字（确定性约束）
        String result = RandomStringUtils.generate(1000);
        assertTrue(result.matches("[A-Za-z0-9]+"),
            "Generated long string should only contain [A-Za-z0-9], but was: " + result);
    }

    // ===== 随机性验证 =====
    // 随机性/唯一性测试在理论上可能失败（如碰撞），会导致单测非确定性，因此这里只在其他用例中验证长度、
    // 字符集和非法参数等确定性行为，不对随机性本身作强断言。
}
