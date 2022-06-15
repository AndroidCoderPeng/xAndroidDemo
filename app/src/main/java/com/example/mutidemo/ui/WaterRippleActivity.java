package com.example.mutidemo.ui;

import android.util.Log;

import com.example.mutidemo.databinding.ActivityWaterRippleBinding;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WaterRippleActivity extends AndroidxBaseActivity<ActivityWaterRippleBinding> {

    private static final String TAG = "WaterRippleActivity";
    private boolean isRunning = true;
    private ExecutorService singleThreadExecutor;

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    public void initData() {
        //只有一个核心线程，当被占用时，其他的任务需要进入队列等待
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void initEvent() {
        viewBinding.waterRippleView.setOnAnimationStartListener(view -> {
            view.start();
            //开启线程搜索设备
            Log.d(TAG, "onStart: 开始线程");
            singleThreadExecutor.execute(searchRunnable);
            isRunning = true;
        });
        viewBinding.stopButton.setOnClickListener(v -> viewBinding.waterRippleView.stop());
    }

    private final Runnable searchRunnable = () -> {
        while (true) {
            try {
                if (!isRunning) {
                    Log.d(TAG, "run: 设备搜索线程休眠中...");
                    Thread.sleep(Long.MAX_VALUE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "run: 设备搜索线程运行中...");
            try {
                //搜索设备
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
        viewBinding.waterRippleView.stop();
    }
}
