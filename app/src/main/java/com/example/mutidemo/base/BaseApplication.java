package com.example.mutidemo.base;

import android.app.Application;
import android.util.Log;

import com.pengxh.app.multilib.widget.EasyToast;

public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: 应用启动");
        EasyToast.init(this);
    }
}