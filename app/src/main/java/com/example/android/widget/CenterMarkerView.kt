package com.example.android.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.amap.api.maps.AMap
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.animation.Animation
import com.amap.api.maps.model.animation.ScaleAnimation
import com.example.android.R

class CenterMarkerView(private val context: Context, private var aMap: AMap) {
    private lateinit var centerMarker: Marker

    //拿到地图中心点的坐标
    private var latLng = aMap.cameraPosition.target

    init {
        aMap.setInfoWindowAdapter(object : AMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View {
                val infoWindow =
                    LayoutInflater.from(context).inflate(R.layout.map_info_window, null)
                val locationView = infoWindow.findViewById<TextView>(R.id.locationView)
                //手动换行
                locationView.text = "${latLng.latitude}, ${latLng.longitude}"
                return infoWindow
            }

            override fun getInfoContents(p0: Marker?): View? {
                return null
            }
        })
    }

    fun addCenterMarker() {
        val options = MarkerOptions()
        //对应Marker.setIcon方法  设置Marker的图片
        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin))
        //设置infoWindow与锚点的相对位置
        options.anchor(0.5f, 1f)
        //把中心点的坐标转换成屏幕像素位置
        val screenPosition: Point = aMap.projection.toScreenLocation(latLng)
        //在地图上添加Marker并获取到Marker.
        centerMarker = aMap.addMarker(options)
        //给marker设置像素位置。
        centerMarker.setPositionByPixels(screenPosition.x, screenPosition.y)
        centerMarker.setAnchor(0.5f, 1f)
    }

    fun showInfoWindow(latLng: LatLng) {
        this.latLng = latLng
        //缩放动画
        val scaleAnimation: Animation = ScaleAnimation(1f, 1f, 0.75f, 1f)
        //时间设置短点
        scaleAnimation.setDuration(500)
        centerMarker.setAnimation(scaleAnimation)
        centerMarker.startAnimation()
        centerMarker.showInfoWindow()
    }

    fun hideCenterMarkerInfoWindow() {
        centerMarker.hideInfoWindow()
        val bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.map_pin)
        centerMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))
    }

    fun destroy() {
        centerMarker.destroy()
        centerMarker.showInfoWindow()
    }
}