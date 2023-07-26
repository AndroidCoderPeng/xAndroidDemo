package com.example.multidemo.util

import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination

object StringHelper {
    /**
     * 获取汉语拼音首字母
     */
    fun obtainHanYuPinyin(chinese: String): String {
        val pinyinStr = StringBuilder()
        if (chinese.startsWith("0")) {
            pinyinStr.append("LING")
        } else if (chinese.startsWith("1")) {
            pinyinStr.append("YI")
        } else if (chinese.startsWith("2")) {
            pinyinStr.append("ER")
        } else if (chinese.startsWith("3")) {
            pinyinStr.append("SAN")
        } else if (chinese.startsWith("4")) {
            pinyinStr.append("SI")
        } else if (chinese.startsWith("5")) {
            pinyinStr.append("WU")
        } else if (chinese.startsWith("6")) {
            pinyinStr.append("LIU")
        } else if (chinese.startsWith("7")) {
            pinyinStr.append("QI")
        } else if (chinese.startsWith("8")) {
            pinyinStr.append("BA")
        } else if (chinese.startsWith("9")) {
            pinyinStr.append("JIU")
        } else if (chinese.startsWith("重庆")) {
            pinyinStr.append("CHONGQING")
        } else {
            val newChar = chinese.toCharArray()
            val defaultFormat = HanyuPinyinOutputFormat()
            defaultFormat.caseType = HanyuPinyinCaseType.UPPERCASE
            defaultFormat.toneType = HanyuPinyinToneType.WITHOUT_TONE
            for (c in newChar) {
                if (c.code > 128) {
                    try {
                        pinyinStr.append(
                            PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat)[0][0]
                        )
                    } catch (e: BadHanyuPinyinOutputFormatCombination) {
                        e.printStackTrace()
                    }
                } else {
                    pinyinStr.append(c)
                }
            }
        }
        return pinyinStr.substring(0, 1)
    }
}