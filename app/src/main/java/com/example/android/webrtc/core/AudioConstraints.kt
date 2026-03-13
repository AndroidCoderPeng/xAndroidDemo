package com.example.android.webrtc.core

import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection

/**
 * 音频约束配置
 * */
object AudioConstraints {
    /**
     * 创建音频约束
     */
    fun createAudioConstraints(): MediaConstraints {
        val constraints = MediaConstraints()

        // 强制音频
        constraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))

        // 禁用视频
        constraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"))

        // 音频优化选项
        constraints.mandatory.add(MediaConstraints.KeyValuePair("echoCancellation", "true"))
        constraints.mandatory.add(MediaConstraints.KeyValuePair("noiseSuppression", "true"))
        constraints.mandatory.add(MediaConstraints.KeyValuePair("autoGainControl", "true"))
        constraints.mandatory.add(MediaConstraints.KeyValuePair("highpassFilter", "true"))

        // 音频编码设置
        constraints.optional.add(MediaConstraints.KeyValuePair("googEchoCancellation", "true"))
        constraints.optional.add(MediaConstraints.KeyValuePair("googNoiseSuppression", "true"))
        constraints.optional.add(MediaConstraints.KeyValuePair("googHighpassFilter", "true"))
        constraints.optional.add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))

        return constraints
    }

    /**
     * 创建 PeerConnection 约束
     */
    fun createPeerConnectionConstraints(): MediaConstraints {
        val constraints = MediaConstraints()

        // DTLS/SRTP 加密
        constraints.mandatory.add(MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"))

        // 可选配置
        constraints.optional.add(MediaConstraints.KeyValuePair("RtpDataChannels", "false"))
        return constraints
    }

    /**
     * 创建 ICE 服务器配置
     */
    fun createIceServers(): List<PeerConnection.IceServer> {
        val iceServers = mutableListOf<PeerConnection.IceServer>()

        // Google 公共 STUN 服务器
        iceServers.add(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302")
                .createIceServer()
        )
        iceServers.add(
            PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302")
                .createIceServer()
        )
        iceServers.add(
            PeerConnection.IceServer.builder("stun:stun2.l.google.com:19302")
                .createIceServer()
        )
        iceServers.add(
            PeerConnection.IceServer.builder("stun:stun3.l.google.com:19302")
                .createIceServer()
        )
        iceServers.add(
            PeerConnection.IceServer.builder("stun:stun4.l.google.com:19302")
                .createIceServer()
        )

        // 如果有 TURN 服务器，可以在这里添加
        // iceServers.add(org.webrtc.PeerConnection.IceServer.builder("turn:your-turn-server.com:3478")
        //     .setUsername("username")
        //     .setPassword("password")
        //     .createIceServer())

        return iceServers
    }
}