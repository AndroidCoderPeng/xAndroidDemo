package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mutidemo.R;
import com.pengxh.app.multilib.base.NormalActivity;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/11/25.
 * <p>
 * 计时器还可以用CountDownTimer
 */

public class TimerActivity extends NormalActivity {

    @BindView(R.id.mTv_timer_show)
    TextView mTvTimerShow;
    @BindView(R.id.mBtn_timer_start)
    Button mBtnTimerStart;

    private Timer mTimer;
    private TimerTask mTimerTask;

    @Override
    public void initView() {
        setContentView(R.layout.activity_timer);
    }

    @Override
    public void init() {

    }

    @Override
    public void initEvent() {
        mBtnTimerStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimer = new Timer();
                mTimerTask = new TimerTask() {
                    int i = 10;

                    @Override
                    public void run() {
                        Message msg = Message.obtain();
                        msg.what = i;
                        i--;
                        mHandler.sendMessage(msg);
                    }
                };
                mTimer.schedule(mTimerTask, 500, 500);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what > 0) {
                mTvTimerShow.setText("" + msg.what);
            } else {
                //在Handler里面更新UI
                mTvTimerShow.setText("倒计时结束");
                mTimer.cancel();
            }
        }
    };
}
