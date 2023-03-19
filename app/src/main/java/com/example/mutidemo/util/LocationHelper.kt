package com.example.mutidemo.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.example.mutidemo.callback.IAddressListener
import com.example.mutidemo.callback.ILocationListener
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object LocationHelper {
    private const val kTag = "LocationHelper"
    private const val pi = 3.14159265358979324
    private const val a = 6378245.0 //  a: 卫星椭球坐标投影到平面地图坐标系的投影因子。
    private const val ee = 0.00669342162296594323 //  ee: 椭球的偏心率平方。

    /**
     * 高德sdk定位
     */
    fun obtainCurrentLocationByGD(context: Context?, listener: ILocationListener, isOnce: Boolean) {
        val mLocationClient = AMapLocationClient(context)
        val mLocationOption = AMapLocationClientOption()
        //设置定位模式为高精度模式，AMapLocationMode.Battery_Saving为低功耗模式，AMapLocationMode.Device_Sensors是仅设备模式
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        mLocationOption.isNeedAddress = true //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.isOnceLocation = isOnce //设置是否只定位一次,默认为false
        mLocationOption.isWifiActiveScan = true //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.isMockEnable = false //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.interval = 15000 //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.isWifiScan =
            true //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mLocationOption.isLocationCacheEnable = true //可选，设置是否使用缓存定位，默认为true
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption)
        //设置定位回调监听
        mLocationClient.setLocationListener { aMapLocation ->
            if (aMapLocation != null) {
                if (aMapLocation.errorCode == 0) {
                    listener.onAMapLocationGet(aMapLocation)
                    //                        mLocationClient.stopLocation();//停止定位
                } else {
                    listener.onAMapLocationGet(null)
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e(
                        kTag, "location Error, ErrCode:" + aMapLocation.errorCode
                                + ", errInfo:" + aMapLocation.errorInfo
                    )
                }
            }
        }
        //启动定位
        mLocationClient.startLocation()
    }

    /**
     * 原生GSP获取当前定位
     */
    @SuppressLint("MissingPermission")
    fun obtainCurrentLocation(context: Context, listener: ILocationListener, isOnce: Boolean) {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (isOnce) {
            val location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            listener.onLocationGet(location)
        } else {
            //位置变化时更新位置
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                (30 * 1000).toLong(), 10f, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        listener.onLocationGet(location)
                    }

                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                })
        }
    }

    /**
     * 经纬度反编码为地址
     */
    fun antiCodingLocation(
        context: Context?,
        lng: Double,
        lat: Double,
        listener: IAddressListener
    ) {
        if (isOutOfChina(lng, lat)) {
            listener.onGetAddress("经纬度异常")
        }
        val codeSearch = GeocodeSearch(context)
        // 第一个参数表示一个LatLonPoint，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        val query = RegeocodeQuery(LatLonPoint(lat, lng), 50f, GeocodeSearch.GPS)
        codeSearch.getFromLocationAsyn(query)
        codeSearch.setOnGeocodeSearchListener(object : OnGeocodeSearchListener {
            override fun onRegeocodeSearched(regeocodeResult: RegeocodeResult, i: Int) {
                val geoCodeAddress = regeocodeResult.regeocodeAddress
                if (geoCodeAddress != null) {
                    listener.onGetAddress(geoCodeAddress.formatAddress)
                } else {
                    listener.onGetAddress("解析位置失败")
                }
            }

            override fun onGeocodeSearched(geocodeResult: GeocodeResult, i: Int) {}
        })
    }

    /**
     * check if the point in china
     * 72.004 <= lng <= 137.8347 and 0.8293 <= lat <= 55.8271
     */
    private fun isOutOfChina(lng: Double, lat: Double): Boolean {
        return if (lng < 72.004 || lng > 137.8347) {
            lat < 0.8293 || lat > 55.8271
        } else false
    }

    fun gcjToWgs(gcjLng: Double, gcjLat: Double): DoubleArray {
        if (isOutOfChina(gcjLng, gcjLat)) {
            return doubleArrayOf(gcjLng, gcjLat)
        }
        var dLng = transformLon(gcjLng - 105.0, gcjLat - 35.0)
        var dLat = transformLat(gcjLng - 105.0, gcjLat - 35.0)
        val radLat = gcjLat / 180.0 * pi
        var sinLat = sin(radLat)
        sinLat = 1 - ee * sinLat * sinLat
        val sinLatSqrt = sqrt(sinLat)
        dLng = dLng * 180.0 / (a / sinLatSqrt * cos(radLat) * pi)
        dLat = dLat * 180.0 / (a * (1 - ee) / (sinLat * sinLatSqrt) * pi)
        return doubleArrayOf(gcjLng - dLng, gcjLat - dLat)
    }

    private fun transformLon(lng: Double, lat: Double): Double {
        var ret = 300.0 + lat + 2.0 * lng + 0.1 * lat * lat + 0.1 * lat * lng + 0.1 * sqrt(
            abs(lat)
        )
        ret += (20.0 * sin(6.0 * lat * pi) + 20.0 * sin(2.0 * lat * pi)) * 2.0 / 3.0
        ret += (20.0 * sin(lat * pi) + 40.0 * sin(lat / 3.0 * pi)) * 2.0 / 3.0
        ret += (150.0 * sin(lat / 12.0 * pi) + 300.0 * sin(lat / 30.0 * pi)) * 2.0 / 3.0
        return ret
    }

    private fun transformLat(lng: Double, lat: Double): Double {
        var ret =
            -100.0 + 2.0 * lat + 3.0 * lng + 0.2 * lng * lng + 0.1 * lat * lng + 0.2 * sqrt(abs(lat))
        ret += (20.0 * sin(6.0 * lat * pi) + 20.0 * sin(2.0 * lat * pi)) * 2.0 / 3.0
        ret += (20.0 * sin(lng * pi) + 40.0 * sin(lng / 3.0 * pi)) * 2.0 / 3.0
        ret += (160.0 * sin(lng / 12.0 * pi) + 320 * sin(lng * pi / 30.0)) * 2.0 / 3.0
        return ret
    }
}