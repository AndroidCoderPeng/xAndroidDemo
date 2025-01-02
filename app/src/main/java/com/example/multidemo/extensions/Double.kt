package com.example.multidemo.extensions

import kotlin.math.floor

/**
 * 经纬度转度分格式，ddmm.mmmm
 *
 * longitude: 116.25980833333332
 * latitude: 39.912785
 *
 * 转换后的纬度 = dd + (mm.mmmm/60)
 * 转换后的经度 = ddd + (mm.mmmm/60)
 * */
fun Double.convert(): String {
    //整数部分，即度数
    val degree = this.toInt()

    //小数部分
    val temp = this - degree
    val mm = "%.4f".format(temp * 60)

    return "$degree$mm"
}

/**
 * 经纬度转度分秒
 * */
fun Double.toDegree(): String {
    val degrees = floor(this).toInt()
    val minutes = ((this - degrees) * 60).toInt()
    val seconds = ((this - degrees) * 60 - minutes) * 60
    val formattedSeconds = "%.3f".format(seconds)
    return "$degrees°$minutes'$formattedSeconds\""
}