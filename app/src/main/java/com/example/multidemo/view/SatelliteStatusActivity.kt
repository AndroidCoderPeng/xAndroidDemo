package com.example.multidemo.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.GnssStatus
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.multidemo.R
import com.example.multidemo.adapter.SatelliteRecyclerAdapter
import com.example.multidemo.databinding.ActivitySatelliteStatusBinding
import com.example.multidemo.extensions.initImmersionBar
import com.example.multidemo.extensions.toDegree
import com.example.multidemo.model.Satellite
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.divider.RecyclerViewItemDivider
import com.pengxh.kt.lite.extensions.getSystemService
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.extensions.toJson


class SatelliteStatusActivity : KotlinBaseActivity<ActivitySatelliteStatusBinding>(),
    LocationListener {

    private val kTag = "SatelliteActivity"
    private val locationManager by lazy { getSystemService<LocationManager>()!! }
    private val satelliteTypeMap = mapOf(
        0 to "UNKNOWN",
        1 to "GPS",
        3 to "GLONASS",
        4 to "QZSS",
        5 to "BDS",
        6 to "GALILEO",
        7 to "IRNSS",
    )
    private val satelliteCollection = ArrayList<Satellite>()
    private lateinit var satelliteAdapter: SatelliteRecyclerAdapter

    override fun initOnCreate(savedInstanceState: Bundle?) {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            "缺少定位权限".show(this)
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 3000, 0f, this
        )
        locationManager.registerGnssStatusCallback(gnssStatusListener, null)
        satelliteAdapter = SatelliteRecyclerAdapter(this, satelliteCollection)
        binding.recyclerView.adapter = satelliteAdapter
        binding.recyclerView.addItemDecoration(RecyclerViewItemDivider(0f, 0f, Color.WHITE))
    }

    override fun onLocationChanged(location: Location) {
        //转为度分秒
        val lng = location.longitude.toDegree()
        val lat = location.latitude.toDegree()
        binding.locationView.text = "经度：${lng} 纬度：${lat}\n精度：${location.accuracy}m"
    }

    private val gnssStatusListener = object : GnssStatus.Callback() {
        override fun onSatelliteStatusChanged(status: GnssStatus) {
            satelliteCollection.clear()
            for (i in 0 until status.satelliteCount) {
                //在同一个导航系统内，svid是唯一的，不会重复，但是，不同的导航系统可能会使用相同的svid数值
                val constellationType = status.getConstellationType(i)
                val satellite = Satellite().apply {
                    svid = "${satelliteTypeMap[constellationType]}_${status.getSvid(i)}"
                    signal = status.getCn0DbHz(i).toInt() //获取卫星的信号
                    elevation = status.getElevationDegrees(i).toInt()// 获取卫星的仰角
                    azimuth = status.getAzimuthDegrees(i).toInt()// 获取卫星的方位角
                    type = constellationType // 获取卫星的类型
                    isUsedInFix = status.usedInFix(i)
                }
                if (satellite.signal != 0) {
                    satelliteCollection.add(satellite)
                }
            }
            Log.d(kTag, satelliteCollection.toJson())
            satelliteAdapter.notifyDataSetChanged()
        }
    }

    override fun initViewBinding(): ActivitySatelliteStatusBinding {
        return ActivitySatelliteStatusBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {
        binding.rootView.initImmersionBar(this, false, R.color.lib_text_color)
    }

    override fun initEvent() {

    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.unregisterGnssStatusCallback(gnssStatusListener)
        locationManager.removeUpdates(this)
    }
}