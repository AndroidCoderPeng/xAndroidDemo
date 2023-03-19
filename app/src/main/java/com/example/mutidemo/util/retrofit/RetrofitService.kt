package com.example.mutidemo.util.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    /**
     * 天气详情
     * https://way.jd.com/jisuapi/weather?city=北京&cityid=1&citycode=101010100&appkey=e957ed7ad90436a57e604127d9d8fa32
     */
    @GET("/jisuapi/weather")
    suspend fun getWeatherDetail(
        @Query("appkey") appkey: String,
        @Query("city") city: String,
        @Query("cityid") cityid: Int,
        @Query("citycode") citycode: Int
    ): String

    /**
     * https://way.jd.com/jisuapi/get?channel=头条&num=10&start=0&appkey=e957ed7ad90436a57e604127d9d8fa32
     */
    @GET("/jisuapi/get")
    suspend fun getNewsList(
        @Query("appkey") appkey: String,
        @Query("channel") channel: String,
        @Query("num") num: Int,
        @Query("start") start: Int
    ): String
}