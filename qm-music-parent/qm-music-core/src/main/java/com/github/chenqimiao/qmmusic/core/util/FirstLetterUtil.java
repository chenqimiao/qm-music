package com.github.chenqimiao.qmmusic.core.util;

import com.github.chenqimiao.qmmusic.core.constant.CommonConstants;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @author Qimiao Chen
 * @since 2025/3/31 16:34
 **/
public abstract class FirstLetterUtil {

    /**
     * 获取字符串首字母（中文取拼音首字母，英文取首字母大写，其他返回#）
     */
    public static String getFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return CommonConstants.UN_KNOWN_FIRST_LETTER;
        }

        char firstChar = input.charAt(0);

        // 判断是否是中文字符
        if (isChineseCharacter(firstChar)) {
            return getChineseFirstLetter(firstChar);
        }
        // 判断是否是英文字母
        else if (isEnglishLetter(firstChar)) {
            return String.valueOf(firstChar).toUpperCase();
        }
        // 其他字符返回#
        else {
            return CommonConstants.UN_KNOWN_FIRST_LETTER;
        }
    }

    /**
     * 判断字符是否为中文（Unicode范围）
     */
    private static boolean isChineseCharacter(char c) {
        return (c >= 0x4E00 && c <= 0x9FA5);
    }

    /**
     * 判断字符是否为英文字母
     */
    private static boolean isEnglishLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    /**
     * 获取中文字符的拼音首字母
     */
    private static String getChineseFirstLetter(char c) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE); // 拼音大写
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE); // 不带声调

        try {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
            if (pinyinArray != null && pinyinArray.length > 0) {
                return pinyinArray[0].substring(0, 1); // 取第一个拼音的首字母
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return "#"; // 转换失败默认返回#
    }
}
