package com.example.android.activity

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.android.adapter.PtzPointAdapter
import com.example.android.databinding.ActivityPtzBinding
import com.example.android.model.DeviceViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.extensions.toJson
import com.pengxh.kt.lite.utils.SaveKeyValues
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PtzActivity : KotlinBaseActivity<ActivityPtzBinding>() {

    private val kTag = "PtzActivity"
    private val gson by lazy { Gson() }
    private val deviceViewModel by lazy { ViewModelProvider(this)[DeviceViewModel::class.java] }
    private var ptzPoints = mutableListOf<Int>()
    private lateinit var ptzPointAdapter: PtzPointAdapter
    private var speed = 5
    private var isNavigating = false  // 是否正在执行导航
    private var shouldStopNavigation = false  // 是否需要停止导航
    private val restTime=1000L

    override fun initViewBinding(): ActivityPtzBinding {
        return ActivityPtzBinding.inflate(layoutInflater)
    }

    override fun setupTopBarLayout() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        val json = SaveKeyValues.getValue("PTZ_POINT_KEY", "") as String
        if (json.isNotBlank()) {
            // [1,2,3,4,5,6,7]
            val type = object : TypeToken<MutableList<Int>>() {}.type
            ptzPoints = gson.fromJson(json, type) ?: mutableListOf()
        }
        ptzPointAdapter = PtzPointAdapter(this, ptzPoints)
        binding.recyclerView.adapter = ptzPointAdapter
    }

    override fun observeRequestState() {

    }

    @SuppressLint("all")
    override fun initEvent() {
        binding.stopNavigationButton.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("提示")
                .setCancelable(false)
                .setMessage("是否停止导航？")
                .setPositiveButton("确定") { _, _ ->
                    shouldStopNavigation = true
                    "正在停止导航...".show(this)

                    executePtzCommand("Stop")
                    ptzPointAdapter.notifyItemRangeRemoved(0, ptzPoints.size)
                    ptzPoints.clear()
                    SaveKeyValues.removeKey("PTZ_POINT_KEY")
                }
                .setNegativeButton("取消") { _, _ ->
                }.show()
        }

        binding.startNavigationButton.setOnClickListener {
            if (ptzPoints.isEmpty()) {
                "请先添加预置点".show(this)
                return@setOnClickListener
            }

            if (isNavigating) {
                "导航任务正在执行中".show(this)
                return@setOnClickListener
            }

            isNavigating = true
            shouldStopNavigation = false

            lifecycleScope.launch {
                "开始执行导航任务，共 ${ptzPoints.size} 个预置点".show(this@PtzActivity)
                ptzPoints.forEachIndexed { i, point ->
                    // 检查是否需要停止导航
                    if (shouldStopNavigation) {
                        Log.d(kTag, "导航任务已停止")
                        return@forEachIndexed
                    }

                    executeSinglePtzCommand(point)

                    // 如果不是最后一个点，则等待10秒
                    if (i < ptzPoints.size - 1) {
                        delay(10_000)  // 10秒 = 10000毫秒
                    }
                }

                // 导航完成
                isNavigating = false
                if (!shouldStopNavigation) {
                    "导航任务执行完毕".show(this@PtzActivity)
                    Log.d(kTag, "导航任务执行完毕")
                } else {
                    "导航任务已停止".show(this@PtzActivity)
                    Log.d(kTag, "导航任务已停止")
                }
            }
        }

        binding.addPtzPointButton.setOnClickListener {
            if (isNavigating) {
                "导航任务执行中，无法添加预置点".show(this)
                return@setOnClickListener
            }

            val oldPosition = ptzPoints.size
            val index = oldPosition + 1

            ptzPoints.add(index)
            ptzPointAdapter.notifyItemRangeInserted(oldPosition, 1)

            Log.d(kTag, "添加第 $index 个点")
            deviceViewModel.executePreset("SetPreset", index)

            SaveKeyValues.putValue("PTZ_POINT_KEY", ptzPoints.toJson())
        }

        binding.turnUpButton.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> executePtzCommand("Up")
                MotionEvent.ACTION_UP -> executePtzCommand("Stop")
            }
            false
        }

        binding.turnLeftButton.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> executePtzCommand("Left")
                MotionEvent.ACTION_UP -> executePtzCommand("Stop")
            }
            false
        }

        binding.turnDownButton.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> executePtzCommand("Down")
                MotionEvent.ACTION_UP -> executePtzCommand("Stop")
            }
            false
        }

        binding.turnRightButton.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> executePtzCommand("Right")
                MotionEvent.ACTION_UP -> executePtzCommand("Stop")
            }
            false
        }
    }

    /**
     * 执行单个PTZ预置点命令
     * 包含四个方向的扫描：上、下、左、右
     * 每个方向：先移动1秒，停止，再调用预置点
     */
    private suspend fun executeSinglePtzCommand(point: Int) {
        "执行第 ${point}/${ptzPoints.size} 个预置点".show(this)
        deviceViewModel.executePreset("Call", point)
        delay(1000)

        // 1. 上下扫描
        executePtzCommand("Up")
        delay(300)
        executePtzCommand("Stop")
        delay(restTime)

        deviceViewModel.executePreset("Call", point)
        delay(restTime)

        executePtzCommand("Down")
        delay(300)
        executePtzCommand("Stop")
        delay(restTime)

        deviceViewModel.executePreset("Call", point)
        delay(restTime)

        // 检查是否需要停止导航
        if (shouldStopNavigation) return

        // 2. 左有扫描
        executePtzCommand("Left")
        delay(200)
        executePtzCommand("Stop")
        delay(restTime)

        deviceViewModel.executePreset("Call", point)
        delay(restTime)

        executePtzCommand("Right")
        delay(200)
        executePtzCommand("Stop")
        delay(restTime)

        deviceViewModel.executePreset("Call", point)
    }

    private fun executePtzCommand(action: String) {
        if (isWiFiConnected()) {
            deviceViewModel.executePtzCommand(action, speed)
        } else {
            "WiFi未连接，指令执行失败".show(this)
        }
    }

    private fun isWiFiConnected(): Boolean {
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) && capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )
    }
}