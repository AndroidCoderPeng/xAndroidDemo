package com.example.android.view

import android.graphics.Color
import android.os.Bundle
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapOptions
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.UiSettings
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MyLocationStyle
import com.example.android.R
import com.example.android.databinding.ActivityDragMapBinding
import com.example.android.extensions.initImmersionBar
import com.example.android.widget.CenterMarkerView
import com.pengxh.kt.lite.base.KotlinBaseActivity

class DragMapActivity : KotlinBaseActivity<ActivityDragMapBinding>(), AMap.OnMapLoadedListener,
    AMap.OnCameraChangeListener {

    private lateinit var aMap: AMap
    private lateinit var centerMarkerView: CenterMarkerView

    override fun initOnCreate(savedInstanceState: Bundle?) {
        binding.mapView.onCreate(savedInstanceState)

        aMap = binding.mapView.map
        aMap.mapType = AMap.MAP_TYPE_NORMAL
        val locationStyle = MyLocationStyle()
        //定位一次，且将视角移动到地图中心点
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
        //设置是否显示定位小蓝点
        locationStyle.showMyLocation(true)
        locationStyle.strokeColor(Color.TRANSPARENT)
        locationStyle.strokeWidth(0f)
        locationStyle.radiusFillColor(Color.TRANSPARENT)
        aMap.myLocationStyle = locationStyle
        aMap.isMyLocationEnabled = true
        val uiSettings: UiSettings = aMap.uiSettings
        uiSettings.isCompassEnabled = true
        uiSettings.zoomPosition = AMapOptions.ZOOM_POSITION_RIGHT_CENTER
        //不许地图随手势倾斜角度
        uiSettings.isTiltGesturesEnabled = false
        //不允许地图旋转
        uiSettings.isRotateGesturesEnabled = false
        //设置默认定位按钮是否显示
        uiSettings.isMyLocationButtonEnabled = true
        //改变地图的缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15f))
        centerMarkerView = CenterMarkerView(this, aMap)

        aMap.setOnMapLoadedListener(this)
        aMap.setOnCameraChangeListener(this)
    }

    override fun initEvent() {

    }

    override fun initViewBinding(): ActivityDragMapBinding {
        return ActivityDragMapBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun onMapLoaded() {
        centerMarkerView.addCenterMarker()
    }

    override fun onCameraChange(cameraPosition: CameraPosition) {
        //隐藏中心点marker的InfoWindow
        centerMarkerView.hideCenterMarkerInfoWindow()
    }

    override fun onCameraChangeFinish(cameraPosition: CameraPosition) {
        val latLng: LatLng = cameraPosition.target
        //显示infoWindow
        centerMarkerView.showInfoWindow(latLng)
        binding.longitudeView.text = latLng.longitude.toString()
        binding.latitudeView.text = latLng.latitude.toString()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        centerMarkerView.destroy()
        binding.mapView.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun setupTopBarLayout() {
        binding.rootView.initImmersionBar(this, true, R.color.white)
    }
}