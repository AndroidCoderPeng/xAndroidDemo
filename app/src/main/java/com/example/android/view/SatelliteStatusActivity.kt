package com.example.android.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.GnssStatus
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.android.adapter.SatelliteRecyclerAdapter
import com.example.android.databinding.ActivitySatelliteStatusBinding
import com.example.android.extensions.toDegree
import com.example.android.model.Satellite
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.divider.RecyclerViewItemDivider
import com.pengxh.kt.lite.extensions.getSystemService
import com.pengxh.kt.lite.extensions.show

@SuppressLint("SetTextI18n")
class SatelliteStatusActivity : KotlinBaseActivity<ActivitySatelliteStatusBinding>(),
    LocationListener {

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
    private lateinit var satelliteAdapter: SatelliteRecyclerAdapter

    override fun initViewBinding(): ActivitySatelliteStatusBinding {
        return ActivitySatelliteStatusBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }

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
        satelliteAdapter = SatelliteRecyclerAdapter(this, ArrayList<Satellite>())
        binding.recyclerView.adapter = satelliteAdapter
        binding.recyclerView.addItemDecoration(RecyclerViewItemDivider(0f, 0f, Color.WHITE))
    }

    override fun onLocationChanged(location: Location) {
        //转为度分秒
        val lng = location.longitude.toDegree()
        val lat = location.latitude.toDegree()
        binding.locationView.text = "经度：${lng} 纬度：${lat}\n" +
                "经度：%.9f 纬度：%.9f\n".format(location.longitude, location.latitude) +
                "精度：${location.accuracy}m\n" +
                "类型：GPS"
    }

    private val gnssStatusListener = object : GnssStatus.Callback() {
        override fun onSatelliteStatusChanged(status: GnssStatus) {
            val newSatellites = mutableListOf<Satellite>()
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
                    newSatellites.add(satellite)
                }
            }
            satelliteAdapter.refresh(newSatellites)
        }
    }

    override fun initEvent() {

    }

    override fun onDestroy() {
        locationManager.unregisterGnssStatusCallback(gnssStatusListener)
        locationManager.removeUpdates(this)
        super.onDestroy()
    }
}