package com.example.android

import android.Manifest
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.maps.MapsInitializer
import com.example.android.activity.AudioVisualizerActivity
import com.example.android.activity.CompassActivity
import com.example.android.activity.DragMapActivity
import com.example.android.activity.PtzActivity
import com.example.android.activity.RadarScanActivity
import com.example.android.activity.SatelliteStatusActivity
import com.example.android.activity.TimeLineActivity
import com.example.android.databinding.ActivityMainBinding
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.navigatePageTo
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : KotlinBaseActivity<ActivityMainBinding>(),
    EasyPermissions.PermissionCallbacks {

    private val kTag = "MainActivity"
    private val permissionsCode = 999
    private val itemNames = mutableListOf(
        "拖拽地图选点",
        "时间轴",
        "雷达扫描动画",
        "指南针",
        "导航卫星数据",
        "音频可视化",
        "PTZ"
    )

    private val userPermissions = buildList {
        // 通用权限（所有版本）
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)

        // 存储权限根据版本适配
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Android 13+
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                // Android 12+
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                // Android 11+
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                // Android 10+
            }

            else -> {
                // Android 10 及以下
            }
        }
    }.toTypedArray()

    override fun setupTopBarLayout() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(0, statusBarHeight, 0, statusBarHeight / 2)
            insets
        }
    }

    override fun observeRequestState() {

    }

    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        if (!EasyPermissions.hasPermissions(this, *userPermissions)) {
            EasyPermissions.requestPermissions(this, "", permissionsCode, *userPermissions)
        } else {
            MapsInitializer.updatePrivacyShow(this, true, true)
            MapsInitializer.updatePrivacyAgree(this, true)
        }
    }

    override fun initEvent() {
        val adapter = object : NormalRecyclerAdapter<String>(
            R.layout.item_main_rv_g, itemNames
        ) {
            override fun convertView(viewHolder: ViewHolder, position: Int, item: String) {
                viewHolder.setText(R.id.itemTitleView, item)
            }
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(itemDecoration)
        adapter.setOnItemClickedListener(object :
            NormalRecyclerAdapter.OnItemClickedListener<String> {
            override fun onItemClicked(position: Int, item: String) {
                when (position) {
                    0 -> navigatePageTo<DragMapActivity>()
                    1 -> navigatePageTo<TimeLineActivity>()
                    2 -> navigatePageTo<RadarScanActivity>()
                    3 -> navigatePageTo<CompassActivity>()
                    4 -> navigatePageTo<SatelliteStatusActivity>()
                    5 -> navigatePageTo<AudioVisualizerActivity>()
                    6 -> navigatePageTo<PtzActivity>()
                }
            }
        })
    }

    private val itemDecoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
        ) {
            val half = parent.resources.getDimensionPixelOffset(R.dimen.margin) / 2
            outRect.set(half, half, half, half)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
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