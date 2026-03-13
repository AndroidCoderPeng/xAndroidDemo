package com.example.android.webrtc.core

import android.content.Context
import android.util.Log
import com.example.android.webrtc.model.CallState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.webrtc.AudioTrack
import org.webrtc.DataChannel
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

/**
 * WebRTC 核心管理类
 * */
class WebRtcManager private constructor(private val context: Context) {

    private val kTag = "WebRtcManager"

    // WebRTC 核心组件
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null
    private var localAudioTrack: AudioTrack? = null
    private var dataChannel: DataChannel? = null

    // 状态
    private var localStream: MediaStream? = null
    private var isInitialized = false
    private var currentCallState = CallState.IDLE
    private var remotePeerId: String? = null

    // 回调
    private var onCallStateChanged: ((CallState) -> Unit)? = null
    private var onRemoteAudioTrack: ((AudioTrack) -> Unit)? = null
    private var onError: ((String) -> Unit)? = null
    private var onLocalIceCandidate: ((IceCandidate) -> Unit)? = null

    // 协程作用域
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    companion object {
        @Volatile
        private var instance: WebRtcManager? = null

        /**
         * 获取单例实例
         */
        fun get(context: Context): WebRtcManager {
            return instance ?: synchronized(this) {
                instance ?: WebRtcManager(context.applicationContext).also { instance = it }
            }
        }

        /**
         * 销毁实例
         */
        fun destroyInstance() {
            instance?.cleanup()
            instance = null
        }
    }

    /**
     * 初始化 WebRTC
     */
    fun initialize(callback: (Boolean) -> Unit) {
        if (isInitialized) {
            callback(true)
            return
        }

        try {
            // 初始化 EGL 上下文
            EglBaseProvider.init(context)

            // 初始化 PeerConnectionFactory
            initializePeerConnectionFactory()

            isInitialized = true
            Log.d(kTag, "WebRTC initialized successfully")
            callback(true)
        } catch (e: Exception) {
            Log.e(kTag, "Failed to initialize WebRTC", e)
            onError?.invoke("初始化失败: ${e.message}")
            callback(false)
        }
    }

    /**
     * 初始化 PeerConnectionFactory
     */
    private fun initializePeerConnectionFactory() {
        // 编解码器工厂
        val encoderFactory = DefaultVideoEncoderFactory(
            EglBaseProvider.getEglBaseContext(),
            true,
            true
        )
        val decoderFactory = DefaultVideoDecoderFactory(EglBaseProvider.getEglBaseContext())

        // 创建 PeerConnectionFactory
        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true)
            .createInitializationOptions()

        PeerConnectionFactory.initialize(options)

        peerConnectionFactory = PeerConnectionFactory.builder()
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .setOptions(PeerConnectionFactory.Options())
            .createPeerConnectionFactory()

