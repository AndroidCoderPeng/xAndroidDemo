package com.example.android.view

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.android.databinding.ActivityWebrtcBinding
import com.example.android.webrtc.core.WebRtcManager
import com.example.android.webrtc.model.CallState
import com.example.android.webrtc.service.CallService
import com.example.android.webrtc.signaling.SignalingCallback
import com.example.android.webrtc.signaling.SignalingMessage
import com.example.android.webrtc.signaling.WebSocketClient
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.show
import kotlinx.coroutines.launch
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

class WebRtcActivity : KotlinBaseActivity<ActivityWebrtcBinding>() {
    private val context = this

    // 用户 ID（实际项目中应该从服务器获取或生成）
    private val userId = "user_${System.currentTimeMillis()}"

    // 组件
    private var webRtcManager: WebRtcManager? = null
    private var webSocketClient: WebSocketClient? = null

    // 对端 ID
    private var remotePeerId: String? = null
    private var isCaller = false

    // 信令服务器地址（请替换为你的实际服务器地址）
    private val signalingServerUrl = "ws://your-signaling-server.com:8080"

    override fun initViewBinding(): ActivityWebrtcBinding {
        return ActivityWebrtcBinding.inflate(layoutInflater)
    }

    override fun setupTopBarLayout() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        binding.userIdView.text = "我的ID: $userId"
        updateUIState(CallState.IDLE)

