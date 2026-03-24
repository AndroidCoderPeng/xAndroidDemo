package com.example.android.base.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    /**
     * 执行PTZ控制指令
     */
    @GET("/cgi-bin/ptz_cgi")
    suspend fun executePtzCommand(
        @Query("action") action: String,
        @Query("user") user: String,
        @Query("pwd") pwd: String,
        @Query("Speed") speed: String
    ): String

    /**
     * 执行PTZ预置点指令
     */
    @GET("/cgi-bin/ptz_cgi")
    suspend fun executePreset(
        @Query("action") action: String,
        @Query("user") user: String,
        @Query("pwd") pwd: String,
        @Query("Preset") preset: String
    ): String
}