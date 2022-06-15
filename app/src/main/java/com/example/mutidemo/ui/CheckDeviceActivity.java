package com.example.mutidemo.ui;

import com.example.mutidemo.databinding.ActivityCheckBinding;
import com.example.mutidemo.widget.CheckView;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;

public class CheckDeviceActivity extends AndroidxBaseActivity<ActivityCheckBinding> {

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        viewBinding.checkView.setOnAnimationStartListener(CheckView::start);
        viewBinding.stopButton.setOnClickListener(v -> viewBinding.checkView.stop());
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewBinding.checkView.stop();
    }
}
