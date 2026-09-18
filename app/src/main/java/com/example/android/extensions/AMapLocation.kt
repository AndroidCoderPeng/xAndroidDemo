package com.example.android.extensions

import com.amap.api.location.AMapLocation

fun AMapLocation.isLocationInChina(): Boolean {
    val minLatitude = 18.0   // 中国最南端约在北纬18°左右
    val maxLatitude = 54.0   // 中国最北端约在北纬54°左右
    val minLongitude = 73.0  // 中国最西端约在东经73°左右
    val maxLongitude = 135.0 // 中国最东端约在东经135°左右

    return (latitude in minLatitude..maxLatitude && longitude >= minLongitude && longitude <= maxLongitude)
}