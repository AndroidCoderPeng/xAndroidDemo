package com.example.mutidemo.base;

import android.app.Application;
import android.util.Log;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.example.mutidemo.R;
import com.example.mutidemo.util.DemoConstant;
import com.example.mutidemo.util.FileUtils;
import com.igexin.sdk.IUserLoggerInterface;

import cn.bmob.v3.Bmob;

public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";
    private volatile static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ArcGISRuntimeEnvironment.setApiKey(getString(R.string.arcgis_key));
        //个推初始化
        com.igexin.sdk.PushManager.getInstance().initialize(this);
        com.igexin.sdk.PushManager.getInstance().setDebugLogger(this, new IUserLoggerInterface() {
            @Override
            public void log(String s) {
                Log.d(TAG, s);
            }
        });
        Bmob.initialize(this, DemoConstant.BMOB_APP_KEY);
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