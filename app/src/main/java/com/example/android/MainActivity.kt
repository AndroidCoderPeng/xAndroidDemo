package com.example.android

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import com.amap.api.maps.MapsInitializer
import com.example.android.databinding.ActivityMainBinding
import com.example.android.extensions.initImmersionBar
import com.example.android.view.AddProductAnimationActivity
import com.example.android.view.AudioVisualActivity
import com.example.android.view.BluetoothActivity
import com.example.android.view.CompassActivity
import com.example.android.view.CompressVideoActivity
import com.example.android.view.DragMapActivity
import com.example.android.view.FaceDetectActivity
import com.example.android.view.GalleryActivity
import com.example.android.view.GridViewActivity
import com.example.android.view.HikVisionActivity
import com.example.android.view.MLKitActivity
import com.example.android.view.RadarScanActivity
import com.example.android.view.RecodeAudioActivity
import com.example.android.view.SatelliteStatusActivity
import com.example.android.view.SaveInAlbumActivity
import com.example.android.view.SlideNavigationActivity
import com.example.android.view.TimeLineActivity
import com.example.android.view.WaterMarkerActivity
import com.example.android.view.YuvDataActivity
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.navigatePageTo
import com.pengxh.kt.lite.extensions.show
import pub.devrel.easypermissions.EasyPermissions

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
        "导航卫星信息",
        "音频可视化",
        "商品加购购物车效果",
        "YUV420分析"
    )
    private var clickTime: Long = 0

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
                    7 -> navigatePageTo<FaceDetectActivity>()
                    8 -> navigatePageTo<TimeLineActivity>()
                    9 -> navigatePageTo<HikVisionActivity>()
                    10 -> navigatePageTo<RadarScanActivity>()
                    11 -> navigatePageTo<CompassActivity>()
                    12 -> navigatePageTo<GalleryActivity>()
                    13 -> navigatePageTo<MLKitActivity>()
                    14 -> navigatePageTo<SaveInAlbumActivity>()
                    15 -> navigatePageTo<SatelliteStatusActivity>()
                    16 -> navigatePageTo<AudioVisualActivity>()
                    17 -> navigatePageTo<AddProductAnimationActivity>()
                    18 -> navigatePageTo<YuvDataActivity>()
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