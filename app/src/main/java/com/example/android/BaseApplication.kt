package com.example.android

import android.app.Application
import com.pengxh.kt.lite.utils.SaveKeyValues

class BaseApplication : Application() {

    companion object {
        private lateinit var application: BaseApplication

        fun get(): BaseApplication = application

        internal fun initApplication(app: BaseApplication) {
            application = app
        }
    }

    override fun onCreate() {
        super.onCreate()
        initApplication(this)
        SaveKeyValues.initSharedPreferences(this)
    }
}