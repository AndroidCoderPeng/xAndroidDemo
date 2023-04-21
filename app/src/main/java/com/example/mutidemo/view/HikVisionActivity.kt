package com.example.mutidemo.view

import android.graphics.PixelFormat
import android.util.Log
import android.view.SurfaceHolder
import androidx.lifecycle.ViewModelProvider
import com.example.mutidemo.R
import com.example.mutidemo.extensions.getChannel
import com.example.mutidemo.extensions.reformat
import com.example.mutidemo.model.Point
import com.example.mutidemo.util.DemoConstant
import com.example.mutidemo.util.LoadingDialogHub
import com.example.mutidemo.util.hk.MessageCodeHub
import com.example.mutidemo.util.hk.SDKGuider
import com.example.mutidemo.util.netty.UdpClient
import com.example.mutidemo.vm.RegionViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.vm.LoadState
import kotlinx.android.synthetic.main.activity_hikvision.*

class HikVisionActivity : KotlinBaseActivity(), SurfaceHolder.Callback {

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
    private lateinit var regionViewModel: RegionViewModel
    private val udpClient by lazy { UdpClient() }

    override fun initData() {
        regionViewModel = ViewModelProvider(this)[RegionViewModel::class.java]
    }

    override fun initEvent() {
        leftBackView.setOnClickListener { finish() }

        clearView.setOnClickListener {
            regionView.clearRoutePath()
        }

        openCameraButton.setOnClickListener {
            val deviceItem = SDKGuider.g_sdkGuider.m_comDMGuider.DeviceItem()
            deviceItem.m_szDevName = ""
            deviceItem.m_struNetInfo = SDKGuider.g_sdkGuider.m_comDMGuider.DevNetInfo(
                DemoConstant.HK_NET_IP,
                DemoConstant.HK_NET_PORT,
                DemoConstant.HK_NET_USERNAME,
                DemoConstant.HK_NET_PASSWORD
            )
            if (deviceItem.m_szDevName.isEmpty()) {
                deviceItem.m_szDevName = deviceItem.m_struNetInfo.m_szIp
            }

            val loginV40Jna = SDKGuider.g_sdkGuider.m_comDMGuider.login_v40_jna(
                deviceItem.m_szDevName, deviceItem.m_struNetInfo
            )
            if (loginV40Jna) {
                //配置设备通道
                try {
                    val deviceInfo = SDKGuider.g_sdkGuider.m_comDMGuider.devList[0]
                    returnUserID = deviceInfo.m_lUserID

                    aChannelNum = deviceInfo.m_struDeviceInfoV40_jna.struDeviceV30.byChanNum.toInt()
                    startAChannel =
                        deviceInfo.m_struDeviceInfoV40_jna.struDeviceV30.byStartChan.toInt()

                    dChannelNum = deviceInfo.m_struDeviceInfoV40_jna.struDeviceV30.byIPChanNum +
                            deviceInfo.m_struDeviceInfoV40_jna.struDeviceV30.byHighDChanNum * 256
                    startDChannel =
                        deviceInfo.m_struDeviceInfoV40_jna.struDeviceV30.byStartChan.toInt()

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
                        SDKGuider.g_sdkGuider.m_comPreviewGuider.RealPlay_Stop_jni(previewHandle)
                    }
                    val strutPlayInfo = NET_DVR_PREVIEWINFO()
                    strutPlayInfo.lChannel = selectChannel
                    strutPlayInfo.dwStreamType = 1
                    strutPlayInfo.bBlocked = 1
                    strutPlayInfo.hHwnd = videoSurfaceView.holder
                    previewHandle = SDKGuider.g_sdkGuider.m_comPreviewGuider.RealPlay_V40_jni(
                        returnUserID, strutPlayInfo, null
                    )
                    if (previewHandle < 0) {
                        Log.d(
                            kTag,
                            "configDevice: NET_DVR_RealPlay_V40 fail, Err:${MessageCodeHub.getErrorCode()}"
                        )
                        return@setOnClickListener
                    }
                    "预览开启成功".show(this)
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                    "设备未正常连接，无法开启预览".show(this)
                }
            }
        }

        closeCameraButton.setOnClickListener {
            if (!SDKGuider.g_sdkGuider.m_comPreviewGuider.RealPlay_Stop_jni(previewHandle)) {
                return@setOnClickListener
            }
            "预览关闭成功".show(this)
            previewHandle = -1
            isPreviewSuccess = false
        }

        socketSendButton.setOnClickListener {
            val region = regionView.getConfirmedRegion()
            val body = JsonObject()
            body.add("position", gson.toJsonTree(region, typeToken).asJsonArray)
            body.addProperty("color", "#FF0000")
            body.addProperty("code", "11,12")

            //发送数据
            udpClient.send(body.toString())
        }

        httpSendButton.setOnClickListener {
            val region = regionView.getConfirmedPoints()
            val data = region.reformat()

            //发送数据
            regionViewModel.postRegion("11,12", "#FF0000", data)
        }
    }

    override fun initLayoutView(): Int = R.layout.activity_hikvision

    override fun observeRequestState() {
        regionViewModel.loadState.observe(this) {
            when (it) {
                LoadState.Loading -> LoadingDialogHub.show(this, "区域设置中...")
                else -> LoadingDialogHub.dismiss()
            }
        }
    }

    override fun setupTopBarLayout() {
        ImmersionBar.with(this).init()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        videoSurfaceView.holder.setFormat(PixelFormat.TRANSLUCENT)
        if (-1 == previewHandle) {
            return
        }
        val surface = holder.surface
        if (surface.isValid) {
            if (-1 == SDKGuider.g_sdkGuider.m_comPreviewGuider.RealPlaySurfaceChanged_jni(
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
            if (-1 == SDKGuider.g_sdkGuider.m_comPreviewGuider.RealPlaySurfaceChanged_jni(
                    previewHandle, 0, null
                )
            ) {
                Log.d(kTag, "surfaceCreated: ${MessageCodeHub.getErrorCode()}")
            }
        }
    }

    override fun onDestroy() {
        udpClient.release()
        super.onDestroy()
    }
}