package com.example.mutidemo.view

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapOptions
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.UiSettings
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MyLocationStyle
import com.example.mutidemo.R
import com.example.mutidemo.widget.CenterMarkerView
import kotlinx.android.synthetic.main.activity_drag_map.*

//TODO 地理逆编码有问题
class DragMapActivity : AppCompatActivity(), AMap.OnMapLoadedListener,
    AMap.OnCameraChangeListener {

    private lateinit var aMap: AMap
    private var centerMarkerView: CenterMarkerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drag_map)
        mapView.onCreate(savedInstanceState)

        aMap = mapView.map
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
        centerMarkerView = CenterMarkerView(this)
        centerMarkerView?.initInfoWindowsView(aMap)

        aMap.setOnMapLoadedListener(this)
        aMap.setOnCameraChangeListener(this)
    }

    override fun onMapLoaded() {
        centerMarkerView?.addCenterMarker(aMap)
    }

    override fun onCameraChange(cameraPosition: CameraPosition) {
        //隐藏中心点marker的InfoWindow
        centerMarkerView?.hideCenterMarkerInfoWindow()
    }

    override fun onCameraChangeFinish(cameraPosition: CameraPosition) {
        val latLng: LatLng = cameraPosition.target
        //显示infoWindow
        centerMarkerView?.showInfoWindow(latLng)
        longitudeView.text = latLng.longitude.toString()
        latitudeView.text = latLng.latitude.toString()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        if (null != centerMarkerView) {
            centerMarkerView!!.destroy()
            centerMarkerView = null
        }
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}