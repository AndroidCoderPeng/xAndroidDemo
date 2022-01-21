package com.example.mutidemo.ui;

import com.example.mutidemo.base.AndroidxBaseActivity;
import com.example.mutidemo.databinding.ActivityBusCardBinding;

public class BusCardActivity extends AndroidxBaseActivity<ActivityBusCardBinding> {

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        viewBinding.busCardView.setTagText("已出站已出站");
    }
}
