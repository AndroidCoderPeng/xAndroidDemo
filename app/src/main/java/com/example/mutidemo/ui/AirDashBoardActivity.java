package com.example.mutidemo.ui;

import com.example.mutidemo.R;
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
        dashBoardView.setCurrentValue(420);
        dashBoardView.setCenterText("优");
    }

    @Override
    public void initEvent() {

    }
}
