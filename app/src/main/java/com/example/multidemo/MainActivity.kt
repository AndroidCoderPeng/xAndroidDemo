package com.example.multidemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import com.example.multidemo.databinding.ActivityMainBinding
import com.example.multidemo.service.LocalForegroundService
import com.example.multidemo.service.RemoteForegroundService
import com.example.multidemo.view.BluetoothActivity
import com.example.multidemo.view.CompassActivity
import com.example.multidemo.view.CompressVideoActivity
import com.example.multidemo.view.DragMapActivity
import com.example.multidemo.view.FaceCollectionActivity
import com.example.multidemo.view.GalleryActivity
import com.example.multidemo.view.GridViewActivity
import com.example.multidemo.view.HikVisionActivity
import com.example.multidemo.view.MLKitActivity
import com.example.multidemo.view.RadarScanActivity
import com.example.multidemo.view.RecodeAudioActivity
import com.example.multidemo.view.RefreshAndLoadMoreActivity
import com.example.multidemo.view.SlideBarActivity
import com.example.multidemo.view.SlideNavigationActivity
import com.example.multidemo.view.SteeringWheelActivity
import com.example.multidemo.view.TimeLineActivity
import com.example.multidemo.view.WaterMarkerActivity
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.navigatePageTo
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.extensions.timestampToCompleteDate
import com.pengxh.kt.lite.utils.socket.tcp.ConnectState
import com.pengxh.kt.lite.utils.socket.tcp.OnTcpMessageCallback
import com.pengxh.kt.lite.utils.socket.tcp.TcpClient
import java.util.Timer
import java.util.TimerTask

class MainActivity : KotlinBaseActivity<ActivityMainBinding>(), OnTcpMessageCallback {

    private val kTag = "MainActivity"
    private val tcpClient by lazy { TcpClient(this) }
    private var clickTime: Long = 0
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private val itemNames = listOf(
        "侧边导航栏", "上拉加载下拉刷新", "联系人侧边滑动控件", "拖拽地图选点",
        "音频录制与播放", "图片添加水印并压缩", "视频压缩", "蓝牙相关",
        "可删减九宫格", "人脸检测", "TCP客户端", "方向控制盘", "时间轴",
        "海康摄像头", "雷达扫描效果", "指南针", "3D画廊", "Google ML Kit"
    )

    override fun setupTopBarLayout() {

    }

    override fun observeRequestState() {

    }

    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        timer = Timer()
//        tcpClient.connectServer(DemoConstant.HOST, DemoConstant.TCP_PORT)

        startService(Intent(this, LocalForegroundService::class.java))
        startService(Intent(this, RemoteForegroundService::class.java))
        Timer().schedule(object : TimerTask() {
            override fun run() {
                Log.d(kTag, "run: ${System.currentTimeMillis().timestampToCompleteDate()}")
            }
        }, 0, 1000)
    }

    override fun onConnectStateChanged(state: ConnectState) {

    }

    override fun onReceivedTcpMessage(data: ByteArray?) {

    }

    override fun initEvent() {
        val adapter = object :
            NormalRecyclerAdapter<String>(R.layout.item_main_rv_g, itemNames.toMutableList()) {
            override fun convertView(viewHolder: ViewHolder, position: Int, item: String) {
                viewHolder.setText(R.id.itemTitleView, item)
            }
        }
        binding.mainRecyclerView.adapter = adapter
        adapter.setOnItemClickedListener(object :
            NormalRecyclerAdapter.OnItemClickedListener<String> {
            override fun onItemClicked(position: Int, t: String) {
                when (position) {
                    0 -> navigatePageTo<SlideNavigationActivity>()
                    1 -> navigatePageTo<RefreshAndLoadMoreActivity>()
                    2 -> navigatePageTo<SlideBarActivity>()
                    3 -> navigatePageTo<DragMapActivity>()
                    4 -> navigatePageTo<RecodeAudioActivity>()
                    5 -> navigatePageTo<WaterMarkerActivity>()
                    6 -> navigatePageTo<CompressVideoActivity>()
                    7 -> navigatePageTo<BluetoothActivity>()
                    8 -> navigatePageTo<GridViewActivity>()
                    9 -> navigatePageTo<FaceCollectionActivity>()
                    10 -> {
                        val sendBytes = byteArrayOf(
                            0xFF.toByte(),
                            0x01,
                            0x00,
                            0x95.toByte(),
                            0x00,
                            0x00,
                            0x96.toByte()
                        )
                        timerTask = object : TimerTask() {
                            override fun run() {
//                                tcpClient.sendMessage(sendBytes)
                            }
                        }
                        timer?.schedule(timerTask, 0, 1000)
                    }

                    11 -> navigatePageTo<SteeringWheelActivity>()
                    12 -> navigatePageTo<TimeLineActivity>()
                    13 -> navigatePageTo<HikVisionActivity>()
                    14 -> navigatePageTo<RadarScanActivity>()
                    15 -> navigatePageTo<CompassActivity>()
                    16 -> navigatePageTo<GalleryActivity>()
                    17 -> navigatePageTo<MLKitActivity>()
                }
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - clickTime > 2000) {
                "再按一次退出程序".show(this)
                clickTime = System.currentTimeMillis()
                true
            } else {
                super.onKeyDown(keyCode, event)
            }
        } else super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}