package com.example.multidemo.view

import android.graphics.PixelFormat
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import com.example.multidemo.databinding.ActivityHikvisionBinding
import com.example.multidemo.extensions.getChannel
import com.example.multidemo.model.Point
import com.example.multidemo.util.DemoConstant
import com.example.multidemo.util.hk.MessageCodeHub
import com.example.multidemo.util.hk.SDKGuider
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.utils.socket.udp.UdpClient

class HikVisionActivity : KotlinBaseActivity<ActivityHikvisionBinding>(), SurfaceHolder.Callback {

    private val kTag = "HikVisionActivity"
    private var previewHandle = -1
    private var selectChannel = -1
    private var returnUserID = -1
    private var aChannelNum = 0
    private var startAChannel = 0
    private var dChannelNum = 0
    private var startDChannel = 0
    private var isPreviewSuccess = false

    private val gson by lazy { Gson() }
    private val typeToken = object : TypeToken<ArrayList<Point>>() {}.type
    private val udpClient by lazy { UdpClient(DemoConstant.HOST, DemoConstant.TCP_PORT) }

    override fun initOnCreate(savedInstanceState: Bundle?) {

    }

    override fun initEvent() {
        binding.leftBackView.setOnClickListener { finish() }

        binding.clearView.setOnClickListener {
            binding.regionView.clearRoutePath()
        }

        binding.openCameraButton.setOnClickListener {
            val deviceItem = SDKGuider.sdkGuider.devManageGuider.DeviceItem()
            deviceItem.szDevName = ""
            deviceItem.devNetInfo = SDKGuider.sdkGuider.devManageGuider.DevNetInfo(
                DemoConstant.HK_NET_IP,
                DemoConstant.HK_NET_PORT,
                DemoConstant.HK_NET_USERNAME,
                DemoConstant.HK_NET_PASSWORD
            )
            if (deviceItem.szDevName.isEmpty()) {
                deviceItem.szDevName = deviceItem.devNetInfo.szIp
            }
            val loginV40Jna = SDKGuider.sdkGuider.devManageGuider.login_v40_jna(
                deviceItem.szDevName, deviceItem.devNetInfo
            )
            if (loginV40Jna) {
                //配置设备通道
                try {
                    val deviceInfo = SDKGuider.sdkGuider.devManageGuider.devList[0]
                    returnUserID = deviceInfo.szUserId

                    aChannelNum = deviceInfo.deviceInfoV40_jna.struDeviceV30.byChanNum.toInt()
                    startAChannel = deviceInfo.deviceInfoV40_jna.struDeviceV30.byStartChan.toInt()

                    dChannelNum = deviceInfo.deviceInfoV40_jna.struDeviceV30.byIPChanNum +
                            deviceInfo.deviceInfoV40_jna.struDeviceV30.byHighDChanNum * 256
                    startDChannel = deviceInfo.deviceInfoV40_jna.struDeviceV30.byStartChan.toInt()

                    var iAnalogStartChan = startAChannel
                    var iDigitalStartChan = startDChannel

                    val channelList = ArrayList<String>()

                    for (i in 0 until aChannelNum) {
                        channelList.add("ACamera_$iAnalogStartChan")
                        iAnalogStartChan++
                    }

                    for (i in 0 until dChannelNum) {
                        channelList.add("DCamera_$iDigitalStartChan")
                        iDigitalStartChan++
                    }
                    selectChannel = Integer.valueOf(channelList[0].getChannel())

                    val streamList = ArrayList<String>()
                    streamList.add("main_stream")
                    streamList.add("sub_stream")
                    streamList.add("third_stream")

                    //开始预览
                    if (previewHandle != -1) {
                        SDKGuider.sdkGuider.devPreviewGuider.RealPlay_Stop_jni(previewHandle)
                    }
                    val strutPlayInfo = NET_DVR_PREVIEWINFO()
                    strutPlayInfo.lChannel = selectChannel
                    strutPlayInfo.dwStreamType = 1
                    strutPlayInfo.bBlocked = 1
                    strutPlayInfo.hHwnd = binding.videoSurfaceView.holder
                    previewHandle = SDKGuider.sdkGuider.devPreviewGuider.RealPlay_V40_jni(
                        returnUserID, strutPlayInfo, null
                    )
                    if (previewHandle < 0) {
                        Log.d(kTag, "initEvent: Err:${MessageCodeHub.getErrorCode()}")
                        return@setOnClickListener
                    }
                    "预览开启成功".show(this)
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                    "设备未正常连接，无法开启预览".show(this)
                }
            }
        }

        binding.closeCameraButton.setOnClickListener {
            if (!SDKGuider.sdkGuider.devPreviewGuider.RealPlay_Stop_jni(previewHandle)) {
                return@setOnClickListener
            }
            "预览关闭成功".show(this)
            previewHandle = -1
            isPreviewSuccess = false
        }

        binding.socketSendButton.setOnClickListener {
            val region = binding.regionView.getConfirmedRegion()
            val body = JsonObject()
            body.add("position", gson.toJsonTree(region, typeToken).asJsonArray)
            body.addProperty("color", "#FF0000")
            body.addProperty("code", "11,12")

            //发送数据
            udpClient.send(body.toString())
        }

        binding.httpSendButton.setOnClickListener {
            val region = binding.regionView.getConfirmedPoints()
        }
    }

    override fun initViewBinding(): ActivityHikvisionBinding {
        return ActivityHikvisionBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {
        ImmersionBar.with(this).init()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        binding.videoSurfaceView.holder.setFormat(PixelFormat.TRANSLUCENT)
        if (-1 == previewHandle) {
            return
        }
        val surface = holder.surface
        if (surface.isValid) {
            if (-1 == SDKGuider.sdkGuider.devPreviewGuider.RealPlaySurfaceChanged_jni(
                    previewHandle, 0, holder
                )
            ) {
                Log.d(kTag, "surfaceCreated: ${MessageCodeHub.getErrorCode()}")
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        if (-1 == previewHandle) {
            return
        }
        if (holder.surface.isValid) {
            if (-1 == SDKGuider.sdkGuider.devPreviewGuider.RealPlaySurfaceChanged_jni(
                    previewHandle, 0, null
                )
            ) {
                Log.d(kTag, "surfaceCreated: ${MessageCodeHub.getErrorCode()}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        udpClient.release()
    }
}