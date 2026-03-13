package com.example.android

import android.Manifest
import android.os.Build
import android.os.Bundle
import com.amap.api.maps.MapsInitializer
import com.example.android.databinding.ActivityMainBinding
import com.example.android.extensions.initImmersionBar
import com.example.android.view.AddProductAnimationActivity
import com.example.android.view.CompassActivity
import com.example.android.view.DragMapActivity
import com.example.android.view.GalleryActivity
import com.example.android.view.GridViewActivity
import com.example.android.view.MLKitActivity
import com.example.android.view.RadarScanActivity
import com.example.android.view.RecodeAudioActivity
import com.example.android.view.SatelliteStatusActivity
import com.example.android.view.SaveInAlbumActivity
import com.example.android.view.SlideNavigationActivity
import com.example.android.view.TimeLineActivity
import com.example.android.view.WaterMarkerActivity
import com.example.android.view.WrapVideoActivity
import com.example.android.view.YuvDataActivity
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
        "音频录制与播放",
        "图片添加水印并压缩",
        "可删减九宫格",
        "时间轴",
        "雷达扫描效果",
        "指南针",
        "3D画廊",
        "Google ML Kit",
        "拍照保存到相册",
        "导航卫星数据",
        "商品添加购物车",
        "YUV420分析",
        "封装音视频"
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
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                    2 -> navigatePageTo<RecodeAudioActivity>()
                    3 -> navigatePageTo<WaterMarkerActivity>()
                    4 -> navigatePageTo<GridViewActivity>()
                    5 -> navigatePageTo<TimeLineActivity>()
                    6 -> navigatePageTo<RadarScanActivity>()
                    7 -> navigatePageTo<CompassActivity>()
                    8 -> navigatePageTo<GalleryActivity>()
                    9 -> navigatePageTo<MLKitActivity>()
                    10 -> navigatePageTo<SaveInAlbumActivity>()
                    11 -> navigatePageTo<SatelliteStatusActivity>()
                    12 -> navigatePageTo<AddProductAnimationActivity>()
                    13 -> navigatePageTo<YuvDataActivity>()
                    14 -> navigatePageTo<WrapVideoActivity>()
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