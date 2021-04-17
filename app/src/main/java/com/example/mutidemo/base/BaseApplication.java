package com.example.mutidemo.base;

import android.app.Application;
import android.util.Log;

import com.igexin.sdk.IUserLoggerInterface;
import com.pengxh.app.multilib.widget.EasyToast;

public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        EasyToast.init(this);
        //个推初始化
        com.igexin.sdk.PushManager.getInstance().initialize(this);
        com.igexin.sdk.PushManager.getInstance().setDebugLogger(this, new IUserLoggerInterface() {
            @Override
            public void log(String s) {
                Log.d(TAG, s);
            }
        });
    }
}