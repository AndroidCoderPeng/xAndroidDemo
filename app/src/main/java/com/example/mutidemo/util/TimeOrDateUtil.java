package com.example.mutidemo.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author : Pengxh
 * @time : 2021/4/15 16:54
 * @email : 290677893@qq.com
 **/
public class TimeOrDateUtil {
    private static final SimpleDateFormat allDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
    private static final SimpleDateFormat minuteFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
    private static final SimpleDateFormat secondsFormat = new SimpleDateFormat("mm:ss", Locale.CHINA);

    /**
     * 时间戳转完整日期时间
     */
    public static String timestampToCompleteDate(long millSeconds) {
        return allDateFormat.format(new Date(millSeconds));
    }

    /**
     * 时间戳转日期
     */
    public static String timestampToDate(long millSeconds) {
        return dateFormat.format(new Date(millSeconds));
    }

    /**
     * 时间戳转时间
     */
    public static String timestampToTime(long millSeconds) {
        return timeFormat.format(new Date(millSeconds));
    }

    /**
     * 时间戳转时分
     */
    public static String minuteToTime(long millSeconds) {
        return minuteFormat.format(new Date(millSeconds));
    }

    /**
     * 时间戳转分秒
     */
    public static String millsToTime(long millSeconds) {
        return secondsFormat.format(new Date(millSeconds));
    }
}
