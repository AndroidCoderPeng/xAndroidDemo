package com.example.multidemo

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.multidemo.databinding.ActivityMainBinding
import com.example.multidemo.service.ScreenShortRecordService
import com.example.multidemo.view.AudioVisualActivity
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
import com.example.multidemo.view.SatelliteStatusActivity
import com.example.multidemo.view.SaveInAlbumActivity
import com.example.multidemo.view.SlideNavigationActivity
import com.example.multidemo.view.SteeringWheelActivity
import com.example.multidemo.view.TimeLineActivity
import com.example.multidemo.view.WaterMarkerActivity
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.createImageFileDir
import com.pengxh.kt.lite.extensions.getSystemService
import com.pengxh.kt.lite.extensions.navigatePageTo
import com.pengxh.kt.lite.extensions.show
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : KotlinBaseActivity<ActivityMainBinding>() {

    private val kTag = "MainActivity"
    private val timeFormat by lazy { SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA) }
    private val mpm by lazy { getSystemService<MediaProjectionManager>() }
    private val itemNames = listOf(
        "侧边导航栏",
        "拖拽地图选点",
        "音频录制与播放",
        "图片添加水印并压缩",
        "视频压缩",
        "蓝牙相关",
        "可删减九宫格",
        "人脸检测",
        "方向控制盘",
        "时间轴",
        "海康摄像头",
        "雷达扫描效果",
        "指南针",
        "3D画廊",
        "Google ML Kit",
        "拍照保存到相册",
        "截屏",
        "导航卫星信息",
        "音频可视化"
    )
    private var clickTime: Long = 0
    private var screenShortService: ScreenShortRecordService? = null

    override fun setupTopBarLayout() {

    }

    override fun observeRequestState() {

    }

    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {

    }

    override fun initEvent() {
        val adapter = object : NormalRecyclerAdapter<String>(
            R.layout.item_main_rv_g, itemNames.toMutableList()
        ) {
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
                    1 -> navigatePageTo<DragMapActivity>()
                    2 -> navigatePageTo<RecodeAudioActivity>()
                    3 -> navigatePageTo<WaterMarkerActivity>()
                    4 -> navigatePageTo<CompressVideoActivity>()
                    5 -> navigatePageTo<BluetoothActivity>()
                    6 -> navigatePageTo<GridViewActivity>()
                    7 -> navigatePageTo<FaceCollectionActivity>()
                    8 -> navigatePageTo<SteeringWheelActivity>()
                    9 -> navigatePageTo<TimeLineActivity>()
                    10 -> navigatePageTo<HikVisionActivity>()
                    11 -> navigatePageTo<RadarScanActivity>()
                    12 -> navigatePageTo<CompassActivity>()
                    13 -> navigatePageTo<GalleryActivity>()
                    14 -> navigatePageTo<MLKitActivity>()
                    15 -> navigatePageTo<SaveInAlbumActivity>()
                    16 -> {
                        val captureIntent = mpm?.createScreenCaptureIntent()
                        captureIntentLauncher.launch(captureIntent)
                    }

                    17 -> navigatePageTo<SatelliteStatusActivity>()
                    18 -> navigatePageTo<AudioVisualActivity>()
                }
            }
        })
    }

    private val captureIntentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imagePath = "${createImageFileDir()}/${timeFormat.format(Date())}.png"
            result.data?.let {
                screenShortService?.startCaptureScreen(imagePath, it)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, ScreenShortRecordService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
            if (iBinder is ScreenShortRecordService.ServiceBinder) {
                //截屏
                screenShortService = iBinder.getScreenShortRecordService()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            "录屏服务已断开".show(this@MainActivity)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
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
}