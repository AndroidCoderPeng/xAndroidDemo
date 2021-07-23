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
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(Objects.requireNonNull(intent.getAction()))) {
            switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
                case BluetoothAdapter.STATE_OFF:
                    BluetoothActivity.sendEmptyMessage(Constant.BLUETOOTH_OFF);
                    break;
                case BluetoothAdapter.STATE_ON:
                    BluetoothActivity.sendEmptyMessage(Constant.BLUETOOTH_ON);
                    break;
            }
        }
    }
}
