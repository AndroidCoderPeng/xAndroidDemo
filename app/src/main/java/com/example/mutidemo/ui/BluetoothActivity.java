package com.example.mutidemo.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.aihook.alertview.library.AlertView;
import com.aihook.alertview.library.OnItemClickListener;
import com.example.mutidemo.R;
import com.example.mutidemo.bean.BlueToothBean;
import com.example.mutidemo.util.Constant;
import com.example.mutidemo.util.OtherUtils;
import com.google.gson.Gson;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.BroadcastManager;
import com.pengxh.app.multilib.widget.EasyToast;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

public class BluetoothActivity extends BaseNormalActivity {

    private static final String TAG = "BluetoothActivity";

    @BindView(R.id.startSearch)
    QMUIRoundButton startSearch;

    private static WeakReferenceHandler weakReferenceHandler;
    private boolean isBluetoothOn = true;
    private List<BlueToothBean> blueToothBeans = new ArrayList<>();

    @Override
    public int initLayoutView() {
        return R.layout.activity_bluetooth;
    }

    @Override
    public void initData() {
        weakReferenceHandler = new WeakReferenceHandler(this);
        BroadcastManager.getInstance(this).addAction(new String[]{BluetoothDevice.ACTION_FOUND, BluetoothAdapter.ACTION_DISCOVERY_FINISHED}, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (Objects.requireNonNull(action)) {
                    case BluetoothDevice.ACTION_FOUND:
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        BlueToothBean bean;
                        if (device.getName() == null || device.getName().equals("")) {
                            bean = new BlueToothBean("设备名未知", device.getAddress());
                        } else {
                            bean = new BlueToothBean(device.getName(), device.getAddress());
                        }
                        if (blueToothBeans.size() == 0) {
                            blueToothBeans.add(bean);
                        } else {
                            int judge = 0;//0表示未添加到list的新设备，1表示已经扫描并添加到list的设备
                            for (int i = 0; i < blueToothBeans.size(); i++) {
                                String address = blueToothBeans.get(i).getBlueToothAddress();
                                //如果相同设备已经添加则不添加
                                if (address.equals(bean.getBlueToothAddress())) {
                                    judge = 1;
                                    break;
                                }
                            }
                            if (judge == 0) {
                                blueToothBeans.add(bean);
                            }
                        }
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        OtherUtils.dismissLoadingDialog();
                        startSearch.setEnabled(true);
                        Log.d(TAG, new Gson().toJson(blueToothBeans));
                        String[] nameArray = new String[blueToothBeans.size()];
                        for (int i = 0; i < blueToothBeans.size(); i++) {
                            nameArray[i] = blueToothBeans.get(i).getBlueToothName();
                        }
                        new AlertView("标题", null, "取消", null, nameArray, BluetoothActivity.this, AlertView.Style.ActionSheet, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                Log.d(TAG, "onItemClick: " + blueToothBeans.get(position).getBlueToothAddress());
                            }
                        }).show();
                        break;
                }
            }
        });
    }

    @Override
    public void initEvent() {
        startSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isBluetoothOn) {
                    EasyToast.showToast("手机蓝牙未打开，请打开后再扫描设备", EasyToast.ERROR);
                    return;
                }
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter.isDiscovering()) {
                    //搜索中不让再次搜索
                    startSearch.setEnabled(false);
                } else {
                    OtherUtils.showLoadingDialog(BluetoothActivity.this, "设备搜索中...");
                    bluetoothAdapter.startDiscovery();
                }
            }
        });
    }

    public static void sendEmptyMessage(int what) {
        weakReferenceHandler.sendEmptyMessage(what);
    }

    private static class WeakReferenceHandler extends Handler {

        private WeakReference<BluetoothActivity> reference;

        private WeakReferenceHandler(BluetoothActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            BluetoothActivity activity = reference.get();
            switch (msg.what) {
                case Constant.BLUETOOTH_ON:
                    EasyToast.showToast("蓝牙已开启", EasyToast.SUCCESS);
                    activity.isBluetoothOn = true;
                    break;
                case Constant.BLUETOOTH_OFF:
                    EasyToast.showToast("蓝牙已关闭", EasyToast.ERROR);
                    activity.isBluetoothOn = false;
                    break;
                case Constant.DEVICE_CONNECTED:
                    //TODO 设备已连接
                    break;
                case Constant.DEVICE_DISCONNECTED:
                    //TODO 设备断开连接
                    break;
            }
        }
    }
}
