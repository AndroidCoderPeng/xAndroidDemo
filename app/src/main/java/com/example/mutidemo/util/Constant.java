package com.example.mutidemo.util;

import android.net.wifi.WifiManager;

public class Constant {
    public static final String[] NET_ACTION = {"android.net.ethernet.ETHERNET_STATE_CHANGED",
            "android.net.ethernet.STATE_CHANGE", "android.net.conn.CONNECTIVITY_CHANGE",
            "android.net.wifi.WIFI_STATE_CHANGED", "android.net.wifi.STATE_CHANGE",
            WifiManager.NETWORK_STATE_CHANGED_ACTION};

    //retrofit请求baseurl只能是根url，不能带任何参数
    public static final String WEATHER_URL = "https://way.jd.com/";
}
