package com.example.android.retrofit

import com.example.android.util.ExampleConstant
import com.pengxh.kt.lite.utils.RetrofitFactory

object RetrofitServiceManager {
    private val deviceControlRetrofit by lazy {
        RetrofitFactory.createRetrofit<RetrofitService>(
            ExampleConstant.PTZ_CONTROL_SERVER_URL, 30, true
        )
    }

    /**
     * 执行PTZ控制指令
     */
    suspend fun executePtzCommand(action: String, speed: Int): String {
        return deviceControlRetrofit.executePtzCommand(
            action,
            ExampleConstant.PTZ_ACCOUNT,
            ExampleConstant.PTZ_PASSWORD,
            speed.toString()
        )
    }

    /**
     * 执行PTZ预置点指令
     */
    suspend fun executePreset(action: String, preset: Int): String {
        return deviceControlRetrofit.executePreset(
            action,
            ExampleConstant.PTZ_ACCOUNT,
            ExampleConstant.PTZ_PASSWORD,
            preset.toString()
        )
    }
}