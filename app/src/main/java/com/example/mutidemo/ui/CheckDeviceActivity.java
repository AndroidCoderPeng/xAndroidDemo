package com.example.mutidemo.ui;

import android.view.View;
import android.widget.Button;

import com.example.mutidemo.R;
import com.example.mutidemo.widget.CheckView;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import butterknife.BindView;

public class CheckDeviceActivity extends BaseNormalActivity {

    @BindView(R.id.checkView)
    CheckView checkView;
    @BindView(R.id.stopButton)
    Button stopButton;

    @Override
    public int initLayoutView() {
        return R.layout.activity_check;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        checkView.setOnAnimationStartListener(new CheckView.OnAnimationStartListener() {
            @Override
            public void onStart(CheckView view) {
                view.start();

            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkView.stop();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkView.stop();
    }
}
