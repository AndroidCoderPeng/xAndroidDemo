package com.example.multidemo.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import com.example.multidemo.R
import com.example.multidemo.adapter.SatelliteRecyclerAdapter
import com.example.multidemo.databinding.ActivitySatelliteStatusBinding
import com.example.multidemo.model.Satellite
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.convertDrawable
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
                var image = R.drawable.ic_unknown
                when (item.type) {
                    0 -> image = R.drawable.ic_unknown
                    1 -> image = R.drawable.ic_usa
                    3 -> image = R.drawable.ic_russia
                    4 -> image = R.drawable.ic_japen
                    5 -> image = R.drawable.ic_china
                    6 -> image = R.drawable.ic_eu
                    7 -> image = R.drawable.ic_india
                }

                /**
                 * 较弱信号：通常低于 20 dB-Hz。
                 * 中等信号：大约在 20-30 dB-Hz 之间。
                 * 较强信号：通常高于 30 dB-Hz。
                 * 非常强的信号：可以达到 40-50 dB-Hz 或更高。
                 * */
                val signalDrawable = if (item.signal < 20) {
                    R.drawable.bg_progress_bar_low
                } else if (item.signal in 20..29) {
                    R.drawable.bg_progress_bar_middle_low
                } else if (item.signal in 30..39) {
                    R.drawable.bg_progress_bar_middle_high
                } else {
                    R.drawable.bg_progress_bar_high
                }
                val signalProgressView = viewHolder.getView<ProgressBar>(R.id.signalProgressView)
                signalProgressView.progressDrawable = signalDrawable.convertDrawable(context)
                signalProgressView.progress = item.signal

                viewHolder.setImageResource(R.id.nationalityView, image)
                    .setText(R.id.svidView, item.svid.split("_")[1])
                    .setText(R.id.signalValueView, "${item.signal}")
                    .setText(R.id.azimuthView, "${item.azimuth}°")
                    .setText(R.id.elevationView, "${item.elevation}°")
            }
        }
        binding.recyclerView.adapter = satelliteAdapter
    }

    private val locationListener = LocationListener { location -> showLocation(location) }

    private fun showLocation(location: Location?) {
        location?.apply {
            val description =
                "经度：${location.longitude}\n纬度：${location.latitude}\n精度：${location.accuracy}m"
            binding.locationView.text = description
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
                    signal = status.getCn0DbHz(i).toInt() //获取卫星的信号
                    elevation = status.getElevationDegrees(i).toInt()// 获取卫星的仰角
                    azimuth = status.getAzimuthDegrees(i).toInt()// 获取卫星的方位角
                    type = constellationType // 获取卫星的类型
                    typeName = satelliteTypeChineseMap[constellationType] // 获取卫星的类型
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

    }

    override fun initEvent() {

    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.unregisterGnssStatusCallback(gnssStatusListener)
        locationManager.removeUpdates(locationListener)
    }
}