package com.example.multidemo.util.retrofit

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitService {
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

    /**
     * 提交算法区域
     */
    @POST("/set_position")
    suspend fun postRegion(@Body requestBody: RequestBody): String
}