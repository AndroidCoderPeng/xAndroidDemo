package com.example.android.util.hk

import com.hikvision.netsdk.HCNetSDK
import com.hikvision.netsdk.INT_PTR

object MessageCodeHub {
    private val sdk by lazy { HCNetSDK.getInstance() }

    fun getErrorCode(): Int = sdk.NET_DVR_GetLastError()

    fun convertErrorCode(code: Int): String {
        val intPtr = INT_PTR()
        intPtr.iValue = code
        return sdk.NET_DVR_GetErrorMsg(intPtr)
    }
}