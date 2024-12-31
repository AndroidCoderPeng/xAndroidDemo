package com.example.multidemo.view

import android.Manifest
import android.content.pm.PackageManager
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
import com.example.multidemo.model.Satellite
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.getSystemService
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.extensions.toJson


class SatelliteStatusActivity : KotlinBaseActivity<ActivitySatelliteStatusBinding>() {

    private val kTag = "SatelliteActivity"
    private val context = this
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
    private val satelliteTypeChineseMap = mapOf(
        0 to "未知导航系统（UNKNOWN）",
        1 to "全球定位系统（GPS）",
        3 to "格洛纳斯系统（GLONASS）",
        4 to "准天顶卫星系统（QZSS）",
        5 to "北斗卫星导航系统（BDS）",
        6 to "伽利略卫星导航系统（GALILEO）",
        7 to "印度区域导航卫星系统（IRNSS）",
    )
    private val satelliteCollection = ArrayList<Satellite>()
    private lateinit var satelliteAdapter: SatelliteRecyclerAdapter<Satellite>

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
            LocationManager.GPS_PROVIDER, 2000, 0f, locationListener
        )
        showLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER))
        locationManager.registerGnssStatusCallback(gnssStatusListener, null)
        satelliteAdapter = object : SatelliteRecyclerAdapter<Satellite>(
            R.layout.item_satellite_rv_l, satelliteCollection
        ) {
            override fun convertView(viewHolder: ViewHolder, position: Int, item: Satellite) {
                val image = when (item.type) {
                    0 -> R.drawable.ic_unknown
                    1 -> R.drawable.ic_usa
                    3 -> R.drawable.ic_russia
                    4 -> R.drawable.ic_japen
                    5 -> R.drawable.ic_china
                    6 -> R.drawable.ic_eu
                    7 -> R.drawable.ic_india
                    else -> R.drawable.ic_unknown
                }

                viewHolder.setImageResource(R.id.nationalityView, image)
                    .setText(R.id.svidView, item.svid.split("_")[1])
                    .setText(R.id.signalValueView, "${item.signal}")
                    .setText(R.id.azimuthView, "${item.azimuth}")
                    .setText(R.id.elevationView, "${item.elevation}")
            }
        }
        binding.recyclerView.adapter = satelliteAdapter
    }

    private val locationListener = LocationListener { location -> showLocation(location) }

    private fun showLocation(location: Location?) {
        location?.apply {
            binding.locationView.text =
                "经度：${location.longitude}，纬度：${location.latitude}，精度：${location.accuracy}m"
        } ?: run {
            Log.d(kTag, "Location is null")
        }
    }

    private val gnssStatusListener = object : GnssStatus.Callback() {
        override fun onSatelliteStatusChanged(status: GnssStatus) {
            satelliteCollection.clear()
            for (i in 0 until status.satelliteCount) {
                //在同一个导航系统内，svid是唯一的，不会重复，但是，不同的导航系统可能会使用相同的svid数值
                val constellationType = status.getConstellationType(i)
                val satellite = Satellite().apply {
                    svid = "${satelliteTypeMap[constellationType]}_${status.getSvid(i)}"
                    signal = status.getCn0DbHz(i) //获取卫星的信号
                    elevation = status.getElevationDegrees(i) // 获取卫星的仰角
                    azimuth = status.getAzimuthDegrees(i) // 获取卫星的方位角
                    type = constellationType // 获取卫星的类型
                    typeName = satelliteTypeChineseMap[constellationType] // 获取卫星的类型
                }
                satelliteCollection.add(satellite)
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

    }

    override fun initEvent() {

    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.unregisterGnssStatusCallback(gnssStatusListener)
        locationManager.removeUpdates(locationListener)
    }
}