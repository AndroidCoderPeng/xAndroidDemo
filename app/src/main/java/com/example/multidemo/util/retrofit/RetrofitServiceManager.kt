package com.example.multidemo.util.retrofit

import com.example.multidemo.util.DemoConstant
import com.pengxh.kt.lite.utils.RetrofitFactory

object RetrofitServiceManager {

    private val api by lazy {
        val httpConfig = "https://way.jd.com"
        RetrofitFactory.createRetrofit<RetrofitService>(httpConfig)
    }

    private val crawlerApi by lazy {
        val httpConfig = "http://192.168.3.2:8080"
        RetrofitFactory.createRetrofit<RetrofitService>(httpConfig)
    }

    suspend fun getNewsByPage(channel: String, start: Int): String {
        return api.getNewsByPage(DemoConstant.APP_KEY, channel, 10, start)
    }
}