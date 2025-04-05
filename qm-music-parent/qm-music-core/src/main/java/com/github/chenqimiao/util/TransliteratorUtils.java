package com.github.chenqimiao.util;

import com.ibm.icu.text.Transliterator;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

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


    public static String reverseSimpleTraditional(String text) {
        if (StringUtils.isBlank(text)) return text;
        TransliteratorUtils.ChineseType chineseType = TransliteratorUtils.detectChineseType(text.substring(0,1));
        if (TransliteratorUtils.ChineseType.SIMPLIFIED == chineseType) {
            return TransliteratorUtils.toTraditional(text);
        } else if (TransliteratorUtils.ChineseType.TRADITIONAL == chineseType) {
            return TransliteratorUtils.toSimplified(text);
        } else {
            return text;
        }
    }

    public static ChineseType quickDetect(String text, int sampleSize) {
        if (text == null || text.isEmpty()) {
            return ChineseType.NOT_CHINESE;
        }

        int length = text.length();
        int step = Math.max(1, length / sampleSize);
        int sampled = 0;
        int simplifiedCount = 0;
        int traditionalCount = 0;
        int chineseCharCount = 0;

        for (int i = 0; i < length && sampled < sampleSize; i += step) {
            char c = text.charAt(i);
            if (!isChineseCharacter(c)) continue; // 跳过非中文字符

            // 检测单字符类型
            boolean isSimplified = isSimplifiedChar(c);
            boolean isTraditional = isTraditionalChar(c);

            if (isSimplified && !isTraditional) {
                simplifiedCount++;
            } else if (isTraditional && !isSimplified) {
                traditionalCount++;
            }
            // 两者都为true时为共通用字（如"人"、"中"）

            chineseCharCount++;
            sampled++;
        }

        // 结果判断逻辑
        if (chineseCharCount == 0) {
            return ChineseType.NOT_CHINESE;
        }

        final double ratioThreshold = 0.8; // 占比阈值
        double simplifiedRatio = (double) simplifiedCount / chineseCharCount;
        double traditionalRatio = (double) traditionalCount / chineseCharCount;

        if (simplifiedRatio > ratioThreshold && traditionalRatio < 0.2) {
            return ChineseType.SIMPLIFIED;
        } else if (traditionalRatio > ratioThreshold && simplifiedRatio < 0.2) {
            return ChineseType.TRADITIONAL;
        } else if (simplifiedCount > 0 && traditionalCount > 0) {
            return ChineseType.MIXED;
        } else {
            // 共通用字为主的情况（如全部是"人人口"）
            return ChineseType.MIXED;
        }
    }

    // 单字符检测方法（需实现）
    private static boolean isSimplifiedChar(char c) {
        String str = String.valueOf(c);
        return !str.equals(SIMPLIFIED_TO_TRADITIONAL.transliterate(str));
    }

    private static boolean isTraditionalChar(char c) {
        String str = String.valueOf(c);
        return !str.equals(TRADITIONAL_TO_SIMPLIFIED.transliterate(str));
    }
}
