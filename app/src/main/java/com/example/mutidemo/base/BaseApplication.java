package com.example.mutidemo.base;

import android.app.Application;
import android.util.Log;

import com.example.mutidemo.util.Constant;
import com.pengxh.app.multilib.widget.EasyToast;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: 应用启动");
        EasyToast.init(this);
        initSDK();
    }

    /**
     * // 参数一：当前上下文context；
     * // 参数二：应用申请的Appkey（需替换）；
     * // 参数三：渠道名称；
     * // 参数四：设备类型，必须参数，传参数为UMConfigure.DEVICE_TYPE_PHONE则表示手机；传参数为UMConfigure.DEVICE_TYPE_BOX则表示盒子；默认为手机；
     * // 参数五：Push推送业务的secret 填充Umeng Message Secret对应信息（需替换）
     */
    private void initSDK() {
        UMConfigure.init(this, Constant.APP_KEY, "Umeng", UMConfigure.DEVICE_TYPE_PHONE, Constant.UMENG_MESSAGE_SECRET);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setResourcePackageName("com.example.mutidemo");
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String s) {
                //注册成功会返回device token
                Log.d(TAG, "注册成功：deviceToken：-------->  " + s);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.d(TAG, "注册失败：-------->  " + s + "," + s1);
            }
        });
    }
}