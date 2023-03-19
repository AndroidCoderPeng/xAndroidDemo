package com.example.mutidemo.widget

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
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.example.mutidemo.R

class CenterMarkerView(private val context: Context) {
    private var centerMarker: Marker? = null
    private var latLng: LatLng? = null
    private val geocoderSearch by lazy { GeocodeSearch(context) }

    fun addCenterMarker(aMap: AMap) {
        val options = MarkerOptions()
        //对应Marker.setIcon方法  设置Marker的图片
        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin))
        //设置infoWindow与锚点的相对位置
        options.anchor(0.5f, 1f)
        //拿到地图中心点的坐标。
        val latLng: LatLng = aMap.cameraPosition.target
        //把中心点的坐标转换成屏幕像素位置
        val screenPosition: Point = aMap.projection.toScreenLocation(latLng)
        //在地图上添加Marker并获取到Marker.
        centerMarker = aMap.addMarker(options)
        //给marker设置像素位置。
        centerMarker!!.setPositionByPixels(screenPosition.x, screenPosition.y)
        centerMarker!!.setAnchor(0.5f, 1f)
    }

    fun initInfoWindowsView(aMap: AMap) {
        aMap.setInfoWindowAdapter(object : AMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View {
                val infoWindow =
                    LayoutInflater.from(context).inflate(R.layout.map_info_window, null)
                val locationView: TextView = infoWindow.findViewById(R.id.locationView)
                val queryParam = RegeocodeQuery(
                    LatLonPoint(latLng!!.latitude, latLng!!.longitude),
                    200f,
                    GeocodeSearch.AMAP
                )
                geocoderSearch.getFromLocationAsyn(queryParam)
                geocoderSearch.setOnGeocodeSearchListener(object :
                    GeocodeSearch.OnGeocodeSearchListener {
                    override fun onRegeocodeSearched(regeocodeResult: RegeocodeResult, code: Int) {
                        if (code == 1000) {
                            //手动换行
                            val address = regeocodeResult.regeocodeAddress.formatAddress
                            val temp = StringBuilder()
                            if (address.length > 20) {
                                temp.append(address.substring(0, 20)).append("\r\n")
                                    .append(address.substring(20))
                            } else {
                                temp.append(address)
                            }
                            locationView.text = temp
                        }
                    }

                    override fun onGeocodeSearched(geocodeResult: GeocodeResult?, i: Int) {}
                })
                return infoWindow
            }

            override fun getInfoContents(p0: Marker?): View? {
                return null
            }
        })
    }

    fun showInfoWindow(latLng: LatLng?) {
        this.latLng = latLng
        if (null != centerMarker) {
            //缩放动画
            val scaleAnimation: Animation = ScaleAnimation(1f, 1f, 0.75f, 1f)
            //时间设置短点
            scaleAnimation.setDuration(500)
            centerMarker!!.setAnimation(scaleAnimation)
            centerMarker!!.startAnimation()
            centerMarker!!.showInfoWindow()
        }
    }

    fun hideCenterMarkerInfoWindow() {
        centerMarker!!.hideInfoWindow()
        if (null != centerMarker) {
            val bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.map_pin)
            centerMarker!!.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))
        }
    }

    fun destroy() {
        if (null != centerMarker) {
            centerMarker!!.destroy()
            centerMarker!!.showInfoWindow()
        }
    }
}