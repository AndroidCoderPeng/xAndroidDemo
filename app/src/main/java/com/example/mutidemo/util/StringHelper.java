package com.example.mutidemo.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class StringHelper {
    /**
     * 获取汉语拼音
     */
    public static String obtainHanYuPinyin(String chinese) {
        StringBuilder pinyinStr = new StringBuilder();
        //如果是多音字需要手动纠正
        if (chinese.equals("重庆")) {
            pinyinStr.append("CHONGQING");
        } else {
            char[] newChar = chinese.toCharArray();  //转为单个字符
            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
            defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            for (char c : newChar) {
                if (c > 128) {
                    try {
                        pinyinStr.append(PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat)[0].charAt(0));
                    } catch (BadHanyuPinyinOutputFormatCombination e) {
                        e.printStackTrace();
                    }
                } else {
                    pinyinStr.append(c);
                }
            }
        }
        return pinyinStr.toString();
    }
}
