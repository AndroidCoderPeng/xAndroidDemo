package com.example.multidemo.util.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    /**
     * https://way.jd.com/jisuapi/get?channel=头条&num=10&start=0&appkey=e957ed7ad90436a57e604127d9d8fa32
     */
    @GET("/jisuapi/get")
    suspend fun getNewsByPage(
        @Query("appkey") appkey: String,
        @Query("channel") channel: String,
        @Query("num") num: Int,
        @Query("start") start: Int
    ): String
}