package com.example.mutidemo;

import android.app.Application;

import com.pengxh.app.multilib.utils.ToastUtil;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtil.init(this);
    }
}