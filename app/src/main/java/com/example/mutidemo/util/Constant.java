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
    public static final String BASE_WEATHER_URL = "https://way.jd.com/";

    //新闻api地址
    /**
     * https://route.showapi.com/109-35?channelId=57463656a44a13cf&channelName=旅游最新&maxResult=20&needAllList=1&needHtml=1&page=1&showapi_appid=28258&showapi_timestamp=20200330100657&showapi_sign=a733bb6e48531a114393ccef8073c00c
     */
    public static final String BASE_NEWS_URL = "https://route.showapi.com/";
    public static final String API_SIGN = "0db25ea1889a4b7a9e12956478769f78";
    public static final String API_ID = "166496";

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
