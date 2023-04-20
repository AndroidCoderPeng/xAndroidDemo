package com.example.mutidemo.util.hk;

import android.util.Log;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_XML_CONFIG_INPUT;
import com.hikvision.netsdk.NET_DVR_XML_CONFIG_OUTPUT;

import hcnetsdk.jna.HCNetSDKByJNA;
import hcnetsdk.jna.HCNetSDKJNAInstance;

public class DevTransportGuider {

    public boolean STDXMLConfig_jni(int lUserID, NET_DVR_XML_CONFIG_INPUT lpInputParam, NET_DVR_XML_CONFIG_OUTPUT lpOutputParam) {
        if (lUserID < 0) {
            Log.e("Casic", "STDXMLConfig_jni failed with error param");
            return false;
        }
        return HCNetSDK.getInstance().NET_DVR_STDXMLConfig(lUserID, lpInputParam, lpOutputParam);
    }

    public boolean STDXMLConfig_jna(int lUserID, HCNetSDKByJNA.NET_DVR_XML_CONFIG_INPUT lpInputParam, HCNetSDKByJNA.NET_DVR_XML_CONFIG_OUTPUT lpOutputParam) {
        if (lUserID < 0) {
            Log.e("Casic", "STDXMLConfig_jna failed with error param");
            return false;
        }
        return HCNetSDKJNAInstance.getInstance().NET_DVR_STDXMLConfig(lUserID, lpInputParam, lpOutputParam);
    }
}
