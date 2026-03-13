package com.example.android.webrtc.signaling

import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

/**
 * 信令消息数据模型
 * */
enum class MessageType {
    CALL_REQUEST,      // 呼叫请求
    CALL_ACCEPT,       // 接受呼叫
    CALL_REJECT,       // 拒绝呼叫
    CALL_END,          // 结束通话
    OFFER,             // SDP Offer
    ANSWER,            // SDP Answer
    ICE_CANDIDATE,     // ICE 候选
    HEARTBEAT          // 心跳保活
}

/**
 * 信令消息数据模型
 */
data class SignalingMessage(
    val type: MessageType,
    val senderId: String,
    val receiverId: String,
    val sdpType: String? = null,           // SDP 类型: "offer" 或 "answer"
    val sdp: String? = null,               // SDP 内容
    val iceSdpMid: String? = null,         // ICE 候选的 sdpMid
    val iceSdpMLineIndex: Int? = null,     // ICE 候选的 sdpMLineIndex
    val iceCandidate: String? = null,      // ICE 候选内容
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        /**
         * 从 SessionDescription 创建 Offer 消息
         */
        fun createOffer(
            senderId: String,
            receiverId: String,
            sdp: SessionDescription
        ): SignalingMessage {
            return SignalingMessage(
                type = MessageType.OFFER,
                senderId = senderId,
                receiverId = receiverId,
                sdpType = sdp.type.canonicalForm(),
                sdp = sdp.description
            )
        }

        /**
         * 从 SessionDescription 创建 Answer 消息
         */
        fun createAnswer(
            senderId: String,
            receiverId: String,
            sdp: SessionDescription
        ): SignalingMessage {
            return SignalingMessage(
                type = MessageType.ANSWER,
                senderId = senderId,
                receiverId = receiverId,
                sdpType = sdp.type.canonicalForm(),
                sdp = sdp.description
            )
        }

        /**
         * 从 IceCandidate 创建 ICE Candidate 消息
         */
        fun createIceCandidate(
            senderId: String,
            receiverId: String,
            candidate: IceCandidate
        ): SignalingMessage {
            return SignalingMessage(
                type = MessageType.ICE_CANDIDATE,
                senderId = senderId,
                receiverId = receiverId,
                iceSdpMid = candidate.sdpMid,
                iceSdpMLineIndex = candidate.sdpMLineIndex,
                iceCandidate = candidate.sdp
            )
        }

        /**
         * 创建呼叫请求消息
         */
        fun createCallRequest(senderId: String, receiverId: String): SignalingMessage {
            return SignalingMessage(
                type = MessageType.CALL_REQUEST,
                senderId = senderId,
                receiverId = receiverId
            )
        }

        /**
         * 创建接受呼叫消息
         */
        fun createCallAccept(senderId: String, receiverId: String): SignalingMessage {
            return SignalingMessage(
                type = MessageType.CALL_ACCEPT,
                senderId = senderId,
                receiverId = receiverId
            )
        }

        /**
         * 创建拒绝呼叫消息
         */
        fun createCallReject(senderId: String, receiverId: String): SignalingMessage {
            return SignalingMessage(
                type = MessageType.CALL_REJECT,
                senderId = senderId,
                receiverId = receiverId
            )
        }

        /**
         * 创建结束通话消息
         */
        fun createCallEnd(senderId: String, receiverId: String): SignalingMessage {
            return SignalingMessage(
                type = MessageType.CALL_END,
                senderId = senderId,
                receiverId = receiverId
            )
        }

        /**
         * 创建心跳消息
         */
        fun createHeartbeat(senderId: String): SignalingMessage {
            return SignalingMessage(
                type = MessageType.HEARTBEAT,
                senderId = senderId,
                receiverId = ""
            )
        }
    }
}