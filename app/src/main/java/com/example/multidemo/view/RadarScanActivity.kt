package com.example.multidemo.view

import android.os.Bundle
import com.example.multidemo.databinding.ActivityRadarScanBinding
import com.pengxh.kt.lite.base.KotlinBaseActivity

class RadarScanActivity : KotlinBaseActivity<ActivityRadarScanBinding>() {

    override fun initEvent() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        binding.radarScanView.renderPointData(45f, 300f)
    }

    override fun initViewBinding(): ActivityRadarScanBinding {
        return ActivityRadarScanBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }
}