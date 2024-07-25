package com.example.multidemo

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.location.LocationListener
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.KeyEvent
import androidx.activity.result.contract.ActivityResultContracts
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapOptions
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.CoordinateConverter
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.example.multidemo.databinding.ActivityMainBinding
import com.example.multidemo.service.ScreenShortRecordService
import com.example.multidemo.util.LocationHelper
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
import com.example.multidemo.view.SaveInAlbumActivity
import com.example.multidemo.view.SlideBarActivity
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
    private val converter by lazy { CoordinateConverter(this) }
    private val itemNames = listOf(
        "侧边导航栏",
        "上拉加载下拉刷新",
        "联系人侧边滑动控件",
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
        "截屏"
    )
    private var clickTime: Long = 0
    private var screenShortService: ScreenShortRecordService? = null
    private lateinit var aMap: AMap

    override fun setupTopBarLayout() {

    }

    override fun observeRequestState() {

    }

    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        binding.mapView.onCreate(savedInstanceState)
        aMap = binding.mapView.map
        aMap.mapType = AMap.MAP_TYPE_NORMAL
        val uiSettings = aMap.uiSettings
        uiSettings.isCompassEnabled = true
        uiSettings.zoomPosition = AMapOptions.ZOOM_POSITION_RIGHT_CENTER
        uiSettings.isTiltGesturesEnabled = false//不许地图随手势倾斜角度
        uiSettings.isRotateGesturesEnabled = false//不允许地图随手势改变方位

        LocationHelper.getCurrentLocation(this, object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val latitude = location.latitude
                val longitude = location.longitude
                binding.locationView.text = "lat: $latitude, lng: $longitude"

                val wgs84 = LatLng(latitude, longitude)
                converter.from(CoordinateConverter.CoordType.GPS)
                converter.coord(wgs84)
                val convert = converter.convert()

                val cameraPosition = CameraPosition(convert, 18f, 0f, 0f)
                val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
                aMap.moveCamera(cameraUpdate)

                aMap.clear()
                aMap.addMarker(MarkerOptions().position(convert))
            }

            override fun onProviderEnabled(provider: String) {
                super.onProviderEnabled(provider)
                Log.d(kTag, "onProviderEnabled: ")
            }

            override fun onProviderDisabled(provider: String) {
                super.onProviderDisabled(provider)
                Log.d(kTag, "onProviderDisabled: ")
            }
        })
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
                    1 -> navigatePageTo<RefreshAndLoadMoreActivity>()
                    2 -> navigatePageTo<SlideBarActivity>()
                    3 -> navigatePageTo<DragMapActivity>()
                    4 -> navigatePageTo<RecodeAudioActivity>()
                    5 -> navigatePageTo<WaterMarkerActivity>()
                    6 -> navigatePageTo<CompressVideoActivity>()
                    7 -> navigatePageTo<BluetoothActivity>()
                    8 -> navigatePageTo<GridViewActivity>()
                    9 -> navigatePageTo<FaceCollectionActivity>()
                    10 -> navigatePageTo<SteeringWheelActivity>()
                    11 -> navigatePageTo<TimeLineActivity>()
                    12 -> navigatePageTo<HikVisionActivity>()
                    13 -> navigatePageTo<RadarScanActivity>()
                    14 -> navigatePageTo<CompassActivity>()
                    15 -> navigatePageTo<GalleryActivity>()
                    16 -> navigatePageTo<MLKitActivity>()
                    17 -> navigatePageTo<SaveInAlbumActivity>()
                    18 -> {
                        val captureIntent = mpm?.createScreenCaptureIntent()
                        captureIntentLauncher.launch(captureIntent)
                    }
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

    /***以下是地图生命周期管理************************************************************************/
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }
}