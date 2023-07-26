package com.example.mutidemo.base

import android.app.Application
import android.util.Log
import com.example.mutidemo.util.FileUtils
import com.igexin.sdk.PushManager
import kotlin.properties.Delegates

class BaseApplication : Application() {

    companion object {
        private var application: BaseApplication by Delegates.notNull()

        fun get() = application
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        //个推初始化
        PushManager.getInstance().initialize(this)
        PushManager.getInstance().setDebugLogger(this) { s -> Log.d("BaseApplication", s) }
        FileUtils.initFileConfig(this)
    }
}