package com.example.android.util.hk;

import android.util.Log;
import android.view.SurfaceHolder;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO_V20;
import com.sun.jna.Pointer;

import hcnetsdk.jna.HCNetSDKJNAInstance;

public class DevPreviewGuider {
    private static final String TAG = "DevPreviewGuider";
    
    //预览接口
    public int RealPlay_V40_jni(int iUserID, NET_DVR_PREVIEWINFO struPlayInfo, Pointer pUser) {
        if (iUserID < 0) {
            Log.e(TAG, "RealPlay_V40_jni failed with error param");
            return -1;
        }
        int iRet = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(iUserID, struPlayInfo, null);
        if (iRet < 0) {
            return -1;
        }

        boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_OpenSound((short) iRet);
        if (bRet) {
            Log.e(TAG, "NET_DVR_OpenSound Success!");
        }
        return iRet;
    }

    public int RealPlay_V40_jni(int iUserID, NET_DVR_PREVIEWINFO_V20 struPlayInfo, Pointer pUser) {
        if (iUserID < 0) {
            Log.e(TAG, "RealPlay_V40_jni failed with error param");
            return -1;
        }
        int iRet = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(iUserID, struPlayInfo, null);
        if (iRet < 0) {
            return -1;
        }
        return iRet;
    }

    //预览窗口句柄变化时调用
    public int RealPlaySurfaceChanged_jni(int iHandle, int nRegionNum, SurfaceHolder hHwnd) {
        if (iHandle < 0 || nRegionNum < 0) {
            Log.e(TAG, "RealPlaySurfaceChanged_jni failed with error param");
            return -1;
        }
        return HCNetSDK.getInstance().NET_DVR_RealPlaySurfaceChanged(iHandle, nRegionNum, hHwnd);
    }

    public boolean RealPlay_Stop_jni(int m_iPreviewHandle) {
        if (m_iPreviewHandle < 0) {
            Log.e(TAG, "RealPlay_Stop_jni failed with error param");
            return false;
        }
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPreviewHandle)) {
            Log.e(TAG, "RealPlay_Stop_jni failed");
            return false;
        }
        return true;
    }

    public boolean RealPlay_Snap(int m_iPreviewHandle, String sPicFileName) {
        if (!HCNetSDKJNAInstance.getInstance().NET_DVR_CapturePicture(m_iPreviewHandle, sPicFileName)) {
            Log.e(TAG, "RealPlay_Snap failed");
            return false;
        }
        return true;
    }

    public boolean RealPlay_Record(int m_iPreviewHandle, int dwTransType, String sPicFileName) {
        if (!HCNetSDKJNAInstance.getInstance().NET_DVR_SaveRealData_V30(m_iPreviewHandle, dwTransType, sPicFileName)) {
            Log.e(TAG, "RealPlay_Record failed");
            return false;
        }
        return true;
    }
}
