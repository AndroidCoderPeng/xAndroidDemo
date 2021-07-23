package com.example.mutidemo.bean;

import android.bluetooth.BluetoothDevice;

public class BlueToothBean {
    private BluetoothDevice bluetoothDevice;  //蓝牙设备
    private int rssi;  //蓝牙信号

    public BlueToothBean(BluetoothDevice bluetoothDevice, int rssi) {
        this.bluetoothDevice = bluetoothDevice;
        this.rssi = rssi;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
