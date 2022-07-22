package com.example.mutidemo.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONException;
import org.json.JSONObject;

public class StringHelper {
    public static int separateResponseCode(String value) {
        if (value.isEmpty()) {
            return 404;
        }
        int code = 500;
        try {
            code = new JSONObject(value).getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 获取汉语拼音首字母
     */
    public static String obtainHanYuPinyin(String chinese) {
        StringBuilder pinyinStr = new StringBuilder();
        if (chinese.startsWith("0")) {
            pinyinStr.append("LING");
        } else if (chinese.startsWith("1")) {
            pinyinStr.append("YI");
        } else if (chinese.startsWith("2")) {
            pinyinStr.append("ER");
        } else if (chinese.startsWith("3")) {
            pinyinStr.append("SAN");
        } else if (chinese.startsWith("4")) {
            pinyinStr.append("SI");
        } else if (chinese.startsWith("5")) {
            pinyinStr.append("WU");
        } else if (chinese.startsWith("6")) {
            pinyinStr.append("LIU");
        } else if (chinese.startsWith("7")) {
            pinyinStr.append("QI");
        } else if (chinese.startsWith("8")) {
            pinyinStr.append("BA");
        } else if (chinese.startsWith("9")) {
            pinyinStr.append("JIU");
        } else if (chinese.startsWith("重庆")) {
            pinyinStr.append("CHONGQING");
        } else {
            char[] newChar = chinese.toCharArray();
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
        return pinyinStr.substring(0, 1);
    }
}
