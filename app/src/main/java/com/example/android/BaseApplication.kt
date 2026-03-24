package com.example.android

import android.app.Application
import com.pengxh.kt.lite.utils.SaveKeyValues
import kotlin.properties.Delegates

class BaseApplication : Application() {

    companion object {
        private var application: BaseApplication by Delegates.notNull()

        fun get() = application
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        SaveKeyValues.initSharedPreferences(this)
    }
}