package com.example.mutidemo.ui;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.mutidemo.databinding.ActivityProgressBinding;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.utils.WeakReferenceHandler;

public class ProcessBarActivity extends AndroidxBaseActivity<ActivityProgressBinding> {

    private WeakReferenceHandler weakReferenceHandler;

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    public void initData() {
        weakReferenceHandler = new WeakReferenceHandler(callback);
        viewBinding.mEasyProgressBar.setMaxProgress(100);
        new Thread(() -> {
            try {
                for (int i = 0; i <= viewBinding.mEasyProgressBar.getMaxProgress(); i++) {
                    Message msg = weakReferenceHandler.obtainMessage();
                    msg.arg1 = i;
                    msg.what = 1;
                    weakReferenceHandler.sendMessage(msg);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private final Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                try {
                    viewBinding.mEasyProgressBar.setCurrentProgress(msg.arg1);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
    };

    @Override
    public void initEvent() {

    }
}
