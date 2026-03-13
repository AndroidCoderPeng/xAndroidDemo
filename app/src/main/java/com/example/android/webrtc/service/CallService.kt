package com.example.android.webrtc.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.android.MainActivity
import com.example.android.R
import com.example.android.webrtc.core.WebRtcManager

/**
 * 后台通话服务
 * 保持通话在后台运行
 * */
class CallService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "call_service_channel"
        private const val ACTION_START_CALL = "com.casic.webrtc.START_CALL"
        private const val ACTION_END_CALL = "com.casic.webrtc.END_CALL"
        private const val EXTRA_PEER_ID = "peer_id"

        /**
         * 启动通话服务
         */
        fun startCallService(context: Context, peerId: String) {
            val intent = Intent(context, CallService::class.java).apply {
                action = ACTION_START_CALL
                putExtra(EXTRA_PEER_ID, peerId)
            }
            context.startForegroundService(intent)
        }

        /**
         * 结束通话服务
         */
        fun stopCallService(context: Context) {
            val intent = Intent(context, CallService::class.java).apply {
                action = ACTION_END_CALL
            }
            context.startService(intent)
        }
    }

    private var webRtcManager: WebRtcManager? = null
    private var currentPeerId: String? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        webRtcManager = WebRtcManager.get(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_CALL -> {
                val peerId = intent.getStringExtra(EXTRA_PEER_ID)
                peerId?.let { handleStartCall(it) }
            }

            ACTION_END_CALL -> {
                handleEndCall()
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        webRtcManager?.endCall()
    }

    /**
     * 处理开始通话
     */
    private fun handleStartCall(peerId: String) {
        currentPeerId = peerId
        startForeground(NOTIFICATION_ID, createCallNotification(peerId))

        // 初始化 WebRTC
        webRtcManager?.initialize { success ->
            if (success) {
                webRtcManager?.startCall(peerId)
            }
        }
    }

    /**
     * 处理结束通话
     */
    private fun handleEndCall() {
        webRtcManager?.endCall()
        stopForeground(true)
        stopSelf()
    }

    /**
     * 创建通知渠道
     */
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "通话服务",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "保持通话在后台运行"
            setSound(null, null)
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * 创建通话通知
     */
    private fun createCallNotification(peerId: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("通话中")
            .setContentText("正在与 $peerId 通话")
            .setSmallIcon(R.mipmap.launcher_logo)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }
}