        Log.d(kTag, "PeerConnectionFactory created")
    }

    /**
     * 创建 PeerConnection
     */
    fun createPeerConnection(iceCandidateCallback: (IceCandidate) -> Unit): Boolean {
        return try {
            // 清理旧的连接
            cleanupPeerConnection()

            // 创建 RTCConfiguration
            val rtcConfig = PeerConnection.RTCConfiguration(AudioConstraints.createIceServers())
            rtcConfig.iceTransportsType = PeerConnection.IceTransportsType.ALL
            rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
            rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
            rtcConfig.continualGatheringPolicy =
                PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
            rtcConfig.keyType = PeerConnection.KeyType.ECDSA
            rtcConfig.enableDtlsSrtp = true
            rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN

            // 设置 ICE 候选回调
            onLocalIceCandidate = iceCandidateCallback

            // 创建 PeerConnection
            peerConnection = peerConnectionFactory?.createPeerConnection(
                rtcConfig,
                PeerConnectionObserver(
                    onIceCandidate = { candidate ->
                        Log.d(kTag, "Local ICE candidate: ${candidate.sdp}")
                        onLocalIceCandidate?.invoke(candidate)
                    },
                    onIceConnectionChange = { state ->
                        handleIceConnectionChange(state)
                    },
                    onIceGatheringChange = { state ->
                        Log.d(kTag, "ICE gathering state: $state")
                    },
                    onSignalingChange = { state ->
                        Log.d(kTag, "Signaling state: $state")
                    },
                    onAddTrack = { receiver, stream ->
                        val track = receiver.track()
                        if (track is AudioTrack) {
                            Log.d(kTag, "Remote audio track added")
                            onRemoteAudioTrack?.invoke(track)
                        }
                    },
                    onRenegotiationNeeded = {
                        Log.d(kTag, "Renegotiation needed")
                    }
                )
            )

            // 创建本地音频轨道
            createLocalAudioTrack()

            peerConnection != null
        } catch (e: Exception) {
            Log.e(kTag, "Failed to create PeerConnection", e)
            onError?.invoke("创建连接失败: ${e.message}")
            false
        }
    }

    /**
     * 创建本地音频轨道
     */
    private fun createLocalAudioTrack() {
        try {
            // 获取音频源
            val audioSource =
                peerConnectionFactory?.createAudioSource(AudioConstraints.createAudioConstraints())

            // 创建音频轨道
            localAudioTrack =
                peerConnectionFactory?.createAudioTrack("audio0", audioSource)?.apply {
                    setEnabled(true)
                }

            // 创建本地媒体流
            localStream = peerConnectionFactory?.createLocalMediaStream("localStream")
            localAudioTrack?.let { localStream?.addTrack(it) }

            // 添加到 PeerConnection
            localStream?.let { peerConnection?.addStream(it) }

            Log.d(kTag, "Local audio track created")
        } catch (e: Exception) {
            Log.e(kTag, "Failed to create local audio track", e)
        }
    }

    /**
     * 创建 Offer
     */
    fun createOffer(callback: (SessionDescription) -> Unit) {
        scope.launch {
            try {
                val constraints = MediaConstraints()
                constraints.mandatory.add(
                    MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
                )
                constraints.mandatory.add(
                    MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false")
                )

                peerConnection?.createOffer(object : SdpObserver {
                    override fun onCreateSuccess(sdp: SessionDescription) {
                        Log.d(kTag, "Offer created: ${sdp.type}")
                        peerConnection?.setLocalDescription(object : SdpObserver {
                            override fun onCreateSuccess(p0: SessionDescription?) {}

                            override fun onSetSuccess() {
                                Log.d(kTag, "Local description set as Offer")
                                callback(sdp)
                            }

                            override fun onCreateFailure(error: String?) {
                                Log.e(kTag, "Failed to create offer: $error")
                                onError?.invoke("创建Offer失败: $error")
                            }

                            override fun onSetFailure(error: String?) {
                                Log.e(kTag, "Failed to set local description: $error")
                                onError?.invoke("设置本地描述失败: $error")
                            }
                        }, sdp)
                    }

                    override fun onSetSuccess() {}

                    override fun onCreateFailure(error: String?) {
                        Log.e(kTag, "Failed to create offer: $error")
                        onError?.invoke("创建Offer失败: $error")
                    }

                    override fun onSetFailure(error: String?) {
                        Log.e(kTag, "Failed to set local description: $error")
                        onError?.invoke("设置本地描述失败: $error")
                    }
                }, constraints)
            } catch (e: Exception) {
                Log.e(kTag, "Exception creating offer", e)
                onError?.invoke("创建Offer异常: ${e.message}")
            }
        }
    }

    /**
     * 创建 Answer
     */
    fun createAnswer(callback: (SessionDescription) -> Unit) {
        scope.launch {
            try {
                val constraints = MediaConstraints()
                constraints.mandatory.add(
                    MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
                )
                constraints.mandatory.add(
                    MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false")
                )

                peerConnection?.createAnswer(object : SdpObserver {
                    override fun onCreateSuccess(sdp: SessionDescription) {
                        Log.d(kTag, "Answer created: ${sdp.type}")
                        peerConnection?.setLocalDescription(object : SdpObserver {
                            override fun onCreateSuccess(p0: SessionDescription?) {}

                            override fun onSetSuccess() {
                                Log.d(kTag, "Local description set as Answer")
                                callback(sdp)
                            }

                            override fun onCreateFailure(error: String?) {
                                Log.e(kTag, "Failed to create answer: $error")
                                onError?.invoke("创建Answer失败: $error")
                            }

                            override fun onSetFailure(error: String?) {
                                Log.e(kTag, "Failed to set local description: $error")
                                onError?.invoke("设置本地描述失败: $error")
                            }
                        }, sdp)
                    }

                    override fun onSetSuccess() {}

                    override fun onCreateFailure(error: String?) {
                        Log.e(kTag, "Failed to create answer: $error")
                        onError?.invoke("创建Answer失败: $error")
                    }

                    override fun onSetFailure(error: String?) {
                        Log.e(kTag, "Failed to set local description: $error")
                        onError?.invoke("设置本地描述失败: $error")
                    }
                }, constraints)
            } catch (e: Exception) {
                Log.e(kTag, "Exception creating answer", e)
                onError?.invoke("创建Answer异常: ${e.message}")
            }
        }
    }

    /**
     * 设置远程描述（Offer 或 Answer）
     */
    fun setRemoteDescription(sdp: SessionDescription, callback: ((Boolean) -> Unit)? = null) {
        peerConnection?.setRemoteDescription(object : SdpObserver {
            override fun onCreateSuccess(p0: SessionDescription?) {}

            override fun onSetSuccess() {
                Log.d(kTag, "Remote description set successfully: ${sdp.type}")
                callback?.invoke(true)
            }

            override fun onCreateFailure(error: String?) {
                Log.e(kTag, "Failed to create: $error")
                callback?.invoke(false)
            }

            override fun onSetFailure(error: String?) {
                Log.e(kTag, "Failed to set remote description: $error")
                onError?.invoke("设置远程描述失败: $error")
                callback?.invoke(false)
            }
        }, sdp)
    }

    /**
     * 添加 ICE 候选
     */
    fun addIceCandidate(candidate: IceCandidate): Boolean {
        return try {
            peerConnection?.addIceCandidate(candidate)
            Log.d(kTag, "ICE candidate added: ${candidate.sdpMid}, ${candidate.sdpMLineIndex}")
            true
        } catch (e: Exception) {
            Log.e(kTag, "Failed to add ICE candidate", e)
            false
        }
    }

    /**
     * 处理 ICE 连接状态变化
     */
    private fun handleIceConnectionChange(state: PeerConnection.IceConnectionState) {
        when (state) {
            PeerConnection.IceConnectionState.CONNECTED,
            PeerConnection.IceConnectionState.COMPLETED -> {
                updateCallState(CallState.CONNECTED)
            }

            PeerConnection.IceConnectionState.DISCONNECTED,
            PeerConnection.IceConnectionState.FAILED,
            PeerConnection.IceConnectionState.CLOSED -> {
                updateCallState(CallState.DISCONNECTED)
            }

            PeerConnection.IceConnectionState.CHECKING -> {
                // 正在检查连接
            }

            PeerConnection.IceConnectionState.NEW -> {
                // 新连接
            }
        }
    }

    /**
     * 更新通话状态
     */
    private fun updateCallState(state: CallState) {
        if (currentCallState != state) {
            currentCallState = state
            Log.d(kTag, "Call state changed: $state")
            onCallStateChanged?.invoke(state)
        }
    }

    /**
     * 获取当前通话状态
     */
    fun getCallState(): CallState {
        return currentCallState
    }

    /**
     * 开始通话
     */
    fun startCall(peerId: String) {
        remotePeerId = peerId
        updateCallState(CallState.CALLING)
    }

    /**
     * 接受通话
     */
    fun acceptCall(peerId: String) {
        remotePeerId = peerId
        updateCallState(CallState.CONNECTED)
    }

    /**
     * 结束通话
     */
    fun endCall() {
        updateCallState(CallState.DISCONNECTED)
        remotePeerId = null
        cleanupPeerConnection()
    }

    /**
     * 清理 PeerConnection
     */
    private fun cleanupPeerConnection() {
        try {
            dataChannel?.dispose()
            dataChannel = null

            localAudioTrack?.dispose()
            localAudioTrack = null

            localStream?.dispose()
            localStream = null

            peerConnection?.close()
            peerConnection = null

            updateCallState(CallState.IDLE)

            Log.d(kTag, "PeerConnection cleaned up")
        } catch (e: Exception) {
            Log.e(kTag, "Error cleaning up PeerConnection", e)
        }
    }

    /**
     * 设置通话状态回调
     */
    fun setOnCallStateChangedListener(listener: (CallState) -> Unit) {
        onCallStateChanged = listener
    }

    /**
     * 设置远程音频轨道回调
     */
    fun setOnRemoteAudioTrackListener(listener: (AudioTrack) -> Unit) {
        onRemoteAudioTrack = listener
    }

    /**
     * 设置错误回调
     */
    fun setOnErrorListener(listener: (String) -> Unit) {
        onError = listener
    }

    /**
     * 清理所有资源
     */
    fun cleanup() {
        cleanupPeerConnection()

        peerConnectionFactory?.dispose()
        peerConnectionFactory = null

        EglBaseProvider.release()

        scope.cancel()

        isInitialized = false
        currentCallState = CallState.IDLE
        remotePeerId = null

        Log.d(kTag, "WebRTC cleaned up")
    }
}