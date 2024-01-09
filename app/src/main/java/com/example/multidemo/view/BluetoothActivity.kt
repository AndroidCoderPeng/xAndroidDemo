package com.example.multidemo.view

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import com.example.multidemo.databinding.ActivityBluetoothBinding
import com.example.multidemo.util.DemoConstant
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.extensions.toASCII
import com.pengxh.kt.lite.utils.BroadcastManager
import com.pengxh.kt.lite.utils.Constant
import com.pengxh.kt.lite.utils.LoadingDialogHub
import com.pengxh.kt.lite.utils.WeakReferenceHandler
import com.pengxh.kt.lite.utils.ble.BLEManager
import com.pengxh.kt.lite.utils.ble.BluetoothDevice
import com.pengxh.kt.lite.utils.ble.OnBleConnectListener
import com.pengxh.kt.lite.utils.ble.OnDeviceDiscoveredListener
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet
import java.util.*

@SuppressLint("all")
class BluetoothActivity : KotlinBaseActivity<ActivityBluetoothBinding>(), Handler.Callback {

    private val kTag = "BluetoothActivity"
    private val bleManager by lazy { BLEManager(this) }
    private val broadcastManager by lazy { BroadcastManager(this) }
    private val weakReferenceHandler by lazy { WeakReferenceHandler(this) }
    private val bluetoothDevices = ArrayList<BluetoothDevice>()
    private val builder by lazy { StringBuilder() }
    private var isConnected = false

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            Constant.BLUETOOTH_ON -> {
                "蓝牙已开启".show(this)
                binding.bluetoothStateView.text = "蓝牙状态: ON"
            }

            Constant.BLUETOOTH_OFF -> {
                "蓝牙已关闭".show(this)
                binding.bluetoothStateView.text = "蓝牙状态: ON"
            }

            Constant.DISCOVERY_DEVICE -> {
                val device = msg.obj as BluetoothDevice
                if (bluetoothDevices.size == 0) {
                    bluetoothDevices.add(device)
                } else {
                    //0表示未添加到list的新设备，1表示已经扫描并添加到list的设备
                    var judge = 0
                    for (it in bluetoothDevices) {
                        if (it.device.address.equals(device.device.address)) {
                            judge = 1
                            break
                        }
                    }
                    if (judge == 0) {
                        bluetoothDevices.add(device)
                    }
                }
            }

            Constant.DISCOVERY_OUT_TIME -> {
                LoadingDialogHub.dismiss()
                val sheetBuilder = QMUIBottomSheet.BottomListSheetBuilder(this)
                for (it in bluetoothDevices) {
                    sheetBuilder.addItem(it.device.name)
                }
                sheetBuilder.setGravityCenter(true).setAddCancelBtn(true)
                    .setOnSheetItemClickListener { dialog: QMUIBottomSheet, _: View?, position: Int, _: String? ->
                        dialog.dismiss()
                        //连接点击的设备
                        startConnectDevice(bluetoothDevices[position].device)
                    }.build().show()
            }

            Constant.CONNECT_SUCCESS -> {
                LoadingDialogHub.dismiss()
                isConnected = true
                bleManager.sendCommand(DemoConstant.ASK_DEV_CODE_COMMAND)
            }

            Constant.CONNECT_FAILURE -> {
                isConnected = false
                Log.d(kTag, "handleMessage: curConnectState" + false)
            }

            Constant.DISCONNECT_SUCCESS -> isConnected = false
            Constant.SEND_SUCCESS -> {
                val sendSuccess = msg.obj as ByteArray
                Log.d(kTag, "发送成功->sendSuccessBuffer: " + sendSuccess.contentToString())
            }

            Constant.SEND_FAILURE -> {
                val sendFail = msg.obj as ByteArray
                Log.d(kTag, "发送失败->sendFailBuffer: " + sendFail.contentToString())
            }

            Constant.RECEIVE_SUCCESS -> {
                val receiveByteArray = msg.obj as ByteArray
                //根据返回值标头判断是设备编号还是数据值
                val firstByte = receiveByteArray[0]
                if (firstByte == 0xAA.toByte()) {
                    //解析测量数据
                    if (receiveByteArray.size == 14) {
                        builder.append("设备返回值: ")
                            .append(receiveByteArray.contentToString())
                            .append("\r\n")
                        binding.deviceValueView.text = builder.toString()
                    } else {
                        Log.d(kTag, "设备返回值长度异常，无法解析")
                    }
                } else if (firstByte == 51.toByte() && receiveByteArray.size >= 14) {
                    //51, 51, 50, 48, 50, 49, 48, 49, 48, 48, 48, 51, 13, 10, -86, 0, 0, 0, 0, 0
                    binding.deviceCodeView.text = "设备编号: ${receiveByteArray.toASCII()}"
                    bleManager.sendCommand(DemoConstant.OPEN_TRANSFER_COMMAND)
                } else {
                    Log.d(kTag, "未知返回值，无法解析")
                }
            }

