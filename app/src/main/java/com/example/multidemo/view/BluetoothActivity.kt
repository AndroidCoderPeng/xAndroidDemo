package com.example.multidemo.view

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.example.multidemo.R
import com.example.multidemo.base.BaseApplication
import com.example.multidemo.databinding.ActivityBluetoothBinding
import com.example.multidemo.util.DemoConstant
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.convertColor
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.utils.BroadcastManager
import com.pengxh.kt.lite.utils.Constant
import com.pengxh.kt.lite.utils.LoadingDialogHub
import com.pengxh.kt.lite.utils.WeakReferenceHandler
import com.pengxh.kt.lite.widget.dialog.BottomActionSheet
import java.util.UUID

class BluetoothActivity : KotlinBaseActivity<ActivityBluetoothBinding>(), Handler.Callback {

    private val kTag = "BluetoothActivity"
    private val context = this
    private val broadcastManager by lazy { BroadcastManager(this) }
    private val weakReferenceHandler by lazy { WeakReferenceHandler(this) }
    private val bluetoothDevices = ArrayList<BleDevice>()
    private var connectedDevice: BleDevice? = null

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            Constant.BLUETOOTH_ON -> {
                "蓝牙已开启".show(this)
                binding.stateView.text = "蓝牙状态: ON"
            }

            Constant.BLUETOOTH_OFF -> {
                "蓝牙已关闭".show(this)
                binding.stateView.text = "蓝牙状态: ON"
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
        BleManager.getInstance().init(BaseApplication.get())
        BleManager.getInstance()
            .enableLog(true)
            .setReConnectCount(1, 5000)
            .setSplitWriteNum(20)
            .setConnectOverTime(10 * 1000L)
            .setOperateTimeout(5000)

        broadcastManager.addAction(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                intent.action?.apply {
                    if (BluetoothAdapter.ACTION_STATE_CHANGED == this) {
                        when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
                            BluetoothAdapter.STATE_ON -> weakReferenceHandler.sendEmptyMessage(
                                Constant.BLUETOOTH_ON
                            )

                            BluetoothAdapter.STATE_OFF -> weakReferenceHandler.sendEmptyMessage(
                                Constant.BLUETOOTH_OFF
                            )
                        }
                    }
                }
            }
        }, Constant.BLUETOOTH_STATE_CHANGED)

        if (!BleManager.getInstance().isBlueEnable) {
            binding.stateView.text = "蓝牙状态: ON"
        } else {
            binding.stateView.text = "蓝牙状态: OFF"
        }
    }

    override fun initEvent() {
        binding.disconnectButton.setOnClickListener {
            BleManager.getInstance().disconnect(connectedDevice)
        }

        binding.searchButton.setOnClickListener { //搜索蓝牙
            if (!BleManager.getInstance().isBlueEnable) {
                BleManager.getInstance().enableBluetooth()
            }

            if (BleManager.getInstance().isConnected(connectedDevice)) {
                BleManager.getInstance().disconnect(connectedDevice)
            } else {
                BleManager.getInstance().scan(object : BleScanCallback() {
                    override fun onScanStarted(success: Boolean) {
                        LoadingDialogHub.show(this@BluetoothActivity, "设备搜索中...")
                    }

                    override fun onScanning(bleDevice: BleDevice) {

                    }

                    override fun onScanFinished(scanResultList: List<BleDevice>) {
                        LoadingDialogHub.dismiss()

                        scanResultList.forEach {
                            if (!it.name.isNullOrBlank()) {
                                bluetoothDevices.add(it)
                            }
                        }
                        showScanResult()
                    }
                })
            }
        }
    }

    private fun showScanResult() {
        val array = ArrayList<String>()
        for (it in bluetoothDevices) {
            array.add(it.name)
        }

        BottomActionSheet.Builder()
            .setContext(this)
            .setActionItemTitle(array)
            .setItemTextColor(R.color.mainColor.convertColor(this))
            .setOnActionSheetListener(object : BottomActionSheet.OnActionSheetListener {
                override fun onActionItemClick(position: Int) {
                    //连接点击的设备
                    startConnectDevice(bluetoothDevices[position])
                }
            }).build().show()
    }

    private fun startConnectDevice(device: BleDevice) {
        // 当前蓝牙设备
        if (!BleManager.getInstance().isConnected(connectedDevice)) {
            BleManager.getInstance().connect(device, object : BleGattCallback() {
                override fun onStartConnect() {
                    LoadingDialogHub.show(this@BluetoothActivity, "正在连接...")
                }

                override fun onConnectFail(bleDevice: BleDevice, exception: BleException) {
                    LoadingDialogHub.dismiss()
                    "连接失败，请重试".show(context)
                }

                override fun onConnectSuccess(
                    bleDevice: BleDevice, gatt: BluetoothGatt, status: Int
                ) {
                    Log.d(kTag, "onConnectSuccess: ${bleDevice.name} 连接成功")
                    connectedDevice = bleDevice
                    notifyDeviceService(bleDevice, gatt)
                }

                override fun onDisConnected(
                    isActiveDisConnected: Boolean, bleDevice: BleDevice,
                    gatt: BluetoothGatt, status: Int
                ) {
                    Log.d(kTag, "onDisConnected => $status")
                    connectedDevice = null
                    binding.stateView.text = "未连接"
                    binding.stateView.setTextColor(Color.RED)
                }
            })
        } else {
            BleManager.getInstance().disconnect(connectedDevice)
        }
    }

    private fun notifyDeviceService(bleDevice: BleDevice, gatt: BluetoothGatt) {
        val serviceList: List<BluetoothGattService> = gatt.services
        for (service in serviceList) {
            if (service.uuid == UUID.fromString(DemoConstant.SERVICE_UUID)) {
                val characteristicList = service.characteristics
                for (characteristic in characteristicList) {
                    val uuidCharacteristic = characteristic.uuid
                    BleManager.getInstance().notify(
                        bleDevice, service.uuid.toString(), uuidCharacteristic.toString(),
                        object : BleNotifyCallback() {
                            override fun onNotifySuccess() {
                                LoadingDialogHub.dismiss()
                                "连接成功".show(context)
                                binding.stateView.text = "已连接"
                                binding.stateView.setTextColor(Color.GREEN)
                            }

                            override fun onNotifyFailure(exception: BleException) {
                                LoadingDialogHub.dismiss()
                                "蓝牙通讯协议不支持".show(context)
                                connectedDevice = null
                            }

                            override fun onCharacteristicChanged(data: ByteArray) {
                                // 打开通知后，设备发过来的数据
                                Log.d(kTag, data.contentToString())
                            }
                        })
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        broadcastManager.destroy(Constant.BLUETOOTH_STATE_CHANGED)
        BleManager.getInstance().disconnect(connectedDevice)
        BleManager.getInstance().destroy()
    }
}