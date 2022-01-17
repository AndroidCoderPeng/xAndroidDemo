package com.example.mutidemo.ui;

import com.example.mutidemo.base.AndroidxBaseActivity;
import com.example.mutidemo.databinding.ActivityCheckBinding;
import com.example.mutidemo.widget.CheckView;

public class CheckDeviceActivity extends AndroidxBaseActivity<ActivityCheckBinding> {

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
