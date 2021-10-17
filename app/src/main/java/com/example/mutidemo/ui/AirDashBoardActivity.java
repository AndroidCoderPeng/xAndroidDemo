package com.example.mutidemo.ui;

import com.example.mutidemo.R;
import com.example.mutidemo.util.ColorUtil;
import com.example.mutidemo.widget.AirDashBoardView;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import butterknife.BindView;

public class AirDashBoardActivity extends BaseNormalActivity {

    @BindView(R.id.dashBoardView)
    AirDashBoardView dashBoardView;

    @Override
    public int initLayoutView() {
        return R.layout.activity_air_dash;
    }

    @Override
    public void initData() {
        dashBoardView.setMinValue(0);
        dashBoardView.setMaxValue(500);
        int aqiValue = 501;
        dashBoardView.setCurrentValue(aqiValue);
        dashBoardView.setCenterText("优");
        dashBoardView.setAirRingForeground(ColorUtil.aqiToColor(this, aqiValue));
        dashBoardView.setAirCenterTextColor(ColorUtil.aqiToColor(this, aqiValue));
        dashBoardView.setAirCurrentValueColor(ColorUtil.aqiToColor(this, aqiValue));
    }

    @Override
    public void initEvent() {

    }
}
