package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.mutidemo.databinding.ActivityBluetoothBinding;
import com.example.mutidemo.util.DemoConstant;
import com.example.mutidemo.util.OtherUtils;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.utils.BroadcastManager;
import com.pengxh.androidx.lite.utils.Constant;
import com.pengxh.androidx.lite.utils.WeakReferenceHandler;
import com.pengxh.androidx.lite.utils.ble.BLEManager;
import com.pengxh.androidx.lite.utils.ble.BlueToothBean;
import com.pengxh.androidx.lite.utils.ble.OnBleConnectListener;
import com.pengxh.androidx.lite.utils.ble.OnDeviceDiscoveredListener;
import com.pengxh.androidx.lite.widget.EasyToast;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressLint("SetTextI18n")
public class BluetoothActivity extends AndroidxBaseActivity<ActivityBluetoothBinding> {

    private static final String TAG = "BluetoothActivity";
    private static WeakReferenceHandler weakReferenceHandler;
    private final List<BlueToothBean> blueToothBeans = new ArrayList<>();
    private BroadcastManager broadcastManager;
    private BLEManager bleManager;
    private boolean isConnected = false;
    private StringBuilder builder;

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    public void initData() {
        broadcastManager = BroadcastManager.getInstance(this);
        broadcastManager.addAction(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(Objects.requireNonNull(intent.getAction()))) {
                    switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
                        case BluetoothAdapter.STATE_ON:
                            weakReferenceHandler.sendEmptyMessage(Constant.BLUETOOTH_ON);
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            weakReferenceHandler.sendEmptyMessage(Constant.BLUETOOTH_OFF);
                            break;
                    }
                }
            }
        }, Constant.BLUETOOTH_STATE_CHANGED);

        bleManager = BLEManager.getInstance();
        weakReferenceHandler = new WeakReferenceHandler(callback);
        builder = new StringBuilder();
        if (bleManager.initBLE(this)) {
            if (bleManager.isBluetoothEnable()) {
                viewBinding.bluetoothStateView.setText("蓝牙状态: ON");
            } else {
                viewBinding.bluetoothStateView.setText("蓝牙状态: OFF");
                bleManager.openBluetooth(true);
            }
        } else {
            EasyToast.show(this, "该设备不支持低功耗蓝牙");
        }
    }

    @Override
    public void initEvent() {
        viewBinding.disconnectButton.setChangeAlphaWhenPress(true);
        viewBinding.searchButton.setChangeAlphaWhenPress(true);
        viewBinding.disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected) {
                    //断开连接
                    bleManager.disConnectDevice();
                    EasyToast.show(BluetoothActivity.this, "设备已断开连接");
                } else {
                    EasyToast.show(BluetoothActivity.this, "设备未连接，无需断开");
                }
            }
        });
        viewBinding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //搜索蓝牙
                if (bleManager.isDiscovery()) {//当前正在搜索设备...
                    bleManager.stopDiscoverDevice();
                }
                OtherUtils.showLoadingDialog(BluetoothActivity.this, "设备搜索中...");
                bleManager.startDiscoverDevice(new OnDeviceDiscoveredListener() {
                    @Override
                    public void onDeviceFound(BlueToothBean blueToothBean) {
                        Message message = weakReferenceHandler.obtainMessage();
                        message.what = Constant.DISCOVERY_DEVICE;
                        message.obj = blueToothBean;
                        weakReferenceHandler.sendMessage(message);
                    }

                    @Override
                    public void onDiscoveryTimeout() {
                        Message message = weakReferenceHandler.obtainMessage();
                        message.what = Constant.DISCOVERY_OUT_TIME;
                        weakReferenceHandler.sendMessage(message);
                    }
                }, 15 * 1000);
            }
        });
    }

    private void startConnectDevice(BluetoothDevice device) {
        // 当前蓝牙设备
        if (!isConnected) {
            OtherUtils.showLoadingDialog(this, "正在连接...");
            bleManager.connectBleDevice(device, 10000,
                    DemoConstant.SERVICE_UUID,
                    DemoConstant.READ_CHARACTERISTIC_UUID,
                    DemoConstant.WRITE_CHARACTERISTIC_UUID,
                    onBleConnectListener);
        }
    }

    private final OnBleConnectListener onBleConnectListener = new OnBleConnectListener() {
        @Override
        public void onConnecting(BluetoothGatt bluetoothGatt) {

        }

        @Override
        public void onConnectSuccess(BluetoothGatt bluetoothGatt, int status) {

        }

        @Override
        public void onConnectFailure(BluetoothGatt bluetoothGatt, String exception, int status) {
            Message message = weakReferenceHandler.obtainMessage();
            message.what = Constant.CONNECT_FAILURE;
            weakReferenceHandler.sendMessage(message);
        }

        @Override
        public void onDisConnecting(BluetoothGatt bluetoothGatt) {

        }

        @Override
        public void onDisConnectSuccess(BluetoothGatt bluetoothGatt, int status) {
            Message message = weakReferenceHandler.obtainMessage();
            message.what = Constant.DISCONNECT_SUCCESS;
            message.obj = status;
            weakReferenceHandler.sendMessage(message);
        }

        @Override
        public void onServiceDiscoverySucceed(BluetoothGatt bluetoothGatt, int status) {
            Message message = weakReferenceHandler.obtainMessage();
            message.what = Constant.CONNECT_SUCCESS;
            weakReferenceHandler.sendMessage(message);
        }

        @Override
        public void onServiceDiscoveryFailed(BluetoothGatt bluetoothGatt, String msg) {
            Message message = weakReferenceHandler.obtainMessage();
            message.what = Constant.CONNECT_FAILURE;
            weakReferenceHandler.sendMessage(message);
        }

        @Override
        public void onReceiveMessage(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic characteristic) {
            Message message = weakReferenceHandler.obtainMessage();
            message.what = Constant.RECEIVE_SUCCESS;
            message.obj = characteristic.getValue();
            weakReferenceHandler.sendMessage(message);
        }

        @Override
        public void onReceiveError(String errorMsg) {
            Message message = weakReferenceHandler.obtainMessage();
            message.what = Constant.RECEIVE_FAILURE;
            weakReferenceHandler.sendMessage(message);
        }

        @Override
        public void onWriteSuccess(BluetoothGatt bluetoothGatt, byte[] msg) {
            Message message = weakReferenceHandler.obtainMessage();
            message.what = Constant.SEND_SUCCESS;
            message.obj = msg;
            weakReferenceHandler.sendMessage(message);
        }

        @Override
        public void onWriteFailure(BluetoothGatt bluetoothGatt, byte[] msg, String errorMsg) {
            Message message = weakReferenceHandler.obtainMessage();
            message.what = Constant.SEND_FAILURE;
            message.obj = msg;
            weakReferenceHandler.sendMessage(message);
        }

        @Override
        public void onReadRssi(BluetoothGatt bluetoothGatt, int rssi, int status) {

        }
    };

    private final Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case Constant.BLUETOOTH_ON:
                    EasyToast.show(BluetoothActivity.this, "蓝牙已开启");
                    viewBinding.bluetoothStateView.setText("蓝牙状态: ON");
                    break;
                case Constant.BLUETOOTH_OFF:
                    EasyToast.show(BluetoothActivity.this, "蓝牙已关闭");
                    viewBinding.bluetoothStateView.setText("蓝牙状态: ON");
                    break;
                case Constant.DISCOVERY_DEVICE:
                    BlueToothBean bean = (BlueToothBean) msg.obj;
                    if (blueToothBeans.size() == 0) {
                        blueToothBeans.add(bean);
                    } else {
                        //0表示未添加到list的新设备，1表示已经扫描并添加到list的设备
                        int judge = 0;
                        for (BlueToothBean it : blueToothBeans) {
                            if (it.getBluetoothDevice().getAddress().equals(bean.getBluetoothDevice().getAddress())) {
                                judge = 1;
                                break;
                            }
                        }
                        if (judge == 0) {
                            blueToothBeans.add(bean);
                        }
                    }
                    break;
                case Constant.DISCOVERY_OUT_TIME:
                    OtherUtils.dismissLoadingDialog();
                    QMUIBottomSheet.BottomListSheetBuilder sheetBuilder = new QMUIBottomSheet.BottomListSheetBuilder(BluetoothActivity.this);
                    for (BlueToothBean it : blueToothBeans) {
                        sheetBuilder.addItem(it.getBluetoothDevice().getName());
                    }
                    sheetBuilder.setGravityCenter(true).setAddCancelBtn(true).setOnSheetItemClickListener((dialog, itemView, position, tag) -> {
                        dialog.dismiss();
                        //连接点击的设备
                        startConnectDevice(blueToothBeans.get(position).getBluetoothDevice());
                    }).build().show();
                    break;
                case Constant.CONNECT_SUCCESS:
                    OtherUtils.dismissLoadingDialog();
                    isConnected = true;
                    bleManager.sendCommand(DemoConstant.ASK_DEV_CODE_COMMAND);
                    break;
                case Constant.CONNECT_FAILURE:
                    isConnected = false;
                    Log.d(TAG, "handleMessage: curConnectState" + false);
                    break;
                case Constant.DISCONNECT_SUCCESS:
                    isConnected = false;
                    break;
                case Constant.SEND_SUCCESS:
                    byte[] sendSuccess = (byte[]) msg.obj;
                    Log.d(TAG, "发送成功->sendSuccessBuffer: " + Arrays.toString(sendSuccess));
                    break;
                case Constant.SEND_FAILURE:
                    byte[] sendFail = (byte[]) msg.obj;
                    Log.d(TAG, "发送失败->sendFailBuffer: " + Arrays.toString(sendFail));
                    break;
                case Constant.RECEIVE_SUCCESS:
                    //TODO 接收成功
                    byte[] receiveByteArray = (byte[]) msg.obj;
                    //根据返回值标头判断是设备编号还是数据值
                    byte firstByte = receiveByteArray[0];
                    if (firstByte == (byte) 0xAA) {
                        //解析测量数据
                        if (receiveByteArray.length == 14) {
                            builder
                                    .append("设备返回值: ")
                                    .append(Arrays.toString(receiveByteArray))
                                    .append("\r\n");
                            viewBinding.deviceValueView.setText(builder.toString());
                        } else {
                            Log.d(TAG, "设备返回值长度异常，无法解析");
                        }
                    } else if (firstByte == (byte) 51) {
                        //解析deviceCode
                        //51, 51, 50, 48, 50, 49, 48, 49, 48, 48, 48, 51, 13, 10, -86, 0, 0, 0, 0, 0
                        if (receiveByteArray.length >= 12) {
                            viewBinding.deviceCodeView.setText("设备编号: " + toDeviceCode(receiveByteArray));
                            bleManager.sendCommand(DemoConstant.OPEN_TRANSFER_COMMAND);
                        } else {
                            Log.d(TAG, "设备返回值长度异常，无法解析");
                        }
                    } else {
                        Log.d(TAG, "未知返回值，无法解析");
                    }
                    break;
                case Constant.RECEIVE_FAILURE:
                    String receiveString = (String) msg.obj;
                    Log.d(TAG, "接收失败->receiveString: " + receiveString);
                    break;
            }
            return true;
        }
    };

    private String toDeviceCode(byte[] byteArray) {
        /**
         * 51, 51, 50, 48, 50, 49, 48, 49, 48, 48, 48, 51, 13, 10, -86, 0, 0, 0, 0, 0
         *
         * 51是数据标头
         * 13,10是数据结束位
         * 后面的是其他参数，不解析
         * */
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            builder.append((char) ((int) byteArray[i]));
        }
        return builder.toString();
    }

    @Override
    protected void onDestroy() {
        broadcastManager.destroy(Constant.BLUETOOTH_STATE_CHANGED);
        super.onDestroy();
    }
}
