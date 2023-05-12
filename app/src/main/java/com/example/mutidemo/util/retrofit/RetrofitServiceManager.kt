package com.example.mutidemo.util.retrofit

import com.example.mutidemo.util.DemoConstant
import com.google.gson.JsonObject
import com.pengxh.kt.lite.utils.RetrofitFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

object RetrofitServiceManager {

    private val api by lazy {
        val httpConfig = "https://way.jd.com"
        RetrofitFactory.createRetrofit<RetrofitService>(httpConfig)
    }

    private val regionApi by lazy {
        val httpConfig = "http://192.168.10.104:5000"
        RetrofitFactory.createRetrofit<RetrofitService>(httpConfig)
    }

    suspend fun getNewsList(channel: String, start: Int): String {
        return api.getNewsList(DemoConstant.APP_KEY, channel, 15, start)
    }

    suspend fun postRegion(code: String, color: String, position: String): String {
        val param = JsonObject()
        param.addProperty("code", code)
        param.addProperty("color", color)
        param.addProperty("position", position)

        val requestBody = param.toString().toRequestBody(
            "application/json;charset=UTF-8".toMediaType()
        )
        return regionApi.postRegion(requestBody)
    }
}