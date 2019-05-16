package com.example.mutidemo.ui;

import com.example.mutidemo.R;
import com.example.mutidemo.view.BarChartView;
import com.pengxh.app.multilib.base.NormalActivity;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/12/15.
 */

public class BarChartActivity extends NormalActivity {

    @BindView(R.id.mBarChartView)
    BarChartView mBarChartView;

    @Override
    public void initView() {
        setContentView(R.layout.layout_barchart);
    }

    @Override
    public void init() {

    }

    @Override
    public void initEvent() {

    }
}
