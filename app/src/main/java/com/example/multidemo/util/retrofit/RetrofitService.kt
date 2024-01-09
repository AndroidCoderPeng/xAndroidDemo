package com.example.multidemo.util.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    @GET("/news/get")
    suspend fun getNewsByPage(
        @Query("channel") channel: String,
        @Query("start") start: Int,
        @Query("num") num: Int,
        @Query("appkey") appkey: String,
    ): String
}