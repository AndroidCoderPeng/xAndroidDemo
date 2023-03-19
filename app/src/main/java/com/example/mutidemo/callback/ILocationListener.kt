package com.example.mutidemo.callback

import android.location.Location
import com.amap.api.location.AMapLocation

interface ILocationListener {
    fun onLocationGet(location: Location?) //原生GPS定位数据
    fun onAMapLocationGet(aMapLocation: AMapLocation?) //高德定位数据
}