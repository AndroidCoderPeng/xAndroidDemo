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

    /**
     * 时间戳转时间
     */
    public static String millsToTime(String formatStr) {
        SimpleDateFormat millsFormat = new SimpleDateFormat(formatStr);
        return millsFormat.format(new Date());
    }
}
