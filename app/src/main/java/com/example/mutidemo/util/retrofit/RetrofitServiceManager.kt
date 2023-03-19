package com.example.mutidemo.util.retrofit

import com.example.mutidemo.util.DemoConstant
import com.pengxh.kt.lite.utils.RetrofitFactory
import com.pengxh.kt.lite.utils.SaveKeyValues

object RetrofitServiceManager {

    private val api by lazy {
        val httpConfig = SaveKeyValues.getValue(
            DemoConstant.BASE_URL, "https://way.jd.com"
        ) as String
        RetrofitFactory.createRetrofit<RetrofitService>(httpConfig)
    }

    suspend fun getWeatherDetail(city: String, cityId: Int, cityCode: Int): String {
        return api.getWeatherDetail(DemoConstant.APP_KEY, city, cityId, cityCode)
    }

    suspend fun getNewsList(channel: String, start: Int): String {
        return api.getNewsList(DemoConstant.APP_KEY, channel, 20, start)
    }
}