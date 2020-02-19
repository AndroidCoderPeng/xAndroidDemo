package com.example.mutidemo;

import android.app.Application;

import com.pengxh.app.multilib.widget.EasyToast;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EasyToast.init(this);
    }
}