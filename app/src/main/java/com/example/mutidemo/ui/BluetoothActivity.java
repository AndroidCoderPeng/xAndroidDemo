package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.mutidemo.base.AndroidxBaseActivity;
import com.example.mutidemo.bean.BlueToothBean;
import com.example.mutidemo.databinding.ActivityBluetoothBinding;
import com.example.mutidemo.util.BLEManager;
import com.example.mutidemo.util.Constant;
import com.example.mutidemo.util.OtherUtils;
import com.example.mutidemo.util.callback.OnBleConnectListener;
import com.example.mutidemo.util.callback.OnDeviceSearchListener;
import com.pengxh.app.multilib.widget.EasyToast;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressLint("SetTextI18n")
public class BluetoothActivity extends AndroidxBaseActivity<ActivityBluetoothBinding> {

    private static final String TAG = "BLEManager";
    private static WeakReferenceHandler weakReferenceHandler;
    private final List<BlueToothBean> blueToothBeans = new ArrayList<>();
    private boolean isConnected = false;
    private StringBuilder builder;

    @Override
    public void initData() {
        weakReferenceHandler = new WeakReferenceHandler(this);
        builder = new StringBuilder();
        if (BLEManager.INSTANCE.initBle(BluetoothActivity.this)) {
            if (BLEManager.INSTANCE.isEnable()) {
                viewBinding.bluetoothStateView.setText("蓝牙状态: ON");
            } else {
                viewBinding.bluetoothStateView.setText("蓝牙状态: OFF");
                BLEManager.INSTANCE.openBluetooth(false);
            }
        } else {
            EasyToast.showToast("该设备不支持低功耗蓝牙", EasyToast.ERROR);
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
                    BLEManager.INSTANCE.disConnectDevice();
                    EasyToast.showToast("设备已断开连接", EasyToast.DEFAULT);
                } else {
                    EasyToast.showToast("设备未连接，无需断开", EasyToast.WARING);
                }
            }
        });
        viewBinding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //搜索蓝牙
                if (BLEManager.INSTANCE.isDiscovery()) {//当前正在搜索设备...
                    BLEManager.INSTANCE.stopDiscoveryDevice();
                }
                OtherUtils.showLoadingDialog(BluetoothActivity.this, "设备搜索中...");
                BLEManager.INSTANCE.startDiscoveryDevice(new OnDeviceSearchListener() {
                    @Override
                    public void onDeviceFound(BlueToothBean blueToothBean) {
                        Message message = weakReferenceHandler.obtainMessage();
                        message.what = Constant.DISCOVERY_DEVICE;
                        message.obj = blueToothBean;
                        weakReferenceHandler.sendMessage(message);
                    }

                    @Override
                    public void onDiscoveryOutTime() {
                        Message message = weakReferenceHandler.obtainMessage();
                        message.what = Constant.DISCOVERY_OUT_TIME;
                        weakReferenceHandler.sendMessage(message);
                    }
                }, 5 * 1000);
            }
        });
    }

    private void startConnectDevice(BluetoothDevice device) {
        // 当前蓝牙设备
        if (!isConnected) {
            OtherUtils.showLoadingDialog(this, "正在连接...");
            BLEManager.INSTANCE.connectBleDevice(device, 10000, Constant.SERVICE_UUID,
                    Constant.READ_CHARACTERISTIC_UUID, Constant.WRITE_CHARACTERISTIC_UUID,
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

    public static void sendEmptyMessage(int what) {
        weakReferenceHandler.sendEmptyMessage(what);
    }

    private static class WeakReferenceHandler extends Handler {

        private final WeakReference<BluetoothActivity> reference;

        private WeakReferenceHandler(BluetoothActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @SuppressLint("MissingPermission")
        @Override
        public void handleMessage(@NonNull Message msg) {
            BluetoothActivity activity = reference.get();
            switch (msg.what) {
                case Constant.BLUETOOTH_ON:
                    EasyToast.showToast("蓝牙已开启", EasyToast.SUCCESS);
                    activity.viewBinding.bluetoothStateView.setText("蓝牙状态: ON");
                    break;
                case Constant.BLUETOOTH_OFF:
                    EasyToast.showToast("蓝牙已关闭", EasyToast.ERROR);
                    activity.viewBinding.bluetoothStateView.setText("蓝牙状态: ON");
                    break;
                case Constant.DISCOVERY_DEVICE:
                    BlueToothBean bean = (BlueToothBean) msg.obj;
                    if (activity.blueToothBeans.size() == 0) {
                        activity.blueToothBeans.add(bean);
                    } else {
                        //0表示未添加到list的新设备，1表示已经扫描并添加到list的设备
                        int judge = 0;
                        for (BlueToothBean it : activity.blueToothBeans) {
                            if (it.getBluetoothDevice().getAddress().equals(bean.getBluetoothDevice().getAddress())) {
                                judge = 1;
                                break;
                            }
                        }
                        if (judge == 0) {
                            activity.blueToothBeans.add(bean);
                        }
                    }
                    break;
                case Constant.DISCOVERY_OUT_TIME:
                    OtherUtils.dismissLoadingDialog();
                    QMUIBottomSheet.BottomListSheetBuilder sheetBuilder = new QMUIBottomSheet.BottomListSheetBuilder(activity);
                    for (BlueToothBean it : activity.blueToothBeans) {
                        sheetBuilder.addItem(it.getBluetoothDevice().getName());
                    }
                    sheetBuilder.setGravityCenter(true).setAddCancelBtn(true).setOnSheetItemClickListener((dialog, itemView, position, tag) -> {
                        dialog.dismiss();
                        //连接点击的设备
                        activity.startConnectDevice(activity.blueToothBeans.get(position).getBluetoothDevice());
                    }).build().show();
                    break;
                case Constant.CONNECT_SUCCESS:
                    OtherUtils.dismissLoadingDialog();
                    activity.isConnected = true;
                    BLEManager.INSTANCE.sendCommand(Constant.ASK_DEV_CODE_COMMAND);
                    break;
                case Constant.CONNECT_FAILURE:
                    activity.isConnected = false;
                    Log.d(TAG, "handleMessage: curConnectState" + false);
                    break;
                case Constant.DISCONNECT_SUCCESS:
                    activity.isConnected = false;
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
                            activity.builder
                                    .append("设备返回值: ")
                                    .append(Arrays.toString(receiveByteArray))
                                    .append("\r\n");
                            activity.viewBinding.deviceValueView.setText(activity.builder.toString());
                        } else {
                            Log.d(TAG, "设备返回值长度异常，无法解析");
                        }
                    } else if (firstByte == (byte) 51) {
                        //解析deviceCode
                        //51, 51, 50, 48, 50, 49, 48, 49, 48, 48, 48, 51, 13, 10, -86, 0, 0, 0, 0, 0
                        if (receiveByteArray.length >= 12) {
                            activity.viewBinding.deviceCodeView.setText("设备编号: " + activity.toDeviceCode(receiveByteArray));
                            BLEManager.INSTANCE.sendCommand(Constant.OPEN_TRANSFER_COMMAND);
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
        }
    }

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
}
