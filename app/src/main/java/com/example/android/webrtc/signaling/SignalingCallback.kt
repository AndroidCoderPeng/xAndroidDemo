package com.example.android.webrtc.signaling

/**
 * 信令回调接口
 * */
interface SignalingCallback {
    /**
     * WebSocket 连接成功
     */
    fun onConnected()

    /**
     * WebSocket 连接断开
     */
    fun onDisconnected()

    /**
     * WebSocket 连接错误
     */
    fun onError(error: String)

    /**
     * 收到呼叫请求
     */
    fun onCallRequest(callerId: String)

    /**
     * 呼叫被接受
     */
    fun onCallAccepted(peerId: String)

    /**
     * 呼叫被拒绝
     */
    fun onCallRejected(peerId: String)

    /**
     * 收到结束通话
     */
    fun onCallEnded(peerId: String)

    /**
     * 收到 SDP Offer
     */
    fun onOfferReceived(sdp: String, senderId: String)

    /**
     * 收到 SDP Answer
     */
    fun onAnswerReceived(sdp: String, senderId: String)

    /**
     * 收到 ICE Candidate
     */
    fun onIceCandidateReceived(
        sdpMid: String,
        sdpMLineIndex: Int,
        candidate: String,
        senderId: String
    )

    /**
     * 收到心跳响应
     */
    fun onHeartbeatReceived()
}