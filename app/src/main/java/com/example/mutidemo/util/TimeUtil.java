package com.example.mutidemo.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/3/30 15:06
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {

    private static final String FORMAT_ONE = "yyyyMMddHHmmss";
    private static final SimpleDateFormat millsFormat = new SimpleDateFormat("mm:ss");

    public static long transformTime() {
        String format = new SimpleDateFormat(FORMAT_ONE).format(new Date());
        return Long.parseLong(format);
    }

    /**
     * 时间戳转时间
     */
    public static String millsToTime(long millSeconds) {
        return millsFormat.format(new Date(millSeconds));
    }
}
