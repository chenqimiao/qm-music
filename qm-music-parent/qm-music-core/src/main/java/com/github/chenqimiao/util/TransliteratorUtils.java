package com.github.chenqimiao.util;

import com.ibm.icu.text.Transliterator;
import lombok.Getter;

/**
 * @author Qimiao Chen
 * @since 2025/4/5 23:12
 **/
public abstract class TransliteratorUtils {

    // 初始化转换器
    private static final Transliterator SIMPLIFIED_TO_TRADITIONAL =
            Transliterator.getInstance("Simplified-Traditional");
    private static final Transliterator TRADITIONAL_TO_SIMPLIFIED =
            Transliterator.getInstance("Traditional-Simplified");


    @Getter
    public enum ChineseType {
        SIMPLIFIED, TRADITIONAL, MIXED, NOT_CHINESE
    }


    // 简体转繁体
    public static String toTraditional(String simplified) {
        Transliterator transliterator = Transliterator.getInstance("Simplified-Traditional");
        return transliterator.transliterate(simplified);
    }

    // 繁体转简体
    public static String toSimplified(String traditional) {
        Transliterator transliterator = Transliterator.getInstance("Traditional-Simplified");
        return transliterator.transliterate(traditional);
    }
    public static ChineseType detectChineseType(String text) {
        if (!isChineseString(text)) return ChineseType.NOT_CHINESE;

        int simplifiedCount = 0;
        int traditionalCount = 0;

        for (char c : text.toCharArray()) {
            if (!isChineseCharacter(c)) continue;

            String charStr = String.valueOf(c);
            String toTraditional = SIMPLIFIED_TO_TRADITIONAL.transliterate(charStr);
            String toSimplified = TRADITIONAL_TO_SIMPLIFIED.transliterate(charStr);

            // 转换后不同的视为对应类型
            if (!charStr.equals(toTraditional)) simplifiedCount++;
            if (!charStr.equals(toSimplified)) traditionalCount++;
        }

        if (simplifiedCount > 0 && traditionalCount > 0) {
            return ChineseType.MIXED;
        } else if (simplifiedCount > 0) {
            return ChineseType.SIMPLIFIED;
        } else if (traditionalCount > 0) {
            return ChineseType.TRADITIONAL;
        }
        return ChineseType.MIXED; // 全为共通用字的情况
    }

    public static boolean isChineseCharacter(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS);
    }

    public static boolean isChineseString(String text) {
        return text.chars().anyMatch(c -> isChineseCharacter((char)c));
    }

    public static ChineseType quickDetect(String text, int sampleSize) {
        int length = text.length();
        if (length == 0) return ChineseType.NOT_CHINESE;

        int step = Math.max(1, length / sampleSize);
        int count = 0;
        for (int i = 0; i < length && count < sampleSize; i += step) {
            char c = text.charAt(i);
            // 此处调用单字符检测逻辑
            count++;
        }
        // 根据抽样结果判断
    }
}
