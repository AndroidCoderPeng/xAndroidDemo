package com.example.android.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.example.android.R
import com.example.android.extensions.isLocationInChina
import com.example.android.util.ExampleConstant
import com.example.android.util.MotionGate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * APP前台服务，降低APP被系统杀死的可能性
 * */
class ForegroundRunningService : Service(), AMapLocationListener {

    companion object {
        private const val INTERVAL_MOVING_MS = 5_000L    // 运动中：5s
        private const val INTERVAL_STILL_MS = 60_000L    // 静止：60s
        private const val MIN_MOVE_DISTANCE_M = 20.0     // 位移兜底：最小位移
        private const val MIN_MOVE_SPEED_MPS = 1.5       // 位移兜底：最小均速（≈5.4km/h）
    }

    private val kTag = "ForegroundService"
    private val channelId = "foreground_running_service_channel"
    private val notificationId = 1001
    private val notificationManager by lazy { getSystemService(NotificationManager::class.java) }
    private var notificationBuilder: NotificationCompat.Builder? = null

    private val mainHandler = Handler(Looper.getMainLooper())
    private var currentIntervalMs = INTERVAL_STILL_MS
    private var isLocating = false

    private var notifyReason = "正在记录外出轨迹"

    @Volatile
    private var isMoving = false

    private val motionGate by lazy {
        MotionGate(
            context = this,
            onMoving = { switchInterval(INTERVAL_MOVING_MS) },
            onStill = { switchInterval(INTERVAL_STILL_MS) },
            onStateChanged = { moving -> mainHandler.post { updateMotionState(moving) } }
        )
    }

    private fun switchInterval(intervalMs: Long) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            applyInterval(intervalMs)
        } else {
            mainHandler.post {
                applyInterval(intervalMs)
            }
        }
    }

    private fun applyInterval(intervalMs: Long) {
        if (intervalMs == currentIntervalMs) return   // 频率未变，别无谓重启（每次重启要重新捕获 GPS）
        currentIntervalMs = intervalMs
        if (!isLocating) return                       // 定位已停（在家连 WiFi），只记录目标频率
        restartLocation()
        notifyReason = "正在记录外出轨迹（${currentIntervalMs / 1000}s 采样）"
        refreshNotification()
    }

    private fun updateMotionState(moving: Boolean) {
        isMoving = moving
        refreshNotification()
    }

    private val locationClient by lazy { AMapLocationClient(this) }

    private fun buildLocationOption(intervalMs: Long) = AMapLocationClientOption().apply {
        locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        interval = intervalMs
        isNeedAddress = false
        isLocationCacheEnable = false
    }

    // 上一个有效的定位点，用于漂移检测
    private var lastGoodLocation: AMapLocation? = null

    private var isReceiverRegistered = false

    override fun onCreate() {
        super.onCreate()
        val name = "${resources.getString(R.string.app_name)}前台服务"
        val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_LOW)
        channel.description = "Channel for Foreground Running Service"
        notificationManager.createNotificationChannel(channel)
        notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.launcher_logo)
            .setContentTitle(ExampleConstant.FOREGROUND_RUNNING_SERVICE_TITLE)
            .setPriority(NotificationCompat.PRIORITY_LOW) // 设置通知优先级
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        val notification = notificationBuilder?.build()
        startForeground(notificationId, notification)

        // 初始化定位客户端，默认低频采样，由 MotionGate 决定是否升频
        locationClient.setLocationListener(this)
        startTracking("正在记录外出轨迹")

        // 注册系统级广播监听器
        val filter = IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(broadcastReceiver, filter)
        }
        isReceiverRegistered = true
    }

    override fun onLocationChanged(location: AMapLocation?) {
        location?.let { loc ->
            // 不在中国境内
            if (!loc.isLocationInChina()) {
                return
            }

            // 定位错误码检查：errorCode != 0 表示定位失败
            if (loc.errorCode != 0) {
                return
            }

            // 精度检查：根据定位源使用不同阈值
            //    GPS 精度好（150m），网络定位在地铁等无GPS场景精度约500m（放宽到600m）
            val isGpsLocation = loc.locationType == 0 || loc.locationType == 1
            val maxAccuracy = if (isGpsLocation) 150.0f else 600.0f
            if (loc.accuracy > maxAccuracy) {
                return
            }

            // 速度异常检查：超过 35m/s (120km/h，北京地铁最高时速) 的视为噪声
            val speed = loc.speed
            if (speed < 0 || speed > 35) {
                return
            }

            // 4. 与上一个有效点的时间距离校验：避免瞬移漂移
            lastGoodLocation?.let { last ->
                val lastLatLng = LatLng(last.latitude, last.longitude)
                val curLatLng = LatLng(loc.latitude, loc.longitude)
                val distance = AMapUtils.calculateLineDistance(lastLatLng, curLatLng).toDouble()
                val timeDiff = (loc.time - last.time) / 1000.0
                if (timeDiff > 0) {
                    val avgSpeed = distance / timeDiff // m/s
                    if (avgSpeed > 35) {
                        return
                    }
                    // 位移兜底：骑车/坐车时计步传感器无感，靠位移维持高频采样
                    if (distance >= MIN_MOVE_DISTANCE_M && avgSpeed >= MIN_MOVE_SPEED_MPS) {
                        motionGate.notifyMotion()
                    }
                }
            }

            lastGoodLocation = loc
            refreshNotification()
        }
    }

    private val timeFormat by lazy { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    private fun refreshNotification() {
        val state = if (isMoving) "移动中" else "静止"
        val time = timeFormat.format(Date())
        val text = "运动状态：${state}（更新于 $time）\n$notifyReason"
        val notification = notificationBuilder?.let {
            it.setContentTitle(ExampleConstant.FOREGROUND_RUNNING_SERVICE_TITLE)
                .setContentText("运动状态：$state · $notifyReason")
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            it.build()
        }
        notificationManager.notify(notificationId, notification)
    }

    private fun restartLocation() {
        // 高德 SDK 建议 setLocationOption 在 startLocation 之前调用，
        // 运行中改间隔最稳的方式是 stop → setOption → start
        locationClient.stopLocation()
        locationClient.setLocationOption(buildLocationOption(currentIntervalMs))
        locationClient.startLocation()
    }

    private fun startTracking(reason: String) {
        isLocating = true
        locationClient.setLocationOption(buildLocationOption(currentIntervalMs))
        locationClient.startLocation()
        motionGate.start()
        notifyReason = reason
        refreshNotification()
    }

    private fun stopTracking() {
        isLocating = false
        motionGate.stop()
        locationClient.stopLocation()
        notifyReason = "WiFi已连接，定位已停止"
        refreshNotification()
    }

    override fun onDestroy() {
        super.onDestroy()
        motionGate.stop()
        locationClient.setLocationListener(null)
        locationClient.stopLocation()

        if (isReceiverRegistered) {
            unregisterReceiver(broadcastReceiver)
            isReceiverRegistered = false
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                // WiFi网络状态变化
                WifiManager.NETWORK_STATE_CHANGED_ACTION -> {
                    val networkInfo =
                        intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
                    if (networkInfo?.isConnected == true) {
                        stopTracking()
                    } else {
                        startTracking("WiFi已断开，正在记录外出轨迹（${currentIntervalMs / 1000}s 采样）")
                    }
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}
