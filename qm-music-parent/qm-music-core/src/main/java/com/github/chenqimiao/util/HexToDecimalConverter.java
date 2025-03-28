package com.github.chenqimiao.util;

import java.util.Arrays;

/**
 * @author Qimiao Chen
 * @since 2025/3/28 21:33
 **/
public abstract class HexToDecimalConverter {
    // 预计算十六进制字符到数值的映射表（ASCII优化版）
    private static final int[] HEX_VALUE = new int[128];
    static {
        Arrays.fill(HEX_VALUE, -1);
        for (int i = 0; i <= 9; i++) HEX_VALUE['0' + i] = i;
        for (int i = 0; i < 6; i++) {
            HEX_VALUE['A' + i] = 10 + i;
            HEX_VALUE['a' + i] = 10 + i;
        }
    }

    /**
     * 高性能十六进制转十进制
     * @param hex 十六进制字符串（支持大小写）
     * @return 十进制字符串
     * @throws IllegalArgumentException 输入非法时抛出异常
     */
    public static String convert(String hex) {
        // 输入校验和预处理
        final int length = hex.length();
        if (length == 0) throw new IllegalArgumentException("Empty input");

        // 转换数字存储容器（预估最大长度：hex.length() * log16(10) ≈ length * 1.25）
        int[] decimalDigits = new int[(int)(length * 1.25) + 2];
        int digitsCount = 1; // 初始化为0的有效位数

        // 逐字符处理
        for (int i = 0; i < length; i++) {
            char c = hex.charAt(i);
            if (c >= HEX_VALUE.length || (c = Character.toUpperCase(c)) >= HEX_VALUE.length) {
                throw new IllegalArgumentException("Invalid character: " + c);
            }

            final int value = HEX_VALUE[c];
            if (value == -1) throw new IllegalArgumentException("Invalid character: " + c);

            // 大数乘法：decimal = decimal * 16 + value
            multiplyBy16(decimalDigits, digitsCount);
            addValue(decimalDigits, value);

            // 动态更新有效位数
            if (decimalDigits[digitsCount] != 0) digitsCount++;
        }

        // 转换为字符串
        return buildResult(decimalDigits, digitsCount);
    }

    // 大数乘法：乘以16（优化版）
    private static void multiplyBy16(int[] digits, int length) {
        int carry = 0;
        for (int i = 0; i < length; i++) {
            long product = (digits[i] & 0xFFFFFFFFL) * 16 + carry;
            digits[i] = (int) product;
            carry = (int) (product >>> 32);
        }
        if (carry != 0) digits[length] = carry;
    }

    // 大数加法：加单个数值（0-15）
    private static void addValue(int[] digits, int value) {
        long sum = (digits[0] & 0xFFFFFFFFL) + value;
        digits[0] = (int) sum;
        int carry = (int) (sum >>> 32);

        for (int i = 1; carry != 0 && i < digits.length; i++) {
            sum = (digits[i] & 0xFFFFFFFFL) + carry;
            digits[i] = (int) sum;
            carry = (int) (sum >>> 32);
        }
    }

    // 结果字符串构建（优化内存分配）
    private static String buildResult(int[] digits, int length) {
        // 计算最终字符串长度
        int strLength = 0;
        int[] temp = Arrays.copyOf(digits, length);
        for (int i = length-1; i >= 0; i--) {
            long number = temp[i] & 0xFFFFFFFFL;
            while (number > 0) {
                strLength++;
                number /= 10;
            }
            if (i != 0 && strLength == 0) continue; // 跳过前导零
        }
        if (strLength == 0) return "0";

        // 分配精确长度的字符数组
        char[] result = new char[strLength];
        int pos = result.length - 1;

        for (int i = 0; i < length; i++) {
            long number = temp[i] & 0xFFFFFFFFL;
            for (int j = 0; j < 9 && pos >= 0; j++) { // 每次处理最多9位
                long rem = number % 10;
                result[pos--] = (char)('0' + rem);
                number /= 10;
                if (number == 0 && i == length-1) break;
            }
        }

        // 跳过前导零
        int start = 0;
        while (start < result.length-1 && result[start] == '0') start++;
        return new String(result, start, result.length - start);
    }
}