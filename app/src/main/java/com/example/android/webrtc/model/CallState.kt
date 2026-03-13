package com.example.android.webrtc.model

/**
 * 通话状态枚举
 * */
enum class CallState {
    IDLE,           // 空闲状态
    CALLING,        // 正在呼叫
    INCOMING,       // 来电中
    CONNECTED,      // 已连接
    DISCONNECTED,   // 已断开
    ERROR           // 错误状态
}