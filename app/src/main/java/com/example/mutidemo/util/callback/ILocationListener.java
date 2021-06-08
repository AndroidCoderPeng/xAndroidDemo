package com.example.mutidemo.util.callback;

import android.location.Location;

import com.amap.api.location.AMapLocation;

public interface ILocationListener {
    void onLocationGet(Location location);//原生GPS定位数据

    void onAMapLocationGet(AMapLocation aMapLocation);//高德定位数据
}
