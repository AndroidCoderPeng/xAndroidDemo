package com.example.mutidemo.util.callback;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public interface OnBleConnectListener {
    void onConnecting(BluetoothGatt bluetoothGatt); //正在连接

    void onConnectSuccess(BluetoothGatt bluetoothGatt, int status); //连接成功

    void onConnectFailure(BluetoothGatt bluetoothGatt, String exception, int status); //连接失败

    void onDisConnecting(BluetoothGatt bluetoothGatt); //正在断开

    void onDisConnectSuccess(BluetoothGatt bluetoothGatt, int status); // 断开连接

    void onServiceDiscoverySucceed(BluetoothGatt bluetoothGatt, int status); //发现服务成功

    void onServiceDiscoveryFailed(BluetoothGatt bluetoothGatt, String msg); //发现服务失败

    void onReceiveMessage(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic characteristic); //收到消息

    void onReceiveError(String errorMsg); //接收数据出错

    void onWriteSuccess(BluetoothGatt bluetoothGatt, byte[] msg); //写入成功

    void onWriteFailure(BluetoothGatt bluetoothGatt, byte[] msg, String errorMsg); //写入失败

    void onReadRssi(BluetoothGatt bluetoothGatt, int rssi, int status); //成功读取到连接信号强度
}
