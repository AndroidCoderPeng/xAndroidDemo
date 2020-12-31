package com.example.mutidemo;

import android.os.Handler;
import android.view.View;

import com.pengxh.app.multilib.base.BaseNormalActivity;

public class SwitchModeActivity extends BaseNormalActivity {

    @Override
    public int initLayoutView() {
        return R.layout.activity_switch;
    }

    @Override
    public void initData() {
        //设置两秒后执行当前activity的销毁操作
        new Handler().postDelayed(() -> back(null), 1000);
    }

    @Override
    public void initEvent() {

    }

    public void back(View v) {
        this.finish();
        try {
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        back(null);
    }
}
