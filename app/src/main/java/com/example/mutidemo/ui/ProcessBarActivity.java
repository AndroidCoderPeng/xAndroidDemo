package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.example.mutidemo.R;
import com.example.mutidemo.widget.EasyProgressBar;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import butterknife.BindView;

public class ProcessBarActivity extends BaseNormalActivity {

    @BindView(R.id.mEasyProgressBar)
    EasyProgressBar mEasyProgressBar;

    @Override
    public int initLayoutView() {
        return R.layout.activity_progress;
    }

    @Override
    public void initData() {
        mEasyProgressBar.setMaxProgress(100);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= mEasyProgressBar.getMaxProgress(); i++) {
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
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                mEasyProgressBar.setCurrentProgress(msg.arg1);
            }
        }
    };

    @Override
    public void initEvent() {

    }
}
