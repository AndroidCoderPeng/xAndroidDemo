package com.example.mutidemo.ui;

import com.example.mutidemo.R;
import com.example.mutidemo.widget.WaterRippleView;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import butterknife.BindView;

public class WaterRippleActivity extends BaseNormalActivity {

    @BindView(R.id.waterRippleView)
    WaterRippleView waterRippleView;

    @Override
    public int initLayoutView() {
        return R.layout.activity_water_ripple;
    }

    @Override
    public void initData() {
        waterRippleView.setOnAnimationStartListener(new WaterRippleView.OnAnimationStartListener() {
            @Override
            public void onStart(WaterRippleView view) {
                view.start();
            }
        });
    }

    @Override
    public void initEvent() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        waterRippleView.stop();
    }
}
