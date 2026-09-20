package com.example.android.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.GnssStatus
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.android.adapter.SatelliteRecyclerAdapter
import com.example.android.databinding.ActivitySatelliteStatusBinding
import com.example.android.extensions.toDegree
import com.example.android.model.Satellite
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.divider.RecyclerViewItemDivider
import com.pengxh.kt.lite.extensions.show

class SatelliteStatusActivity : KotlinBaseActivity<ActivitySatelliteStatusBinding>(),
    LocationListener {

    companion object {
        private val CONSTELLATION_NAMES = listOf(
            "UNKNOWN",  // 0: 未知星座 (CONSTELLATION_UNKNOWN)
            "GPS",      // 1: 美国GPS卫星导航系统 (CONSTELLATION_GPS)
            "SBAS",     // 2: 星基增强系统 (CONSTELLATION_SBAS)
            "GLONASS",  // 3: 俄罗斯格洛纳斯卫星导航系统 (CONSTELLATION_GLONASS)
            "QZSS",     // 4: 日本准天顶卫星系统 (CONSTELLATION_QZSS)
            "BDS",      // 5: 中国北斗卫星导航系统 (CONSTELLATION_BEIDOU)
            "GALILEO",  // 6: 欧盟伽利略卫星导航系统 (CONSTELLATION_GALILEO)
            "IRNSS"     // 7: 印度区域导航卫星系统 (CONSTELLATION_IRNSS)
        )
    }

    private val locationManager by lazy { getSystemService(LocationManager::class.java) }
    private val satelliteTypeMap: Map<Int, String> =
        CONSTELLATION_NAMES.mapIndexed { index, name -> index to name }.toMap()

    private lateinit var satelliteAdapter: SatelliteRecyclerAdapter

    override fun initViewBinding(): ActivitySatelliteStatusBinding {
        return ActivitySatelliteStatusBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(0, statusBarHeight, 0, 0)
            insets
        }

        binding.toolbar.setNavigationOnClickListener { finish() }
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
        satelliteAdapter = SatelliteRecyclerAdapter(this, ArrayList())
        binding.recyclerView.adapter = satelliteAdapter
        binding.recyclerView.addItemDecoration(RecyclerViewItemDivider(0f, 0f, Color.LTGRAY))
    }

    override fun onLocationChanged(location: Location) {
        //转为度分秒
        val lng = location.longitude.toDegree()
        val lat = location.latitude.toDegree()
        val str = buildString {
            append("经度：${lng} 纬度：${lat}\n")
            append("经度：%.9f 纬度：%.9f\n".format(location.longitude, location.latitude))
            append("精度：${location.accuracy}m\n")
            append("类型：GPS")
        }
        binding.locationView.text = str
    }

    private val gnssStatusListener = object : GnssStatus.Callback() {
        override fun onSatelliteStatusChanged(status: GnssStatus) {
            // 使用 Map 去重，相同 svid 的卫星只保留最后一个（信号值最新的）
            val satelliteMap = LinkedHashMap<String, Satellite>()
            //只统计已捕获到信号的卫星
            var totalCount = 0
            for (i in 0 until status.satelliteCount) {
                //C/N0 为 0 表示该卫星尚未捕获到信号，不参与统计与展示
                val cn0 = status.getCn0DbHz(i)
                if (cn0 <= 0f) continue
                totalCount++

                val constellationType = status.getConstellationType(i)
                val satellite = Satellite().apply {
                    svid =
                        "${satelliteTypeMap[constellationType] ?: "UNKNOWN"}_${status.getSvid(i)}"
                    signal = cn0.toInt()
                    elevation = status.getElevationDegrees(i).toInt()
                    azimuth = status.getAzimuthDegrees(i).toInt()
                    type = constellationType
                    isUsedInFix = status.usedInFix(i)
                }
                satelliteMap[satellite.svid] = satellite
            }

            val availableCount = satelliteMap.values.count { it.isUsedInFix }
            binding.toolbar.title = "卫星定位信号（$availableCount/$totalCount）"

            // 将 Map 的值转为列表
            val newSatellites = ArrayList(satelliteMap.values)
            // 让信号值更强的排在前面
            newSatellites.sortWith(compareByDescending<Satellite> {
                it.signal
            })
            satelliteAdapter.refresh(newSatellites)
        }
    }

    override fun initEvent() {

    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.unregisterGnssStatusCallback(gnssStatusListener)
        locationManager.removeUpdates(this)
    }
}