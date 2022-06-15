package com.example.mutidemo.mvvm.retrofit

import com.example.mutidemo.mvvm.model.WeatherModel
import com.example.mutidemo.util.DemoConstant

object RetrofitServiceManager {

    private val api by lazy { RetrofitFactory.createRetrofit(RetrofitService::class.java) }

    /**
     * 获取巡检详情
     */
    suspend fun obtainWeatherData(city: String, cityId: Int, cityCode: Int): WeatherModel {
        return api.obtainWeatherData(
            appkey = DemoConstant.APP_KEY,
            city = city,
            cityid = cityId,
            citycode = cityCode
        )
    }
}