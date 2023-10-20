package com.example.multidemo.view

import android.os.Bundle
import com.example.multidemo.databinding.ActivityRadarScanBinding
import com.example.multidemo.widget.RadarScanView
import com.pengxh.kt.lite.base.KotlinBaseActivity

class RadarScanActivity : KotlinBaseActivity<ActivityRadarScanBinding>() {

    override fun initEvent() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        val dataPoints = ArrayList<RadarScanView.DataPoint>()
        dataPoints.add(RadarScanView.DataPoint(60f, 125f))
        dataPoints.add(RadarScanView.DataPoint(45f, 115f))
        dataPoints.add(RadarScanView.DataPoint(120f, 95f))
        dataPoints.add(RadarScanView.DataPoint(225f, 145f))
        dataPoints.add(RadarScanView.DataPoint(345f, 75f))
        binding.radarScanView.renderPointData(dataPoints)
    }

    override fun initViewBinding(): ActivityRadarScanBinding {
        return ActivityRadarScanBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }
}