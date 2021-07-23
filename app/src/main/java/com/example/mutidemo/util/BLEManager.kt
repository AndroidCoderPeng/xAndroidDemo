package com.example.mutidemo.util

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.util.Log
import com.example.mutidemo.bean.BlueToothBean
import com.example.mutidemo.util.callback.OnBleConnectListener
import com.example.mutidemo.util.callback.OnDeviceSearchListener
import java.util.*


/**
 * 1、扫描设备
 * 2、配对设备
 * 3、解除设备配对
 * 4、连接设备
 * 6、发现服务
 * 7、打开读写功能
 * 8、数据通讯（发送数据、接收数据）
 * 9、断开连接
 */
object BLEManager {
    private const val Tag = "BLEManager"
    private const val MAX_CONNECT_TIME = 10000L//连接超时时间10s
    private var context: Context? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private val handler = Handler()
    private var onDeviceSearchListener: OnDeviceSearchListener? = null
    private var isConnecting = false
    private lateinit var serviceUUID: UUID
    private lateinit var readUUID: UUID
    private lateinit var writeUUID: UUID
    private var onBleConnectListener: OnBleConnectListener? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var readCharacteristic: BluetoothGattCharacteristic? = null
    private var writeCharacteristic: BluetoothGattCharacteristic? = null