        initWebRTC()
        initSignaling()
    }

    override fun observeRequestState() {

    }

    override fun initEvent() {
        binding.callButton.setOnClickListener {
            val peerId = binding.peerIdView.text.toString().trim()
            if (peerId.isEmpty()) {
                "请输入对方ID".show(this)
                return@setOnClickListener
            }

            if (peerId == userId) {
                "不能呼叫自己".show(this)
                return@setOnClickListener
            }

            startCall(peerId)
        }

        binding.hangupButton.setOnClickListener {
            endCall()
        }
    }

    private fun initWebRTC() {
        webRtcManager = WebRtcManager.get(this)

        // 初始化
        webRtcManager?.initialize { success ->
            if (success) {
                "WebRTC 初始化成功".show(this)
            } else {
                "WebRTC 初始化失败".show(this)
            }
        }

        // 监听通话状态
        webRtcManager?.setOnCallStateChangedListener { state ->
            lifecycleScope.launch {
                updateUIState(state)
            }
        }

        // 监听错误
        webRtcManager?.setOnErrorListener { error ->
            "错误: $error".show(this)
        }
    }

    private fun initSignaling() {
        webSocketClient = WebSocketClient.get(
            signalingServerUrl,
            userId,
            createSignalingCallback()
        )

        // 连接到信令服务器
        webSocketClient?.connectPlatform()
    }

    /**
     * 创建信令回调
     */
    private fun createSignalingCallback(): SignalingCallback {
        return object : SignalingCallback {
            override fun onConnected() {
                "已连接到信令服务器".show(context)
            }

            override fun onDisconnected() {
                "与信令服务器断开连接".show(context)
            }

            override fun onError(error: String) {
                "信令错误: $error".show(context)
            }

            override fun onCallRequest(callerId: String) {
                "收到来自 $callerId 的呼叫".show(context)
                remotePeerId = callerId
                isCaller = false

                // 创建 PeerConnection
                webRtcManager?.createPeerConnection { iceCandidate ->
                    sendIceCandidate(callerId, iceCandidate)
                }

                // 更新 UI
                updateUIState(CallState.INCOMING)

                // 自动接受通话（可以改为让用户选择）
                acceptCall(callerId)
            }

            override fun onCallAccepted(peerId: String) {
                "$peerId 接受了呼叫".show(context)
                remotePeerId = peerId

                // 创建 Offer
                webRtcManager?.createOffer { sdp ->
                    sendSdpOffer(peerId, sdp)
                }
            }

            override fun onCallRejected(peerId: String) {
                "$peerId 拒绝了呼叫".show(context)
                updateUIState(CallState.IDLE)
            }

            override fun onCallEnded(peerId: String) {
                "$peerId 结束了通话".show(context)
                webRtcManager?.endCall()
                updateUIState(CallState.IDLE)
            }

            override fun onOfferReceived(sdp: String, senderId: String) {
                remotePeerId = senderId

                // 设置远程描述
                val sessionDescription = SessionDescription(
                    SessionDescription.Type.OFFER,
                    sdp
                )
                webRtcManager?.setRemoteDescription(sessionDescription) {
                    // 创建 Answer
                    webRtcManager?.createAnswer { answerSdp ->
                        sendSdpAnswer(senderId, answerSdp)
                    }
                }
            }

            override fun onAnswerReceived(sdp: String, senderId: String) {
                val sessionDescription = SessionDescription(
                    SessionDescription.Type.ANSWER,
                    sdp
                )
                webRtcManager?.setRemoteDescription(sessionDescription)
            }

            override fun onIceCandidateReceived(
                sdpMid: String,
                sdpMLineIndex: Int,
                candidate: String,
                senderId: String
            ) {
                val iceCandidate = IceCandidate(sdpMid, sdpMLineIndex, candidate)
                webRtcManager?.addIceCandidate(iceCandidate)
            }

            override fun onHeartbeatReceived() {
                // 心跳响应
            }
        }
    }

    private fun startCall(peerId: String) {
        remotePeerId = peerId
        isCaller = true

        // 创建 PeerConnection
        webRtcManager?.createPeerConnection { iceCandidate ->
            sendIceCandidate(peerId, iceCandidate)
        }?.let { success ->
            if (success) {
                // 发送呼叫请求
                sendCallRequest(peerId)
                updateUIState(CallState.CALLING)
            } else {
                "创建连接失败".show(context)
            }
        }
    }

    /**
     * 接受呼叫
     */
    private fun acceptCall(peerId: String) {
        // 发送接受呼叫
        sendCallAccept(peerId)

        // 启动前台服务
        CallService.startCallService(context, peerId)
    }

    /**
     * 结束通话
     */
    private fun endCall() {
        remotePeerId?.let { peerId ->
            sendCallEnd(peerId)
        }

        webRtcManager?.endCall()
        CallService.stopCallService(context)
        updateUIState(CallState.IDLE)
    }

    /**
     * 发送呼叫请求
     */
    private fun sendCallRequest(peerId: String) {
        val message = SignalingMessage.createCallRequest(userId, peerId)
        webSocketClient?.sendMessage(message)
    }

    /**
     * 发送接受呼叫
     */
    private fun sendCallAccept(peerId: String) {
        val message = SignalingMessage.createCallAccept(userId, peerId)
        webSocketClient?.sendMessage(message)
    }

    /**
     * 发送拒绝呼叫
     */
    private fun sendCallReject(peerId: String) {
        val message = SignalingMessage.createCallReject(userId, peerId)
        webSocketClient?.sendMessage(message)
    }

    /**
     * 发送结束通话
     */
    private fun sendCallEnd(peerId: String) {
        val message = SignalingMessage.createCallEnd(userId, peerId)
        webSocketClient?.sendMessage(message)
    }

    /**
     * 发送 SDP Offer
     */
    private fun sendSdpOffer(peerId: String, sdp: SessionDescription) {
        val message = SignalingMessage.createOffer(userId, peerId, sdp)
        webSocketClient?.sendMessage(message)
    }

    /**
     * 发送 SDP Answer
     */
    private fun sendSdpAnswer(peerId: String, sdp: SessionDescription) {
        val message = SignalingMessage.createAnswer(userId, peerId, sdp)
        webSocketClient?.sendMessage(message)
    }

    /**
     * 发送 ICE Candidate
     */
    private fun sendIceCandidate(peerId: String, candidate: IceCandidate) {
        val message = SignalingMessage.createIceCandidate(userId, peerId, candidate)
        webSocketClient?.sendMessage(message)
    }

    /**
     * 更新 UI 状态
     */
    private fun updateUIState(state: CallState) {
        runOnUiThread {
            when (state) {
                CallState.IDLE -> {
                    binding.callStatusView.text = "空闲"
                    binding.callButton.isEnabled = true
                    binding.hangupButton.isEnabled = false
                    binding.peerIdView.isEnabled = true
                }

                CallState.CALLING -> {
                    binding.callStatusView.text = "正在呼叫..."
                    binding.callButton.isEnabled = false
                    binding.hangupButton.isEnabled = true
                    binding.peerIdView.isEnabled = false
                }

                CallState.INCOMING -> {
                    binding.callStatusView.text = "来电中"
                    binding.callButton.isEnabled = false
                    binding.hangupButton.isEnabled = true
                    binding.peerIdView.isEnabled = false
                }

                CallState.CONNECTED -> {
                    binding.callStatusView.text = "通话中"
                    binding.callButton.isEnabled = false
                    binding.hangupButton.isEnabled = true
                    binding.peerIdView.isEnabled = false
                }

                CallState.DISCONNECTED -> {
                    binding.callStatusView.text = "通话结束"
                    binding.callButton.isEnabled = true
                    binding.hangupButton.isEnabled = false
                    binding.peerIdView.isEnabled = true
                }

                CallState.ERROR -> {
                    binding.callStatusView.text = "通话错误"
                    binding.callButton.isEnabled = true
                    binding.hangupButton.isEnabled = false
                    binding.peerIdView.isEnabled = true
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // 清理资源
        webRtcManager?.endCall()
        webSocketClient?.disconnect()
    }
}