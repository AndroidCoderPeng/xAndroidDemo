package com.example.android.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.TriggerEvent
import android.hardware.TriggerEventListener
import android.os.Handler
import android.os.Looper
import android.os.SystemClock

class MotionGate(
    context: Context,
    private val onMoving: () -> Unit,   // 切高频采样
    private val onStill: () -> Unit,    // 切低频采样
    private val onStateChanged: ((moving: Boolean) -> Unit)? = null  // 运动状态变化（供 UI/通知展示）
) : SensorEventListener {

    private val sensorManager = context.getSystemService(SensorManager::class.java)
    private val stillHandler = Handler(Looper.getMainLooper())
    private val stillTimeoutMs = 3 * 60 * 1000L   // 连续3分钟无运动 → 静止

    @Volatile
    private var lastMotionElapsed = 0L
    @Volatile
    private var isMoving = false

    // 计步确认：静止时偶发的孤立单步（桌面震动、碰撞等）会误报，
    // 要求时间窗口内连续多步才判定为真实移动（正常步行步频 1.5~3Hz）
    private var stepCount = 0
    private var stepWindowStart = 0L
    private val stepConfirmCount = 2
    private val stepWindowMs = 3000L

    // 显著运动传感器（机型不支持时为 null）
    private val sigMotionSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION)

    // 计步传感器（机型不支持时为 null）
    private val stepSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    // 显式类型：避免初始化器自引用导致的类型推断递归
    private val triggerListener: TriggerEventListener = object : TriggerEventListener() {
        override fun onTrigger(event: TriggerEvent?) {
            onMotionDetected()
            // 显著运动是一次性触发器，必须重新注册
            requestSignificantMotion()
        }
    }

    private val stillCheck: Runnable = Runnable {
        if (isMoving && SystemClock.elapsedRealtime() - lastMotionElapsed >= stillTimeoutMs) {
            isMoving = false
            onStateChanged?.invoke(false)
            onStill()
        } else if (isMoving) {
            armStillTimer()   // 还在动，续期
        }
    }

    fun start() {
        runCatching { requestSignificantMotion() }
        stepSensor?.let {
            // Android 10+ 需要 ACTIVITY_RECOGNITION 权限，未授权时会抛 SecurityException
            runCatching {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
        armStillTimer()
    }

    fun stop() {
        sigMotionSensor?.let { sensorManager.cancelTriggerSensor(triggerListener, it) }
        runCatching { sensorManager.unregisterListener(this) }
        stillHandler.removeCallbacksAndMessages(null)
        if (isMoving) {
            isMoving = false
            onStateChanged?.invoke(false)
        }
    }

    /**
     * 外部运动证据（如定位位移），覆盖骑车/坐车等计步传感器感知不到的场景。
     * 可在任意线程调用。
     */
    fun notifyMotion() {
        stillHandler.post { onMotionDetected() }
    }

    private fun requestSignificantMotion() {
        sigMotionSensor?.let { sensorManager.requestTriggerSensor(triggerListener, it) }
    }

    private fun onMotionDetected() {
        lastMotionElapsed = SystemClock.elapsedRealtime()
        if (!isMoving) {
            isMoving = true
            onStateChanged?.invoke(true)
            onMoving()
        }
        armStillTimer()
    }

    // 计步回调走 SensorEventListener
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
            onStepDetected()
        }
    }

    private fun onStepDetected() {
        val now = SystemClock.elapsedRealtime()
        if (now - stepWindowStart > stepWindowMs) {
            stepWindowStart = now
            stepCount = 0
        }
        stepCount++
        if (stepCount >= stepConfirmCount) {
            onMotionDetected()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun armStillTimer() {
        stillHandler.removeCallbacks(stillCheck)
        stillHandler.postDelayed(stillCheck, stillTimeoutMs)
    }
}
