package com.example.mutidemo.ui;

import android.graphics.Color;
import android.widget.Toast;

import com.example.mutidemo.R;
import com.example.mutidemo.widget.CustomProgressBar;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/11/26.
 */

public class CustomProgressBarActivity extends BaseNormalActivity {

    @BindView(R.id.cpb_progresbar)
    CustomProgressBar cpbProgresbar;

    @Override
    public void initView() {
        setContentView(R.layout.activity_progress);
    }

    @Override
    public void init() {

    }

    @Override
    public void initEvent() {
        cpbProgresbar.setOnFinishedListener(new CustomProgressBar.OnFinishedListener() {
            @Override
            public void onFinish() {
                Toast.makeText(CustomProgressBarActivity.this, "加载完成!", Toast.LENGTH_SHORT).show();
            }
        });
        cpbProgresbar.setProgressDesc("剩余");
        cpbProgresbar.setMaxProgress(100);
        cpbProgresbar.setProgressColor(Color.parseColor("#0094ff"));
        cpbProgresbar.setCurProgress(100);
    }
}
