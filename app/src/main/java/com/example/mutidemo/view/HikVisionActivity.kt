package com.example.mutidemo.view

import android.graphics.PixelFormat
import android.util.Log
import android.view.SurfaceHolder
import com.example.mutidemo.R
import com.example.mutidemo.extensions.getChannel
import com.example.mutidemo.util.DemoConstant
import com.example.mutidemo.util.hk.MessageCodeHub
import com.example.mutidemo.util.hk.SDKGuider
import com.gyf.immersionbar.ImmersionBar
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.show
import kotlinx.android.synthetic.main.activity_hikvision.*

class HikVisionActivity : KotlinBaseActivity(), SurfaceHolder.Callback {

    private val kTag = "HikVisionActivity"
    private var previewHandle = -1
    private var selectChannel = -1
    private var selectStreamType = -1

    // return by NET_DVR_Login_v30
    private var returnUserID = -1

    // analog channel nums
    private var aChannelNum = 0

    //start analog channel
    private var startAChannel = 0

    //digital channel nums
    private var dChannelNum = 0

    //start digital channel
    private var startDChannel = 0
    private var isPreviewSuccess = false

    override fun initData() {

    }

    override fun initEvent() {
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

                    selectStreamType = 0

                    //开始预览
                    if (previewHandle != -1) {
                        SDKGuider.g_sdkGuider.m_comPreviewGuider.RealPlay_Stop_jni(previewHandle)
                    }
                    val strutPlayInfo = NET_DVR_PREVIEWINFO()
                    strutPlayInfo.lChannel = selectChannel
                    strutPlayInfo.dwStreamType = selectStreamType
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
    }

    override fun initLayoutView(): Int = R.layout.activity_hikvision

    override fun observeRequestState() {

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
}