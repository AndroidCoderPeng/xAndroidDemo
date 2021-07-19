package com.example.mutidemo.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mutidemo.ui.BluetoothActivity;

import java.util.Objects;

public class BluetoothStateBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (Objects.requireNonNull(intent.getAction())) {
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)){
                    case BluetoothAdapter.STATE_OFF:
                        BluetoothActivity.sendEmptyMessage(Constant.BLUETOOTH_OFF);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        BluetoothActivity.sendEmptyMessage(Constant.BLUETOOTH_ON);
                        break;
                }
                break;
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                BluetoothActivity.sendEmptyMessage(Constant.DEVICE_CONNECTED);
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                BluetoothActivity.sendEmptyMessage(Constant.DEVICE_DISCONNECTED);
                break;
        }
    }
}
