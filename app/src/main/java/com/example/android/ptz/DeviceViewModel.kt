package com.example.android.ptz

import androidx.lifecycle.ViewModel
import com.example.android.base.retrofit.RetrofitServiceManager
import com.pengxh.kt.lite.extensions.launch

class DeviceViewModel : ViewModel() {
    /**
     * 执行PTZ预置点指令
     */
    fun executePreset(action: String, preset: Int) = launch({
        RetrofitServiceManager.executePreset(action, preset)
    })

    /**
     * 执行PTZ控制指令
     */
    fun executePtzCommand(action: String, speed: Int) = launch({
        RetrofitServiceManager.executePtzCommand(action, speed)
    })
}