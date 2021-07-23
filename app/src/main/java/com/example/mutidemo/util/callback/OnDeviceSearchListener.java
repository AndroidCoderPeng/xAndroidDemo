package com.example.mutidemo.util.callback;

import com.example.mutidemo.bean.BlueToothBean;

public interface OnDeviceSearchListener {
    void onDeviceFound(BlueToothBean blueToothBean); //搜索到设备

    void onDiscoveryOutTime(); //扫描超时
}
