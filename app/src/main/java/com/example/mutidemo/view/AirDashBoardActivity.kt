package com.example.mutidemo.view

import android.os.Bundle
import com.example.mutidemo.R
import com.example.mutidemo.util.ColorUtil
import com.pengxh.kt.lite.base.KotlinBaseActivity
import kotlinx.android.synthetic.main.activity_air_dash.*

class AirDashBoardActivity : KotlinBaseActivity() {

    override fun initData(savedInstanceState: Bundle?) {
        dashBoardView.setMinValue(0)
        dashBoardView.setMaxValue(500)
        val aqiValue = 128
        dashBoardView.setCurrentValue(aqiValue)
        dashBoardView.setCenterText("良")
        dashBoardView.setAirRingForeground(ColorUtil.aqiToColor(this, aqiValue))
        dashBoardView.setAirCenterTextColor(ColorUtil.aqiToColor(this, aqiValue))
        dashBoardView.setAirCurrentValueColor(ColorUtil.aqiToColor(this, aqiValue))
    }

    override fun initEvent() {

    }

    override fun initLayoutView(): Int = R.layout.activity_air_dash

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }
}