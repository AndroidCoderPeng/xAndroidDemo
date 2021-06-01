package com.example.mutidemo.base;

import android.app.Application;
import android.util.Log;

import com.example.mutidemo.util.FileUtils;
import com.igexin.sdk.IUserLoggerInterface;
import com.pengxh.app.multilib.widget.EasyToast;

public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";
    private volatile static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        EasyToast.init(this);
        //个推初始化
        com.igexin.sdk.PushManager.getInstance().initialize(this);
        com.igexin.sdk.PushManager.getInstance().setDebugLogger(this, new IUserLoggerInterface() {
            @Override
            public void log(String s) {
                Log.d(TAG, s);
            }
        });
        FileUtils.initFileConfig(this);
    }

    /**
     * 双重锁单例
     */
    public static BaseApplication getInstance() {
        if (instance == null) {
            synchronized (BaseApplication.class) {
                if (instance == null) {
                    instance = new BaseApplication();
                }
            }
        }
        return instance;
    }
}