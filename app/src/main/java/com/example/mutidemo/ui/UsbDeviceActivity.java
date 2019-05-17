package com.example.mutidemo.ui;

import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.mutidemo.R;
import com.example.mutidemo.event.ZBEATEvent;
import com.example.mutidemo.widget.EditTextWithDelete;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

public class UsbDeviceActivity extends BaseNormalActivity {

    @BindView(R.id.mTvUsbReceive)
    TextView mTvUsbReceive;
    @BindView(R.id.mEtSend)
    EditTextWithDelete mEtSend;
    @BindView(R.id.mBtnSend)
    Button mBtnSend;

    @Override
    public void initView() {
        setContentView(R.layout.activity_usb_device);
    }

    @Override
    public void init() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void initEvent() {
        mBtnSend.setOnClickListener(v -> {
            String cmdString = mEtSend.getText().toString();
            if (!TextUtils.isEmpty(cmdString) && cmdString.startsWith("AT")) {
                if (TextUtils.isEmpty(mTvUsbReceive.getText().toString())) {
                    EventBus.getDefault().postSticky(new ZBEATEvent(cmdString, ""));
                } else {
                    mTvUsbReceive.setText("");
                }
            } else {
                ToastUtil.showBeautifulToast("指令错误", 5);
            }
        });
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ZBEATEvent event) {
        String response = event.getResponse();
        if (!response.isEmpty()) {
            mTvUsbReceive.setText(response);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}