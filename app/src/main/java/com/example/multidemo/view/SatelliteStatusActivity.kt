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
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import com.example.multidemo.R
import com.example.multidemo.adapter.SatelliteRecyclerAdapter
import com.example.multidemo.databinding.ActivitySatelliteStatusBinding
import com.example.multidemo.model.Satellite
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.divider.RecyclerViewItemDivider
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
                    1 -> image = R.drawable.ic_usa
                    3 -> image = R.drawable.ic_russia
                    4 -> image = R.drawable.ic_japen
                    5 -> image = R.drawable.ic_china
                    6 -> image = R.drawable.ic_eu
                    7 -> image = R.drawable.ic_india
                }

                val signalDrawable = if (item.isHasAlmanac) {
                    if (item.signal <= 19) {
                        R.drawable.bg_progress_bar_middle_low
                    } else if (item.signal in 20..29) {
                        R.drawable.bg_progress_bar_middle_high
                    } else {
                        R.drawable.bg_progress_bar_high
                    }
                } else {
                    R.drawable.bg_progress_bar_low
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
        binding.recyclerView.addItemDecoration(RecyclerViewItemDivider(1, Color.WHITE))
    }

    private val locationListener = LocationListener { location -> showLocation(location) }

    private fun showLocation(location: Location?) {
        location?.apply {
            val description =
                "经度：${location.longitude}\n纬度：${location.latitude}\n精度：${location.accuracy}m"
            binding.locationView.text = description
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
                    //TODO 待定
                    isHasAlmanac = status.hasAlmanacData(i)
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