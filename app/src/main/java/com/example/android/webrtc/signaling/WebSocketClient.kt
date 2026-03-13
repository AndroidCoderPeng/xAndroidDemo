package com.example.android.webrtc.signaling

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import org.java_websocket.client.WebSocketClient as JavaWebSocketClient

/**
 * WebSocket 信令客户端
 * */
class WebSocketClient private constructor(
    private val serverUrl: String,
    private val userId: String,
    private val callback: SignalingCallback
) {
    private val kTag = "WebSocketClient"
    private val gson = Gson()
    private var webSocket: JavaWebSocketClient? = null
    private var isManualDisconnect = false
    private var isReconnecting = false
    private var reconnectAttempts = 0
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var heartbeatJob: Job? = null
    private var reconnectJob: Job? = null

    companion object {
        private const val HEARTBEAT_INTERVAL = 30000L // 心跳间隔 30 秒
        private const val RECONNECT_DELAY = 5000L      // 重连延迟 5 秒
        private const val MAX_RECONNECT_ATTEMPTS = 5   // 最大重连次数

        @Volatile
        private var client: WebSocketClient? = null

        /**
         * 获取单例实例
         */
        fun get(serverUrl: String, userId: String, callback: SignalingCallback): WebSocketClient {
            return client ?: synchronized(this) {
                client ?: WebSocketClient(serverUrl, userId, callback).also { client = it }
            }
        }

        /**
         * 销毁实例
         */
        fun destroyInstance() {
            client?.disconnect()
            client = null
        }
    }

    private inner class WebSocketClientImpl(serverUri: URI) : JavaWebSocketClient(serverUri) {
        override fun onOpen(handshake: ServerHandshake?) {
            Log.d(kTag, "WebSocket connected")
            isManualDisconnect = false
            isReconnecting = false
            reconnectAttempts = 0
            scope.launch {
                withContext(Dispatchers.Main) {
                    callback.onConnected()
                }
            }
            startHeartbeat()
        }

        override fun onMessage(message: String?) {
            message?.let {
                Log.d(kTag, "Message received: $it")
                handleSignalingMessage(it)
            }
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            Log.d(kTag, "WebSocket closed: $code, $reason, remote=$remote")
            stopHeartbeat()

            if (!isManualDisconnect && !isReconnecting && reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                isReconnecting = true
                reconnectAttempts++
                Log.d(kTag, "Reconnect attempt $reconnectAttempts/$MAX_RECONNECT_ATTEMPTS")

                // 取消之前的重连任务
                reconnectJob?.cancel()

                // 自动重连
                reconnectJob = scope.launch {
                    delay(RECONNECT_DELAY)
                    Log.d(kTag, "Attempting to reconnect...")
                    webSocket = null
                    connectPlatform()
                    isReconnecting = false
                }
            } else if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
                Log.e(kTag, "Max reconnect attempts reached, giving up")
                scope.launch {
                    withContext(Dispatchers.Main) {
                        callback.onError("连接失败：已达到最大重连次数")
                    }
                }
            }

            scope.launch {
                withContext(Dispatchers.Main) {
                    callback.onDisconnected()
                }
            }
        }

        override fun onError(ex: Exception?) {
            Log.e(kTag, "WebSocket error", ex)
            scope.launch {
                withContext(Dispatchers.Main) {
                    callback.onError(ex?.message ?: "Unknown error")
                }
            }
        }
    }

    /**
     * 连接到信令服务器
     */
    fun connectPlatform() {
        try {
            // 只在非重连场景下才清理旧的连接
            if (!isReconnecting) {
                webSocket?.close()
                webSocket = null
            }

            val uri = URI(serverUrl)
            webSocket = WebSocketClientImpl(uri)
            webSocket?.connect()
            Log.d(kTag, "Connecting to signaling server: $serverUrl")
        } catch (e: Exception) {
            Log.e(kTag, "Failed to connect to signaling server", e)
            isReconnecting = false
            callback.onError(e.message ?: "Connection failed")
        }
    }

    /**
     * 断开连接
     */
    fun disconnect() {
        isManualDisconnect = true
        isReconnecting = false
        reconnectAttempts = 0
        reconnectJob?.cancel()
        stopHeartbeat()
        webSocket?.close()
        webSocket = null
        scope.cancel()
        Log.d(kTag, "Disconnected from signaling server")
    }

    /**
     * 发送信令消息
     */
    fun sendMessage(message: SignalingMessage) {
        try {
            val json = gson.toJson(message)
            Log.d(kTag, "Sending message: $json")
            webSocket?.send(json)
        } catch (e: Exception) {
            Log.e(kTag, "Failed to send message", e)
            callback.onError(e.message ?: "Send failed")
        }
    }

    /**
     * 处理收到的信令消息
     */
    private fun handleSignalingMessage(json: String) {
        try {
            val message = gson.fromJson(json, SignalingMessage::class.java)
            scope.launch {
                withContext(Dispatchers.Main) {
                    when (message.type) {
                        MessageType.CALL_REQUEST -> {
                            callback.onCallRequest(message.senderId)
                        }

                        MessageType.CALL_ACCEPT -> {
                            callback.onCallAccepted(message.senderId)
                        }

                        MessageType.CALL_REJECT -> {
                            callback.onCallRejected(message.senderId)
                        }

                        MessageType.CALL_END -> {
                            callback.onCallEnded(message.senderId)
                        }

                        MessageType.OFFER -> {
                            message.sdp?.let { sdp ->
                                callback.onOfferReceived(sdp, message.senderId)
                            }
                        }

                        MessageType.ANSWER -> {
                            message.sdp?.let { sdp ->
                                callback.onAnswerReceived(sdp, message.senderId)
                            }
                        }

                        MessageType.ICE_CANDIDATE -> {
                            if (message.iceSdpMid != null && message.iceSdpMLineIndex != null && message.iceCandidate != null) {
                                callback.onIceCandidateReceived(
                                    message.iceSdpMid,
                                    message.iceSdpMLineIndex,
                                    message.iceCandidate,
                                    message.senderId
                                )
                            }
                        }

                        MessageType.HEARTBEAT -> {
                            callback.onHeartbeatReceived()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(kTag, "Failed to handle signaling message", e)
        }
    }

    /**
     * 开始心跳
     */
    private fun startHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = scope.launch {
            while (isActive) {
                delay(HEARTBEAT_INTERVAL)
                try {
                    val heartbeat = SignalingMessage.createHeartbeat(userId)
                    sendMessage(heartbeat)
                } catch (e: Exception) {
                    Log.e(kTag, "Heartbeat failed", e)
                }
            }
        }
    }

    /**
     * 停止心跳
     */
    private fun stopHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }

    /**
     * 检查是否已连接
     */
    fun isConnected(): Boolean {
        return webSocket?.isOpen == true
    }
}