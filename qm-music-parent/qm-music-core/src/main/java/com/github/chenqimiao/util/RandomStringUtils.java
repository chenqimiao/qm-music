package com.github.chenqimiao.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Qimiao Chen
 * @since 2025/4/6 14:36
 **/
public final class RandomStringUtils {

    // 包含大写字母、小写字母和数字的字符集（共62个字符）
    private static final char[] CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final int CHARS_LENGTH = CHARS.length;
    private static final int DEFAULT_LENGTH = 32;

    // 私有构造方法防止实例化
    private RandomStringUtils() {}

    /**
     * 生成32位随机字符串
     * @return 32位的随机字符串
     */
    public static String generate() {
        return generate(DEFAULT_LENGTH);
    }

    /**
     * 生成指定长度的随机字符串
     * @param length 字符串长度
     * @return 指定长度的随机字符串
     */
    public static String generate(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }

        final char[] buffer = new char[length];
        final ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < length; i++) {
            buffer[i] = CHARS[random.nextInt(CHARS_LENGTH)];
        }

        return new String(buffer);
    }
}