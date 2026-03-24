package com.example.android

import android.Manifest
import android.os.Build
import android.os.Bundle
import com.amap.api.maps.MapsInitializer
import com.example.android.base.extensions.initImmersionBar
import com.example.android.compass.CompassActivity
import com.example.android.databinding.ActivityMainBinding
import com.example.android.gallery.GalleryActivity
import com.example.android.map.DragMapActivity
import com.example.android.ptz.PtzActivity
import com.example.android.radar.RadarScanActivity
import com.example.android.satellite.SatelliteStatusActivity
import com.example.android.shopping.AddProductAnimationActivity
import com.example.android.slide.SlideNavigationActivity
import com.example.android.timeline.TimeLineActivity
import com.example.android.video.WrapVideoActivity
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.navigatePageTo
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : KotlinBaseActivity<ActivityMainBinding>(),
    EasyPermissions.PermissionCallbacks {

    private val kTag = "MainActivity"
    private val permissionsCode = 999
    private val itemNames = listOf(
        "侧边导航栏",
        "拖拽地图选点",
        "图片添加水印并压缩",
        "时间轴",
        "雷达扫描效果",
        "指南针",
        "3D画廊",
        "导航卫星数据",
        "商品添加购物车",
        "YUV420分析",
        "封装音视频",
        "WebRTC",
        "PTZ"
    )

    private val userPermissions = buildList {
        // 通用权限（所有版本）
        add(Manifest.permission.CAMERA)
        add(Manifest.permission.READ_PHONE_STATE)
        add(Manifest.permission.RECORD_AUDIO)
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)

        // 存储权限根据版本适配
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Android 13+ 使用新的媒体权限
                add(Manifest.permission.READ_MEDIA_VIDEO)
                add(Manifest.permission.READ_MEDIA_IMAGES)
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                // Android 11+ 需要请求 MANAGE_EXTERNAL_STORAGE
                add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

            else -> {
                // Android 10 及以下
            }
        }
    }.toTypedArray()

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
            override fun onItemClicked(position: Int, item: String) {
                when (position) {
                    0 -> navigatePageTo<SlideNavigationActivity>()
                    1 -> navigatePageTo<DragMapActivity>()
                    2 -> navigatePageTo<WaterMarkerActivity>()
                    3 -> navigatePageTo<TimeLineActivity>()
                    4 -> navigatePageTo<RadarScanActivity>()
                    5 -> navigatePageTo<CompassActivity>()
                    6 -> navigatePageTo<GalleryActivity>()
                    7 -> navigatePageTo<SatelliteStatusActivity>()
                    8 -> navigatePageTo<AddProductAnimationActivity>()
                    9 -> navigatePageTo<YuvDataActivity>()
                    10 -> navigatePageTo<WrapVideoActivity>()
                    11 -> navigatePageTo<WebRtcActivity>()
                    12 -> navigatePageTo<PtzActivity>()
                }
            }
        })
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