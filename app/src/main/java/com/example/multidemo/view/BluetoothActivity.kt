package com.example.multidemo.view

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.example.multidemo.R
import com.example.multidemo.base.BaseApplication
import com.example.multidemo.databinding.ActivityBluetoothBinding
import com.example.multidemo.util.DemoConstant
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.convertColor
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.utils.LoadingDialogHub
import com.pengxh.kt.lite.widget.dialog.BottomActionSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class BluetoothActivity : KotlinBaseActivity<ActivityBluetoothBinding>() {

    private val kTag = "BluetoothActivity"
    private val context = this
    private val bleManager by lazy { BleManager.getInstance() }
    private val stringBuffer by lazy { StringBuffer() }
    private val bluetoothDevices = ArrayList<BleDevice>()
    private var connectedDevice: BleDevice? = null
    private lateinit var writeUuid: String
    private lateinit var notifyUuid: String

    override fun setupTopBarLayout() {}

    override fun observeRequestState() {

    }

    override fun initViewBinding(): ActivityBluetoothBinding {
        return ActivityBluetoothBinding.inflate(layoutInflater)
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        bleManager.enableLog(true).setSplitWriteNum(16).init(BaseApplication.get())
    }

    override fun initEvent() {
        binding.disconnectButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                sendCommand(DemoConstant.CLOSE_TRANSFER_COMMAND)

                delay(500)

                //关闭数据传送指令
                bleManager.disconnect(connectedDevice)
            }
        }

        binding.searchButton.setOnClickListener { //搜索蓝牙
            if (!bleManager.isBlueEnable) {
                bleManager.enableBluetooth()
            }

            if (bleManager.isConnected(connectedDevice)) {
                bleManager.disconnect(connectedDevice)
            }
            bleManager.scan(object : BleScanCallback() {
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

        binding.obtainDeviceCodeButton.setOnClickListener {
            sendCommand(DemoConstant.ASK_DEV_CODE_COMMAND)
        }
    }

    private fun sendCommand(command: ByteArray) {
        bleManager.write(connectedDevice, DemoConstant.UUIDS[2], writeUuid, command,
            object : BleWriteCallback() {
                override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray?) {
                    "指令下发成功".show(context)
                }

                override fun onWriteFailure(exception: BleException?) {
                    "指令下发失败".show(context)
                }
            })
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
        bleManager.connect(device, object : BleGattCallback() {
            override fun onStartConnect() {
                LoadingDialogHub.show(this@BluetoothActivity, "设备连接中...")
            }

            override fun onConnectFail(bleDevice: BleDevice, exception: BleException) {
                LoadingDialogHub.dismiss()
            }

            override fun onConnectSuccess(bleDevice: BleDevice, gatt: BluetoothGatt, status: Int) {
                connectedDevice = bleDevice
                notifyDeviceService(bleDevice, gatt)
            }

            override fun onDisConnected(
                isActiveDisConnected: Boolean, bleDevice: BleDevice,
                gatt: BluetoothGatt, status: Int
            ) {
                LoadingDialogHub.dismiss()
                connectedDevice = null
                binding.stateView.text = "蓝牙状态：未连接"
                binding.stateView.setTextColor(Color.RED)
            }
        })
    }

    private fun notifyDeviceService(bleDevice: BleDevice, gatt: BluetoothGatt) {
        val gattService = gatt.getService(UUID.fromString(DemoConstant.UUIDS[2]))
        if (gattService == null) {
            Log.d(kTag, "notifyDeviceService: gattService is null")
            LoadingDialogHub.dismiss()
            "连接失败，设备不支持低功耗蓝牙".show(this)
            return
        }

        gattService.characteristics.forEach {
            //获取到相应的服务UUID和特征UUID
            val uuid = it.uuid
            val properties = it.properties

            if (properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE > 0 ||
                properties and BluetoothGattCharacteristic.PROPERTY_WRITE > 0
            ) {
                Log.d(kTag, "uuid可写: $uuid")
                writeUuid = uuid.toString()
                it.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            }

            if (properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0 ||
                properties and BluetoothGattCharacteristic.PROPERTY_INDICATE > 0
            ) {
                Log.d(kTag, "uuid可通知: $uuid")
                notifyUuid = uuid.toString()
            }
        }

        bleManager.notify(bleDevice, DemoConstant.UUIDS[2], notifyUuid,
            object : BleNotifyCallback() {
                override fun onNotifySuccess() {
                    LoadingDialogHub.dismiss()
                    "连接成功".show(context)
                    binding.stateView.text = "蓝牙状态：已连接"
                    binding.stateView.setTextColor(Color.GREEN)
                }

                override fun onNotifyFailure(exception: BleException) {
                    connectedDevice = null
                }

                override fun onCharacteristicChanged(data: ByteArray) {
                    // 打开通知后，设备发过来的数据
                    Log.d(kTag, "设备返回 <=== ${data.contentToString()}")
                    if (data.first() == 51.toByte() && data.size >= 14) {
                        //[51, 57, 50, 48, 50, 52, 48, 49, 48, 48, 48, 54, 32, 0]
                        lifecycleScope.launch(Dispatchers.IO) {
                            val builder = StringBuilder()
                            for (index in 0..11) {
                                builder.append(data[index].toInt().toChar())
                            }
                            withContext(Dispatchers.Main) {
                                binding.deviceCodeView.text = "设备编号:$builder"
                            }

                            delay(500)

                            //发送数据传送指令
                            sendCommand(DemoConstant.OPEN_TRANSFER_COMMAND)
                        }
                    } else if (data.first() == (-86).toByte() && data.size >= 14) {
                        //[-86, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 13, 10, -86, 0, 0, 0, 0, 1]
                        stringBuffer.append(data.contentToString()).append("\r\n")
                        binding.deviceValueView.text = stringBuffer.toString()
                    }
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        bleManager.disconnect(connectedDevice)
        bleManager.destroy()
    }
}