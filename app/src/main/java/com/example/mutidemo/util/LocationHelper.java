package com.example.mutidemo.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.example.mutidemo.util.callback.IAddressListener;
import com.example.mutidemo.util.callback.ILocationListener;

import org.jetbrains.annotations.NotNull;

public class LocationHelper {
    private static final String TAG = "LocationHelper";
    private static final double pi = 3.14159265358979324;
    private static final double a = 6378245.0; //  a: 卫星椭球坐标投影到平面地图坐标系的投影因子。
    private static final double ee = 0.00669342162296594323; //  ee: 椭球的偏心率平方。

    /**
     * 高德sdk定位
     */
    public static void obtainCurrentLocationByGD(Context context, ILocationListener listener, boolean isOnce) {
        AMapLocationClient mLocationClient = new AMapLocationClient(context);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，AMapLocationMode.Battery_Saving为低功耗模式，AMapLocationMode.Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setNeedAddress(true);//设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setOnceLocation(isOnce);//设置是否只定位一次,默认为false
        mLocationOption.setWifiActiveScan(true);//设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setMockEnable(false);//设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setInterval(15000);//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mLocationOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //设置定位回调监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        listener.onAMapLocationGet(aMapLocation);
//                        mLocationClient.stopLocation();//停止定位
                    } else {
                        listener.onAMapLocationGet(null);
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Log.e(TAG, "location Error, ErrCode:" + aMapLocation.getErrorCode()
                                + ", errInfo:" + aMapLocation.getErrorInfo());
                    }
                }
            }
        });
        //启动定位
        mLocationClient.startLocation();
    }

    /**
     * 原生GSP获取当前定位
     */
    @SuppressLint("MissingPermission")
    public static void obtainCurrentLocation(Context context, ILocationListener listener, boolean isOnce) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (isOnce) {
            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            listener.onLocationGet(location);
        } else {
            //位置变化时更新位置
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30 * 1000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(@NotNull Location location) {
                    listener.onLocationGet(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(@NotNull String provider) {

                }

                @Override
                public void onProviderDisabled(@NotNull String provider) {

                }
            });
        }
    }

    /**
     * 经纬度反编码为地址
     */
    public static void antiCodingLocation(Context context, double lng, double lat, IAddressListener listener) throws AMapException {
        if (isOutOfChina(lng, lat)) {
            listener.onGetAddress("经纬度异常");
        }
        GeocodeSearch codeSearch = new GeocodeSearch(context);
        // 第一个参数表示一个LatLonPoint，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(lat, lng), 50, GeocodeSearch.GPS);
        codeSearch.getFromLocationAsyn(query);
        codeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
                if (regeocodeAddress != null) {
                    listener.onGetAddress(regeocodeAddress.getFormatAddress());
                } else {
                    listener.onGetAddress("解析位置失败");
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
    }

    /**
     * check if the point in china
     * 72.004 <= lng <= 137.8347 and 0.8293 <= lat <= 55.8271
     */
    private static boolean isOutOfChina(double lng, double lat) {
        if (lng < 72.004 || lng > 137.8347) {
            return lat < 0.8293 || lat > 55.8271;
        }
        return false;
    }

    public static double[] gcjToWgs(double gcjLng, double gcjLat) {
        if (isOutOfChina(gcjLng, gcjLat)) {
            return new double[]{gcjLng, gcjLat};
        }
        double dLng = transformLon(gcjLng - 105.0, gcjLat - 35.0);
        double dLat = transformLat(gcjLng - 105.0, gcjLat - 35.0);
        double radLat = gcjLat / 180.0 * pi;
        double sinLat = Math.sin(radLat);
        sinLat = 1 - ee * sinLat * sinLat;
        double sinLatSqrt = Math.sqrt(sinLat);
        dLng = (dLng * 180.0) / (a / sinLatSqrt * Math.cos(radLat) * pi);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (sinLat * sinLatSqrt) * pi);
        return new double[]{gcjLng - dLng, gcjLat - dLat};
    }

    private static double transformLon(double lng, double lat) {
        double ret = 300.0 + lat + 2.0 * lng + 0.1 * lat * lat + 0.1 * lat * lng + 0.1 * Math.sqrt(Math.abs(lat));
        ret += (20.0 * Math.sin(6.0 * lat * pi) + 20.0 * Math.sin(2.0 * lat * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * pi) + 40.0 * Math.sin(lat / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lat / 12.0 * pi) + 300.0 * Math.sin(lat / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLat(double lng, double lat) {
        double ret = -100.0 + 2.0 * lat + 3.0 * lng + 0.2 * lng * lng + 0.1 * lat * lng + 0.2 * Math.sqrt(Math.abs(lat));
        ret += (20.0 * Math.sin(6.0 * lat * pi) + 20.0 * Math.sin(2.0 * lat * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * pi) + 40.0 * Math.sin(lng / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lng / 12.0 * pi) + 320 * Math.sin(lng * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }
}
