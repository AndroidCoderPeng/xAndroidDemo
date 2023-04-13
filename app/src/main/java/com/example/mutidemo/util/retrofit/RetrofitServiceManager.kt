package com.example.mutidemo.util.retrofit

import com.example.mutidemo.util.DemoConstant
import com.pengxh.kt.lite.utils.RetrofitFactory

object RetrofitServiceManager {

    private val api by lazy {
        val httpConfig = "https://way.jd.com"
        RetrofitFactory.createRetrofit<RetrofitService>(httpConfig)
    }

    suspend fun getNewsList(channel: String, start: Int): String {
        return api.getNewsList(DemoConstant.APP_KEY, channel, 20, start)
    }
}