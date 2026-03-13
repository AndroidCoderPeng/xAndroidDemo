package com.example.android.webrtc.core

import org.webrtc.EglBase
import org.webrtc.EglBase.Context

/**
 * 渲染上下文管理
 * */
object EglBaseProvider {
    private var eglBase: EglBase? = null

    /**
     * 初始化 EGL 上下文
     */
    fun init(context: android.content.Context) {
        if (eglBase == null) {
            eglBase = EglBase.create()
        }
    }

    /**
     * 获取 EGL 上下文
     */
    fun getEglBaseContext(): Context? {
        return eglBase?.eglBaseContext
    }

    /**
     * 释放资源
     */
    fun release() {
        eglBase?.release()
        eglBase = null
    }
}