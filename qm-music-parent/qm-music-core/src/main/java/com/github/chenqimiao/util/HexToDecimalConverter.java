package com.github.chenqimiao.util;

import java.util.Arrays;

/**
 * @author Qimiao Chen
 * @since 2025/3/28 21:33
 **/
public abstract class HexToDecimalConverter {
    // 预先生成字符到数值的快速映射表 (ASCII范围优化)
    private static final int[] HEX_DECODE_MAP = new int[128];

    static {
        Arrays.fill(HEX_DECODE_MAP, -1);
        for (int i = 0; i < 10; i++) {
            HEX_DECODE_MAP['0' + i] = i;
        }
        for (int i = 0; i < 6; i++) {
            HEX_DECODE_MAP['a' + i] = 10 + i;
            HEX_DECODE_MAP['A' + i] = 10 + i;
        }
    }

    /**
     * 高性能十六进制字符串转字节数组
     * @param hex 十六进制字符串（必须为偶数长度）
     * @return 解码后的字节数组
     * @throws IllegalArgumentException 输入格式错误时抛出
     */
    public static String convert(String hex) {
        if (hex == null) throw new IllegalArgumentException("Input is null");
        int len = hex.length();
        if ((len & 1) != 0) throw new IllegalArgumentException("Odd length hex string");

        byte[] out = new byte[len >> 1];
        for (int i = 0, j = 0; j < len; i++) {
            int hi = charToValue(hex.charAt(j++));
            int lo = charToValue(hex.charAt(j++));
            out[i] = (byte) ((hi << 4) | lo);
        }
        return new String(out);
    }

    private static int charToValue(char c) {
        if (c >= 128 || (HEX_DECODE_MAP[c] == -1)) {
            throw new IllegalArgumentException("Invalid hex char: " + c);
        }
        return HEX_DECODE_MAP[c];
    }

}