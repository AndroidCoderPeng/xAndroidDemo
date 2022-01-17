package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.example.mutidemo.base.AndroidxBaseActivity;
import com.example.mutidemo.databinding.ActivityProgressBinding;

public class ProcessBarActivity extends AndroidxBaseActivity<ActivityProgressBinding> {

    @Override
    public void initData() {
        viewBinding.mEasyProgressBar.setMaxProgress(100);
        new Thread(() -> {
            for (int i = 0; i <= viewBinding.mEasyProgressBar.getMaxProgress(); i++) {
                Message msg = new Message();
                msg.arg1 = i;
                msg.what = 1;
                handler.sendMessage(msg);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                viewBinding.mEasyProgressBar.setCurrentProgress(msg.arg1);
            }
        }
    };

    @Override
    public void initEvent() {

    }
}
