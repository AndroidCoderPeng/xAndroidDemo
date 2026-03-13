package com.example.android.webrtc.model

/**
 * 对端信息
 * */
data class PeerInfo(val userId: String, val userName: String, val isOnline: Boolean = false)
