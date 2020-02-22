package com.example.mutidemo.util;

import android.net.wifi.WifiManager;

public class Constant {
    public static final String[] NET_ACTION = {"android.net.ethernet.ETHERNET_STATE_CHANGED",
            "android.net.ethernet.STATE_CHANGE", "android.net.conn.CONNECTIVITY_CHANGE",
            "android.net.wifi.WIFI_STATE_CHANGED", "android.net.wifi.STATE_CHANGE",
            WifiManager.NETWORK_STATE_CHANGED_ACTION};

    //retrofit请求baseurl只能是根url，不能带任何参数
    public static final String BASE_URL = "https://way.jd.com/";

    //新闻api地址
    /**
     * https://way.jd.com/jisuapi/get?channel=头条&num=10&start=0&appkey=e957ed7ad90436a57e604127d9d8fa32
     */
    public static final String NEWS_URL = BASE_URL + "jisuapi/get?channel=头条&num=10&start=" + "pageNum" + "&appkey=e957ed7ad90436a57e604127d9d8fa32";
}