    fun initBle(context: Context): Boolean {
        BLEManager.context = context
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val bluetoothManager =
                    context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = bluetoothManager.adapter
            bluetoothAdapter != null
        } else {
            false
        }
    }

    fun isEnable(): Boolean {
        if (bluetoothAdapter == null) {
            return false
        }
        return bluetoothAdapter!!.isEnabled
    }

    /**
     * 打开蓝牙
     * @param isFast  true 直接打开蓝牙  false 提示用户打开
     */
    fun openBluetooth(isFast: Boolean) {
        if (!isEnable()) {
            if (isFast) {
                Log.d(Tag, "直接打开手机蓝牙")
                bluetoothAdapter!!.enable()
            } else {
                context?.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            }
        } else {
            Log.d(Tag, "手机蓝牙状态已开")
        }
    }

    /**
     * 本地蓝牙是否处于正在扫描状态
     * @return true false
     */
    fun isDiscovery(): Boolean {
        if (bluetoothAdapter == null) {
            return false
        }
        return bluetoothAdapter!!.isDiscovering
    }

    fun stopDiscoveryDevice() {
        handler.removeCallbacks(stopScanRunnable)
        if (bluetoothAdapter == null) {
            return
        }
        bluetoothAdapter!!.stopLeScan(scanCallback)
    }

    private val stopScanRunnable = Runnable {
        if (onDeviceSearchListener != null) {
            onDeviceSearchListener!!.onDiscoveryOutTime()  //扫描超时回调
        }
        //scanTime之后还没有扫描到设备，就停止扫描。
        stopDiscoveryDevice()
    }

    //扫描设备回调
    private val scanCallback = object : BluetoothAdapter.LeScanCallback {
        override fun onLeScan(device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray?) {
            if (device == null) return
            if (device.name != null) {
//                Log.d(Tag, device.name + "-->" + device.address)
                //去掉非必要的数据，比如mobike的蓝牙的等数据
                if (device.name != "mobike" || device.name != "") {
                    if (onDeviceSearchListener != null) {
                        onDeviceSearchListener!!.onDeviceFound(BlueToothBean(device, rssi))
                    }
                }
            }
        }
    }

    fun startDiscoveryDevice(onDeviceSearchListener: OnDeviceSearchListener, scanTime: Long) {
        if (bluetoothAdapter == null) {
            return
        }
        BLEManager.onDeviceSearchListener = onDeviceSearchListener
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bluetoothAdapter!!.startLeScan(scanCallback)
        } else {
            return
        }
        //设定最长扫描时间
        handler.postDelayed(stopScanRunnable, scanTime)
    }

    fun connectBleDevice(bluetoothDevice: BluetoothDevice, outTime: Long, serviceUUID: String,
                         readUUID: String, writeUUID: String, onBleConnectListener: OnBleConnectListener) {
        if (isConnecting) {
            Log.d(Tag, "connectBleDevice()-->isConnecting = true")
            return
        }
        BLEManager.serviceUUID = UUID.fromString(serviceUUID)
        BLEManager.readUUID = UUID.fromString(readUUID)
        BLEManager.writeUUID = UUID.fromString(writeUUID)
        BLEManager.onBleConnectListener = onBleConnectListener
        Log.d(Tag, "开始准备连接：" + bluetoothDevice.name + "-->" + bluetoothDevice.address)
        try {
            bluetoothGatt = bluetoothDevice.connectGatt(context, false, bluetoothGattCallback)
            bluetoothGatt!!.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //设置连接超时时间10s
        handler.postDelayed(connectOutTimeRunnable, outTime)
    }

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            val bluetoothDevice = gatt!!.device
            Log.d(Tag, "连接的设备：" + bluetoothDevice.name + "  " + bluetoothDevice.address)
            isConnecting = true
            //移除连接超时
            handler.removeCallbacks(connectOutTimeRunnable)
            when (newState) {
                BluetoothGatt.STATE_CONNECTING -> {
                    Log.d(Tag, "正在连接...")
                    if (onBleConnectListener != null) {
                        onBleConnectListener!!.onConnecting(gatt)  //正在连接回调
                    }
                }
                BluetoothGatt.STATE_CONNECTED -> {
                    Log.d(Tag, "连接成功")
                    //连接成功去发现服务
                    gatt.discoverServices()
                    //设置发现服务超时时间
                    handler.postDelayed(serviceDiscoverOutTimeRunnable, MAX_CONNECT_TIME)
                    if (onBleConnectListener != null) {
                        onBleConnectListener!!.onConnectSuccess(gatt, status)
                    }
                }
                BluetoothGatt.STATE_DISCONNECTING -> {
                    Log.d(Tag, "正在断开...")
                    if (onBleConnectListener != null) {
                        onBleConnectListener!!.onDisConnecting(gatt) //正在断开回调
                    }
                }
                BluetoothGatt.STATE_DISCONNECTED -> {
                    Log.d(Tag, "断开连接status: $status")
                    gatt.close()
                    isConnecting = false
                    when (status) {
                        133 -> {//133连接异常,无法连接
                            if (onBleConnectListener != null) {
                                gatt.close()
                                onBleConnectListener!!.onConnectFailure(
                                        gatt, "连接异常！", status
                                )
                                Log.d(Tag, "连接失败status：" + status + "  " + bluetoothDevice.address)
                            }
                        }
                        62 -> {//62没有发现服务 异常断开
                            if (onBleConnectListener != null) {
                                gatt.close()
                                onBleConnectListener!!.onConnectFailure(
                                        gatt, "连接成功服务未发现断开！", status
                                )
                            }
                        }
                        0 -> { //0正常断开 回调
                            if (onBleConnectListener != null) {
                                onBleConnectListener!!.onDisConnectSuccess(gatt, status)
                            }
                        }
                        8 -> {//因为距离远或者电池无法供电断开连接
                            if (onBleConnectListener != null) {
                                onBleConnectListener!!.onDisConnectSuccess(gatt, status)
                            }
                        }
                        34 -> {//34断开
                            if (onBleConnectListener != null) {
                                onBleConnectListener!!.onDisConnectSuccess(gatt, status)
                            }
                        }
                        else -> {//其它断开连接
                            if (onBleConnectListener != null) {
                                onBleConnectListener!!.onDisConnectSuccess(gatt, status)
                            }
                        }
                    }
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            isConnecting = false
            //移除发现服务超时
            handler.removeCallbacks(serviceDiscoverOutTimeRunnable)
            //配置服务信息
            if (onBleConnectListener != null) {
                if (setupService(gatt!!, serviceUUID, readUUID, writeUUID)) {
                    //成功发现服务回调
                    onBleConnectListener!!.onServiceDiscoverySucceed(gatt, status)
                } else {
                    onBleConnectListener!!.onServiceDiscoveryFailed(gatt, "获取服务特征异常")
                }
            }
        }

        //读取蓝牙设备发出来的数据回调
        override fun onCharacteristicRead(
                gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            Log.d(Tag, "读status: $status")
        }

        //向蓝牙设备写入数据结果回调
        override fun onCharacteristicWrite(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?, status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (characteristic!!.value == null) {
                Log.e(Tag, "characteristic.getValue() == null");
                return
            }
            //将收到的字节数组转换成十六进制字符串
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    //写入成功
                    if (onBleConnectListener != null) {
                        onBleConnectListener!!.onWriteSuccess(gatt, characteristic.value)
                    }
                }
                BluetoothGatt.GATT_FAILURE -> {
                    //写入失败
                    if (onBleConnectListener != null) {
                        onBleConnectListener!!.onWriteFailure(gatt, characteristic.value, "写入失败")
                    }
                }
                BluetoothGatt.GATT_WRITE_NOT_PERMITTED -> {
                    Log.d(Tag, "没有权限")
                }
            }
        }

        override fun onCharacteristicChanged(
                gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            //接收数据
            Log.d(Tag, "收到数据:" + characteristic!!.value.toList())
            if (onBleConnectListener != null) {
                onBleConnectListener!!.onReceiveMessage(gatt, characteristic)  //接收数据回调
            } else {
                Log.d(Tag, "onCharacteristicChanged-->onBleConnectListener == null")
            }
        }

        override fun onDescriptorRead(
                gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int
        ) {
            super.onDescriptorRead(gatt, descriptor, status)
            //开启监听成功，可以从设备读数据了
            Log.d(Tag, "onDescriptorRead开启监听成功${descriptor!!.value.toList()}")
        }

        override fun onDescriptorWrite(
                gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            //开启监听成功，可以向设备写入命令了
            Log.d(Tag, "onDescriptorWrite开启监听成功")
        }

        //蓝牙信号强度
        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            super.onReadRemoteRssi(gatt, rssi, status)
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    Log.w(Tag, "读取RSSI值成功，RSSI值: $rssi ,status: $status")
                    if (onBleConnectListener != null) {
                        onBleConnectListener!!.onReadRssi(gatt, rssi, status)  //成功读取连接的信号强度回调
                    }
                }
                BluetoothGatt.GATT_FAILURE -> Log.w(Tag, "读取RSSI值失败，status: $status")
            }
        }
    }

    private val connectOutTimeRunnable = Runnable {
        if (bluetoothGatt == null) {
            Log.d(Tag, "connectOutTimeRunnable-->bluetoothGatt == null")
            return@Runnable
        }
        isConnecting = false
        bluetoothGatt!!.disconnect()
        //连接超时当作连接失败回调
        if (onBleConnectListener != null) {
            onBleConnectListener!!.onConnectFailure(
                    bluetoothGatt,
                    "连接超时",
                    -1
            )  //连接失败回调
        }
    }

    private val serviceDiscoverOutTimeRunnable = Runnable {
        if (bluetoothGatt == null) {
            Log.d(Tag, "serviceDiscoverOutTimeRunnable-->bluetoothGatt == null");
            return@Runnable
        }
        isConnecting = false
        bluetoothGatt!!.disconnect()
        //发现服务超时当作连接失败回调
        if (onBleConnectListener != null) {
            onBleConnectListener!!.onConnectFailure(
                    bluetoothGatt,
                    "发现服务超时！",
                    -1
            )  //连接失败回调
        }
    }

    private fun setupService(
            bluetoothGatt: BluetoothGatt, serviceUUID: UUID, readUUID: UUID, writeUUID: UUID
    ): Boolean {
        var notifyCharacteristic: BluetoothGattCharacteristic? = null
        bluetoothGatt.services.forEach { service ->
            if (service.uuid == serviceUUID) {
                service!!.characteristics.forEach { characteristic ->
                    val charaProp = characteristic.properties
                    if (characteristic.uuid == readUUID) {  //读特征
                        readCharacteristic = characteristic
                    }
                    if (characteristic.uuid == writeUUID) {  //写特征
                        writeCharacteristic = characteristic
                    }
                    if (charaProp and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                        val notifyServiceUUID = service.uuid
                        val notifyCharacteristicUUID = characteristic.uuid
                        Log.d(Tag, "notifyCharacteristicUUID=$notifyCharacteristicUUID, notifyServiceUUID=$notifyServiceUUID")
                        notifyCharacteristic = bluetoothGatt.getService(notifyServiceUUID)
                                .getCharacteristic(notifyCharacteristicUUID)
                    }
                }
            }
        }
        //打开读通知，打开的是notifyCharacteristic！！！，不然死活不走onCharacteristicChanged回调
        bluetoothGatt.setCharacteristicNotification(notifyCharacteristic, true)
        //一定要重新设置
        for (descriptor in notifyCharacteristic!!.descriptors) {
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            bluetoothGatt.writeDescriptor(descriptor)
        }
        //延迟2s，保证所有通知都能及时打开
        handler.postDelayed({ }, 2000)
        return true
    }

    fun sendCommand(cmd: ByteArray) {
        if (writeCharacteristic == null) {
            Log.d(Tag, "sendCommand(ByteArray)-->writeGattCharacteristic == null")
            return
        }
        if (bluetoothGatt == null) {
            Log.d(Tag, "sendCommand(ByteArray)-->bluetoothGatt == null")
            return
        }
        val value = writeCharacteristic!!.setValue(cmd)
        Log.d(Tag, "写特征设置值结果：$value")
        bluetoothGatt!!.writeCharacteristic(writeCharacteristic)
    }

    fun disConnectDevice() {
        if (bluetoothGatt == null) {
            Log.d(Tag, "disConnectDevice(ByteArray)-->bluetoothGatt == null");
            return
        }
        bluetoothGatt!!.disconnect()
        isConnecting = false
    }
}