package com.example.mutidemo.ui;

import android.view.View;

import com.example.mutidemo.databinding.ActivityBusCardBinding;
import com.example.mutidemo.widget.BusCardView;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.widget.EasyToast;


public class BusCardActivity extends AndroidxBaseActivity<ActivityBusCardBinding> {

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        viewBinding.busCardView.setTagText("已出站");
        viewBinding.busCardView.setOnTagClickListener(new BusCardView.OnTagClickListener() {
            @Override
            public void onClick(View view) {
                EasyToast.show(BusCardActivity.this, "点击了Tag ===> " + viewBinding.busCardView.getTagText());
            }
        });
    }
}
