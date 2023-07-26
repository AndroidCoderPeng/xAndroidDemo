package com.example.multidemo.view

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import com.example.multidemo.R
import com.example.multidemo.util.DemoConstant
import com.example.multidemo.util.LoadingDialogHub
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.utils.BroadcastManager
import com.pengxh.kt.lite.utils.Constant
import com.pengxh.kt.lite.utils.WeakReferenceHandler
import com.pengxh.kt.lite.utils.ble.BLEManager
import com.pengxh.kt.lite.utils.ble.BlueToothBean
import com.pengxh.kt.lite.utils.ble.OnBleConnectListener
import com.pengxh.kt.lite.utils.ble.OnDeviceDiscoveredListener
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet
import kotlinx.android.synthetic.main.activity_bluetooth.*
import java.util.*

class BluetoothActivity : KotlinBaseActivity() {

    private val kTag = "BluetoothActivity"
    private val blueToothBeans: MutableList<BlueToothBean> = ArrayList<BlueToothBean>()
    private val builder by lazy { StringBuilder() }
    private lateinit var weakReferenceHandler: WeakReferenceHandler
    private lateinit var broadcastManager: BroadcastManager
    private var isConnected = false

    override fun setupTopBarLayout() {}

    override fun observeRequestState() {

    }

    override fun initLayoutView(): Int = R.layout.activity_bluetooth

    override fun initData(savedInstanceState: Bundle?) {
        broadcastManager = BroadcastManager.obtainInstance(this)
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

        weakReferenceHandler = WeakReferenceHandler(callback)

        if (BLEManager.initBLE(this)) {
            if (BLEManager.isBluetoothEnable()) {
                bluetoothStateView.text = "蓝牙状态: ON"
            } else {
                bluetoothStateView.text = "蓝牙状态: OFF"
                BLEManager.openBluetooth(true)
            }
        } else {
            "该设备不支持低功耗蓝牙".show(this)
        }
    }

    override fun initEvent() {
        disconnectButton.setChangeAlphaWhenPress(true)
        searchButton.setChangeAlphaWhenPress(true)
        disconnectButton.setOnClickListener {
            if (isConnected) {
                //断开连接
                BLEManager.disConnectDevice()
                "设备已断开连接".show(this)
            } else {
                "设备未连接，无需断开".show(this)
            }
        }
        searchButton.setOnClickListener { //搜索蓝牙
            if (BLEManager.isDiscovery()) { //当前正在搜索设备...
                BLEManager.stopDiscoverDevice()
            }
            LoadingDialogHub.show(this, "设备搜索中...")
            BLEManager.startDiscoverDevice(object : OnDeviceDiscoveredListener {
                override fun onDeviceFound(blueToothBean: BlueToothBean?) {
                    val message: Message = weakReferenceHandler.obtainMessage()
                    message.what = Constant.DISCOVERY_DEVICE
                    message.obj = blueToothBean
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

    private fun startConnectDevice(device: BluetoothDevice) {
        // 当前蓝牙设备
        if (!isConnected) {
            LoadingDialogHub.show(this, "正在连接...")
            BLEManager.connectBleDevice(
                this, device, 10000,
                DemoConstant.SERVICE_UUID,
                DemoConstant.READ_CHARACTERISTIC_UUID,
                DemoConstant.WRITE_CHARACTERISTIC_UUID,
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

        override fun onReceiveMessage(
            bluetoothGatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?
        ) {
            val message: Message = weakReferenceHandler.obtainMessage()
            message.what = Constant.RECEIVE_SUCCESS
            message.obj = characteristic?.value
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

    @SuppressLint("MissingPermission")
    private val callback = Handler.Callback { msg ->
        when (msg.what) {
            Constant.BLUETOOTH_ON -> {
                "蓝牙已开启".show(this)
                bluetoothStateView.text = "蓝牙状态: ON"
            }
            Constant.BLUETOOTH_OFF -> {
                "蓝牙已关闭".show(this)
                bluetoothStateView.text = "蓝牙状态: ON"
            }
            Constant.DISCOVERY_DEVICE -> {
                val bean: BlueToothBean = msg.obj as BlueToothBean
                if (blueToothBeans.size == 0) {
                    blueToothBeans.add(bean)
                } else {
                    //0表示未添加到list的新设备，1表示已经扫描并添加到list的设备
                    var judge = 0
                    for (it in blueToothBeans) {
                        if (it.bluetoothDevice.address.equals(bean.bluetoothDevice.address)) {
                            judge = 1
                            break
                        }
                    }
                    if (judge == 0) {
                        blueToothBeans.add(bean)
                    }
                }
            }
            Constant.DISCOVERY_OUT_TIME -> {
                LoadingDialogHub.dismiss()
                val sheetBuilder = QMUIBottomSheet.BottomListSheetBuilder(this)
                for (it in blueToothBeans) {
                    sheetBuilder.addItem(it.bluetoothDevice.name)
                }
                sheetBuilder.setGravityCenter(true).setAddCancelBtn(true)
                    .setOnSheetItemClickListener { dialog: QMUIBottomSheet, _: View?, position: Int, _: String? ->
                        dialog.dismiss()
                        //连接点击的设备
                        startConnectDevice(blueToothBeans[position].bluetoothDevice)
                    }.build().show()
            }
            Constant.CONNECT_SUCCESS -> {
                LoadingDialogHub.dismiss()
                isConnected = true
                BLEManager.sendCommand(DemoConstant.ASK_DEV_CODE_COMMAND)
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
                        deviceValueView.text = builder.toString()
                    } else {
                        Log.d(kTag, "设备返回值长度异常，无法解析")
                    }
                } else if (firstByte == 51.toByte() && receiveByteArray.size >= 14) {
                    //51, 51, 50, 48, 50, 49, 48, 49, 48, 48, 48, 51, 13, 10, -86, 0, 0, 0, 0, 0
                    deviceCodeView.text = "设备编号: ${receiveByteArray.toDeviceCode()}"
                    BLEManager.sendCommand(DemoConstant.OPEN_TRANSFER_COMMAND)
                } else {
                    Log.d(kTag, "未知返回值，无法解析")
                }
            }
            Constant.RECEIVE_FAILURE -> {
                val receiveString = msg.obj as String
                Log.d(kTag, "接收失败->receiveString: $receiveString")
            }
        }
        true
    }

    private fun ByteArray.toDeviceCode(): String {
        /**
         * 51, 51, 50, 48, 50, 49, 48, 49, 48, 48, 48, 51, 13, 10, -86, 0, 0, 0, 0, 0
         *
         * 51是数据标头
         * 13,10是数据结束位
         * 后面的是其他参数，不解析
         */
        val builder = StringBuilder()
        for (i in 0..11) {
            builder.append(this[i].toInt().toChar())
        }
        return builder.toString()
    }

    override fun onDestroy() {
        broadcastManager.destroy(Constant.BLUETOOTH_STATE_CHANGED)
        super.onDestroy()
    }
}