            Constant.RECEIVE_FAILURE -> {
                val receiveString = msg.obj as String
                Log.d(kTag, "接收失败->receiveString: $receiveString")
            }
        }
        return true
    }

    override fun setupTopBarLayout() {}

    override fun observeRequestState() {

    }

    override fun initViewBinding(): ActivityBluetoothBinding {
        return ActivityBluetoothBinding.inflate(layoutInflater)
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        broadcastManager.addAction(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (BluetoothAdapter.ACTION_STATE_CHANGED == Objects.requireNonNull<String>(intent.action)) {
                    when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
                        BluetoothAdapter.STATE_ON -> weakReferenceHandler.sendEmptyMessage(Constant.BLUETOOTH_ON)
                        BluetoothAdapter.STATE_OFF -> weakReferenceHandler.sendEmptyMessage(Constant.BLUETOOTH_OFF)
                    }
                }
            }
        }, Constant.BLUETOOTH_STATE_CHANGED)

        if (bleManager.isBluetoothEnabled()) {
            binding.bluetoothStateView.text = "蓝牙状态: ON"
        } else {
            binding.bluetoothStateView.text = "蓝牙状态: OFF"
            bleManager.openBluetooth(true)
        }
    }

    override fun initEvent() {
        binding.disconnectButton.setChangeAlphaWhenPress(true)
        binding.searchButton.setChangeAlphaWhenPress(true)
        binding.disconnectButton.setOnClickListener {
            if (isConnected) {
                //断开连接
                bleManager.disConnectDevice()
                "设备已断开连接".show(this)
            } else {
                "设备未连接，无需断开".show(this)
            }
        }
        binding.searchButton.setOnClickListener { //搜索蓝牙
            if (bleManager.isDiscovering()) { //当前正在搜索设备...
                bleManager.stopDiscoverDevice()
            }
            LoadingDialogHub.show(this, "设备搜索中...")
            bleManager.startScanDevice(object : OnDeviceDiscoveredListener {
                override fun onDeviceFound(device: BluetoothDevice) {
                    val message: Message = weakReferenceHandler.obtainMessage()
                    message.what = Constant.DISCOVERY_DEVICE
                    message.obj = device
                    weakReferenceHandler.sendMessage(message)
                }

                override fun onDiscoveryTimeout() {
                    val message: Message = weakReferenceHandler.obtainMessage()
                    message.what = Constant.DISCOVERY_OUT_TIME
                    weakReferenceHandler.sendMessage(message)
                }
            }, 15 * 1000)
        }
    }

    private fun startConnectDevice(device: android.bluetooth.BluetoothDevice) {
        // 当前蓝牙设备
        if (!isConnected) {
            LoadingDialogHub.show(this, "正在连接...")
            bleManager.connectBleDevice(
                device,
                DemoConstant.SERVICE_UUID,
                DemoConstant.READ_CHARACTERISTIC_UUID,
                DemoConstant.WRITE_CHARACTERISTIC_UUID,
                10000,
                onBleConnectListener
            )
        }
    }

    private val onBleConnectListener: OnBleConnectListener = object : OnBleConnectListener {
        override fun onConnecting(bluetoothGatt: BluetoothGatt?) {}
        override fun onConnectSuccess(bluetoothGatt: BluetoothGatt?, status: Int) {}
        override fun onConnectFailure(
            bluetoothGatt: BluetoothGatt?,
            exception: String?,
            status: Int
        ) {
            val message: Message = weakReferenceHandler.obtainMessage()
            message.what = Constant.CONNECT_FAILURE
            weakReferenceHandler.sendMessage(message)
        }

        override fun onDisConnecting(bluetoothGatt: BluetoothGatt?) {}
        override fun onDisConnectSuccess(bluetoothGatt: BluetoothGatt?, status: Int) {
            val message: Message = weakReferenceHandler.obtainMessage()
            message.what = Constant.DISCONNECT_SUCCESS
            message.obj = status
            weakReferenceHandler.sendMessage(message)
        }

        override fun onServiceDiscoverySucceed(bluetoothGatt: BluetoothGatt?, status: Int) {
            val message: Message = weakReferenceHandler.obtainMessage()
            message.what = Constant.CONNECT_SUCCESS
            weakReferenceHandler.sendMessage(message)
        }

        override fun onServiceDiscoveryFailed(bluetoothGatt: BluetoothGatt?, msg: String?) {
            val message: Message = weakReferenceHandler.obtainMessage()
            message.what = Constant.CONNECT_FAILURE
            weakReferenceHandler.sendMessage(message)
        }

        override fun onReceiveMessage(bluetoothGatt: BluetoothGatt?, value: ByteArray) {
            val message: Message = weakReferenceHandler.obtainMessage()
            message.what = Constant.RECEIVE_SUCCESS
            message.obj = value
            weakReferenceHandler.sendMessage(message)
        }

        override fun onReceiveError(errorMsg: String?) {
            val message: Message = weakReferenceHandler.obtainMessage()
            message.what = Constant.RECEIVE_FAILURE
            weakReferenceHandler.sendMessage(message)
        }

        override fun onWriteSuccess(bluetoothGatt: BluetoothGatt?, msg: ByteArray?) {
            val message: Message = weakReferenceHandler.obtainMessage()
            message.what = Constant.SEND_SUCCESS
            message.obj = msg
            weakReferenceHandler.sendMessage(message)
        }

        override fun onWriteFailure(
            bluetoothGatt: BluetoothGatt?, msg: ByteArray?, errorMsg: String?
        ) {
            val message: Message = weakReferenceHandler.obtainMessage()
            message.what = Constant.SEND_FAILURE
            message.obj = msg
            weakReferenceHandler.sendMessage(message)
        }

        override fun onReadRssi(bluetoothGatt: BluetoothGatt?, rssi: Int, status: Int) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        broadcastManager.destroy(Constant.BLUETOOTH_STATE_CHANGED)
    }
}