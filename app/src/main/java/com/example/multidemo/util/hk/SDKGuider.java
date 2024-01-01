package com.example.multidemo.util.hk;

import android.util.Log;

import com.hikvision.netsdk.HCNetSDK;

import hcnetsdk.jna.HCNetSDKJNAInstance;

public class SDKGuider {
    private static final String TAG = "SDKGuider";
    static public SDKGuider sdkGuider = new SDKGuider();
    //设备管理接口
    public DevManageGuider devManageGuider = new DevManageGuider();
    //预览相关接口
    public DevPreviewGuider devPreviewGuider = new DevPreviewGuider();

    //SDK初始化
    public SDKGuider() {
        initNetSdk_jna();
    }

    //清理
    protected void finalize() {
        cleanupNetSdk_jna();
    }

    public int GetLastError_jni() {
        return MessageCodeHub.INSTANCE.getErrorCode();
    }

    private void initNetSdk_jni() {
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.e(TAG, "HCNetSDK init is failed!");
            return;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/", true);
    }

    private void initNetSdk_jna() {
        if (!HCNetSDKJNAInstance.getInstance().NET_DVR_Init()) {
            Log.e(TAG, "HCNetSDK init is failed!");
            return;
        }
        HCNetSDKJNAInstance.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/", true);
    }

    private void cleanupNetSdk_jni() {
        if (!HCNetSDK.getInstance().NET_DVR_Cleanup()) {
            Log.e(TAG, "HCNetSDK cleanup is failed!");
        }
    }

    private void cleanupNetSdk_jna() {
        if (!HCNetSDKJNAInstance.getInstance().NET_DVR_Cleanup()) {
            Log.e(TAG, "HCNetSDK cleanup is failed!");
        }
    }
}
