package com.example.multidemo

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import androidx.activity.result.contract.ActivityResultContracts
import com.amap.api.maps.MapsInitializer
import com.example.multidemo.databinding.ActivityMainBinding
import com.example.multidemo.extensions.initImmersionBar
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
import com.example.multidemo.view.SerialPortActivity
import com.example.multidemo.view.SlideNavigationActivity
import com.example.multidemo.view.TimeLineActivity
import com.example.multidemo.view.WaterMarkerActivity
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.createImageFileDir
import com.pengxh.kt.lite.extensions.getSystemService
import com.pengxh.kt.lite.extensions.navigatePageTo
import com.pengxh.kt.lite.extensions.show
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : KotlinBaseActivity<ActivityMainBinding>(),
    EasyPermissions.PermissionCallbacks {

    private val kTag = "MainActivity"
    private val permissionsCode = 999
    private val userPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        arrayOf(
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
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
        "时间轴",
        "海康摄像头",
        "雷达扫描效果",
        "指南针",
        "3D画廊",
        "Google ML Kit",
        "拍照保存到相册",
        "截屏",
        "导航卫星信息",
        "音频可视化",
        "串口"
    )
    private var clickTime: Long = 0
    private var screenShortService: ScreenShortRecordService? = null

    override fun setupTopBarLayout() {
        binding.rootView.initImmersionBar(this, true, R.color.white)
    }

    override fun observeRequestState() {

    }

    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        if (!EasyPermissions.hasPermissions(this, *userPermissions)) {
            EasyPermissions.requestPermissions(this, "", permissionsCode, *userPermissions)
        }
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
                    8 -> navigatePageTo<TimeLineActivity>()
                    9 -> navigatePageTo<HikVisionActivity>()
                    10 -> navigatePageTo<RadarScanActivity>()
                    11 -> navigatePageTo<CompassActivity>()
                    12 -> navigatePageTo<GalleryActivity>()
                    13 -> navigatePageTo<MLKitActivity>()
                    14 -> navigatePageTo<SaveInAlbumActivity>()
                    15 -> {
                        val captureIntent = mpm?.createScreenCaptureIntent()
                        captureIntentLauncher.launch(captureIntent)
                    }

                    16 -> navigatePageTo<SatelliteStatusActivity>()
                    17 -> navigatePageTo<AudioVisualActivity>()
                    18 -> navigatePageTo<SerialPortActivity>()
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

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        //先把导航隐私政策声明，后面导航会用到
        MapsInitializer.updatePrivacyShow(this, true, true)
        MapsInitializer.updatePrivacyAgree(this, true)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {}
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //将请求结果传递EasyPermission库处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}