package com.example.mutidemo.util;

import android.net.wifi.WifiManager;

import java.util.Arrays;
import java.util.List;

public class Constant {
    public static final String[] NET_ACTION = {"android.net.ethernet.ETHERNET_STATE_CHANGED",
            "android.net.ethernet.STATE_CHANGE", "android.net.conn.CONNECTIVITY_CHANGE",
            "android.net.wifi.WIFI_STATE_CHANGED", "android.net.wifi.STATE_CHANGE",
            WifiManager.NETWORK_STATE_CHANGED_ACTION};

    public static final String APP_KEY = "5e413d150cafb2802f00000d";
    public static final String UMENG_MESSAGE_SECRET = "28f5f328182850764fb00a940c7943af";

    //retrofit请求baseurl只能是根url，不能带任何参数
    public static final String BASE_URL = "https://way.jd.com/";

    //新闻api地址
    /**
     * https://way.jd.com/jisuapi/get?channel=头条&num=10&start=0&appkey=e957ed7ad90436a57e604127d9d8fa32
     */
    public static final String NEWS_URL = BASE_URL + "jisuapi/get?channel=头条&num=10&start=" + "pageNum" + "&appkey=e957ed7ad90436a57e604127d9d8fa32";

    /**
     * 测试图片地址
     */
    public static final List<String> IMAGE_URL = Arrays.asList("http://pic1.win4000.com/mobile/2020-02-25/5e54d31f9d31b.jpg",
            "http://pic1.win4000.com/mobile/2020-02-25/5e54d3208c43c.jpg",
            "http://pic1.win4000.com/mobile/2020-02-25/5e54d321a0997.jpg",
            "http://pic1.win4000.com/mobile/2020-02-25/5e54d322bcc21.jpg",
            "http://pic1.win4000.com/mobile/2020-02-25/5e54d32409642.jpg",
            "http://pic1.win4000.com/mobile/2020-02-25/5e54d325c871e.jpg",
            "http://pic1.win4000.com/mobile/2020-02-25/5e54d327691b3.jpg",
            "http://pic1.win4000.com/mobile/2020-02-25/5e54d328b9b50.jpg",
            "http://pic1.win4000.com/mobile/2020-02-25/5e54d32aac620.jpg",
            "http://pic1.win4000.com/mobile/2020-02-06/5e3ba39a30f8b.jpg");
}
