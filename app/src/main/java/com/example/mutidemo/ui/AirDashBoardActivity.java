package com.example.mutidemo.ui;

import com.example.mutidemo.base.AndroidxBaseActivity;
import com.example.mutidemo.databinding.ActivityAirDashBinding;
import com.example.mutidemo.util.ColorUtil;

public class AirDashBoardActivity extends AndroidxBaseActivity<ActivityAirDashBinding> {

    @Override
    public void initData() {
        viewBinding.dashBoardView.setMinValue(0);
        viewBinding.dashBoardView.setMaxValue(500);
        int aqiValue = 128;
        viewBinding.dashBoardView.setCurrentValue(aqiValue);
        viewBinding.dashBoardView.setCenterText("良");
        viewBinding.dashBoardView.setAirRingForeground(ColorUtil.aqiToColor(this, aqiValue));
        viewBinding.dashBoardView.setAirCenterTextColor(ColorUtil.aqiToColor(this, aqiValue));
        viewBinding.dashBoardView.setAirCurrentValueColor(ColorUtil.aqiToColor(this, aqiValue));
    }

    @Override
    public void initEvent() {

    }
}
