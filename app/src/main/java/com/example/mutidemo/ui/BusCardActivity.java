package com.example.mutidemo.ui;

import android.view.View;

import com.example.mutidemo.base.AndroidxBaseActivity;
import com.example.mutidemo.databinding.ActivityBusCardBinding;
import com.example.mutidemo.widget.BusCardView;
import com.pengxh.app.multilib.widget.EasyToast;

public class BusCardActivity extends AndroidxBaseActivity<ActivityBusCardBinding> {

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        viewBinding.busCardView.setTagText("已出站已出站");
        viewBinding.busCardView.setOnTagClickListener(new BusCardView.OnTagClickListener() {
            @Override
            public void onClick(View view) {
                EasyToast.showToast("点击了Tag ===> "+viewBinding.busCardView.getTagText(), EasyToast.SUCCESS);
            }
        });
    }
}
