package com.example.multidemo.base

import android.app.Application
import kotlin.properties.Delegates

class BaseApplication : Application() {

    companion object {
        private var application: BaseApplication by Delegates.notNull()

        fun get() = application
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }
}