package com.example.mutidemo.ui;

import com.example.mutidemo.R;
import com.example.mutidemo.view.LineChartView;
import com.pengxh.app.multilib.base.NormalActivity;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/12/13.
 */

public class LineChartActivity extends NormalActivity {

    @BindView(R.id.mLineChartView)
    LineChartView mLineChartView;

    @Override
    public void initView() {
        setContentView(R.layout.layout_linechart);
    }

    @Override
    public void init() {

    }

    @Override
    public void initEvent() {

    }
}
