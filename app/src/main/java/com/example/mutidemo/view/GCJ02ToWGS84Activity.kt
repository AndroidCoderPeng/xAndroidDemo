package com.example.mutidemo.view

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import com.amap.api.location.AMapLocation
import com.amap.api.services.core.AMapException
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReference
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol
import com.example.mutidemo.R
import com.example.mutidemo.callback.IAddressListener
import com.example.mutidemo.callback.ILocationListener
import com.example.mutidemo.util.LoadingDialogHub
import com.example.mutidemo.util.LocationHelper
import com.pengxh.kt.lite.base.KotlinBaseActivity
import kotlinx.android.synthetic.main.activity_gis.*

class GCJ02ToWGS84Activity : KotlinBaseActivity() {

    private val kTag = "GCJ02ToWGS84Activity"
    private val context: Context = this@GCJ02ToWGS84Activity

    override fun setupTopBarLayout() {}

    override fun observeRequestState() {

    }

    override fun initLayoutView(): Int = R.layout.activity_gis

    override fun initData(savedInstanceState: Bundle?) {
        mapView.isAttributionTextVisible = false //去掉左下角属性标识
        mapView.setViewpointScaleAsync(2800.0) //数字越大，放大比例越小，缩放比例[36000,250]
        val arcGISMap = ArcGISMap(BasemapStyle.ARCGIS_STREETS)
        mapView.map = arcGISMap
    }

    override fun initEvent() {
        LocationHelper.obtainCurrentLocationByGD(this, object : ILocationListener {
            override fun onLocationGet(location: Location?) {}
            override fun onAMapLocationGet(aMapLocation: AMapLocation?) {
                if (aMapLocation != null) {
                    val gcjToWgs = LocationHelper.gcjToWgs(
                        aMapLocation.longitude, aMapLocation.latitude
                    )
                    Log.d(kTag, "GCJ-02: [" + gcjToWgs[0] + "," + gcjToWgs[1] + "]")
                    val point: Point = Point(
                        gcjToWgs[0], gcjToWgs[1], SpatialReference.create(4326)
                    )
                    addPictureMarker(point, false)
                    mapView.setViewpointCenterAsync(point, 28000.0)
                }
            }
        }, true)
        expandMapView.setOnClickListener {
            mapView.setViewpointScaleAsync(
                mapView.mapScale * 0.5
            )
        }
        minusMapView.setOnClickListener {
            mapView.setViewpointScaleAsync(
                mapView.mapScale * 2
            )
        }
        removeToLocalView.setOnClickListener {
            LoadingDialogHub.show(this, "定位中，请稍后")
            LocationHelper.obtainCurrentLocation(
                this, object : ILocationListener {
                    override fun onLocationGet(location: Location?) {
                        if (location != null) {
                            Log.d(
                                kTag,
                                "WGS-84: [" + location.longitude + "," + location.latitude + "]"
                            )
                            val point = Point(
                                location.longitude, location.latitude, SpatialReference.create(4326)
                            )
                            addPictureMarker(point, true)
                            mapView.setViewpointCenterAsync(point, 28000.0)
                            LoadingDialogHub.dismiss()
                            //显示具体位置
                            try {
                                LocationHelper.antiCodingLocation(
                                    context, location.longitude, location.latitude,
                                    object : IAddressListener {
                                        override fun onGetAddress(address: String?) {
                                            addressView.text = address
                                        }
                                    })
                            } catch (e: AMapException) {
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onAMapLocationGet(aMapLocation: AMapLocation?) {}
                },
                true
            )
        }
    }

    private fun addPictureMarker(point: Point, isGPS: Boolean) {
        val caseBitmap = if (isGPS) {
            BitmapFactory.decodeResource(resources, R.mipmap.location_handle)
        } else {
            BitmapFactory.decodeResource(resources, R.mipmap.location)
        }
        val caseDrawable = BitmapDrawable(resources, caseBitmap)
        val pictureMarker = PictureMarkerSymbol(caseDrawable)
        pictureMarker.width = 24f
        pictureMarker.height = 24f
        pictureMarker.loadAsync()
        val graphic = Graphic(point, pictureMarker)
        val mGraphicsOverlay = GraphicsOverlay()
        val overlayGraphics = mGraphicsOverlay.graphics
        val graphicsOverlays = mapView.graphicsOverlays
        overlayGraphics.add(graphic)
        graphicsOverlays.add(mGraphicsOverlay)
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onPause() {
        super.onPause()
        mapView.pause()
    }

    override fun onDestroy() {
        mapView.dispose()
        super.onDestroy()
    }
}