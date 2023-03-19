package com.example.mutidemo.util.netty

import android.util.Log
import com.example.mutidemo.util.DemoConstant
import com.google.gson.Gson

class SocketManager : ISocketListener {

    companion object {
        //Kotlin委托模式双重锁单例
        val get: SocketManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SocketManager()
        }
    }

    private val kTag = "SocketManager"
    private val nettyClient by lazy { SocketClient() }

    fun connectNetty(hostname: String, port: Int) {
        Thread {
            if (!nettyClient.connectStatus) {
                nettyClient.setSocketListener(this)
                nettyClient.connect(hostname, port)
            } else {
                nettyClient.disconnect()
            }
        }.start()
    }

    override fun onMessageResponse(data: ByteArray) {
        Log.d(kTag, "channelRead0 ===> " + data.contentToString())
        /**
         * 0xFF,0x01,
         * 0x01,0x37,0xE6,  甲烷浓度值(数据码1* 65536 + 数据码2 * 256 + 数据码3)
         * 0x00,            激光甲烷模块工作状态(00表示设备正常，01表示温控故障，02表示设备激光未打开)
         * 0x00,0xB2,0x35,  激光强度值(数据码5* 65536 + 数据码6 * 256 + 数据码7)
         * 0x0A,0x3D,       云台水平角度([数据码8 * 256 + 数据码9]/100，单位为°，精确到0.01)
         * 0x05,0x6F,       云台垂直角度(首先计算Tangle=[数据码10 * 256 + 数据码11]/100，单位为°，精确到0.01。若Tangle在0~90范围内，则垂直角度值=Tangle；若Tangle在-1~-90范围内，则垂直角度值=Tangle-360)
         * 0xC1
         *
         * [-1, 1, 1, 55, -26, 0, 0, -78, 53, 10, 61, 5, 111, -63]
         * [FF  01 01 37  E6   00 00  B2  35  0A  3D  05 6F   C1]
         * 甲烷浓度值为79638，计算为79638=0x01*65536+0x37*256+0xE6[0x01为数据码1，0x37为数据码2，0xE6为数据码3]；
         * 激光甲烷设备状态值为0，表示状态正常，[0x00为数据码4]；
         * 激光强度值为45621，计算为45621=0x00*65536+0xB2*256+0x35[0x00为数据码5，0xB2为数据码6，0x35为数据码7];
         */
        val bytes = bytesToUnsigned(data)
        val hashMap = HashMap<String, Any>()
        val methaneBytes = IntArray(3)
        System.arraycopy(bytes, 2, methaneBytes, 0, 3)
        hashMap["methane"] = covertDataValue(methaneBytes)
        hashMap["methaneState"] = covertState(bytes[5])
        val laserBytes = IntArray(3)
        System.arraycopy(bytes, 6, laserBytes, 0, 3)
        hashMap["laser"] = covertDataValue(laserBytes)
        val horizontalBytes = IntArray(2)
        System.arraycopy(bytes, 9, horizontalBytes, 0, 2)
        hashMap["horizontal"] = covertAngleValue(horizontalBytes)
        val verticalBytes = IntArray(2)
        System.arraycopy(bytes, 11, verticalBytes, 0, 2)
        hashMap["vertical"] = covertAngleValue(verticalBytes)

        //{"horizontal":26.21,"laser":-19915,"methaneState":"正常","methane":79590,"vertical":13.91}
        Log.d(kTag, "onMessageResponse ===> " + Gson().toJson(hashMap))
    }

    private fun covertDataValue(bytes: IntArray): Int {
        //数据码1* 65536 + 数据码2 * 256 + 数据码3
        return bytes[0] * 65536 + bytes[1] * 256 + bytes[2]
    }

    private fun covertState(b: Int): String {
        var state = ""
        when (b) {
            0 -> state = "正常"
            1 -> state = "温控故障"
            2 -> state = "激光未打开"
            else -> {}
        }
        return state
    }

    private fun covertAngleValue(bytes: IntArray): Double {
        //首先计算Tangle=[数据码10 * 256 + 数据码11]/100，单位为°，精确到0.01。
        //若Tangle在0~90范围内，则垂直角度值=Tangle；若Tangle在-1~-90范围内，则垂直角度值=Tangle-360)
        val tangle = (bytes[0] * 256 + bytes[1]).toDouble() / 100
        return if (tangle in 0.0..90.0) {
            tangle
        } else {
            tangle - 360
        }
    }

    override fun onServiceStatusConnectChanged(statusCode: Int) {
        if (statusCode == DemoConstant.STATUS_CONNECT_SUCCESS) {
            if (nettyClient.connectStatus) {
                Log.d(kTag, "连接成功")
            }
        } else {
            if (!nettyClient.connectStatus) {
                Log.e(kTag, "onServiceStatusConnectChanged:$statusCode，连接断开，正在重连")
            }
        }
    }

    fun sendData(data: ByteArray) {
        nettyClient.sendData(data)
    }

    fun close() {
        nettyClient.disconnect()
    }

    private fun bytesToUnsigned(data: ByteArray): IntArray {
        val array = IntArray(data.size)
        for (i in data.indices) {
            val datum = data[i]
            val temp = if (datum < 0) {
                0xFF and datum.toInt()
            } else {
                datum.toInt()
            }
            array[i] = temp
        }
        return array
    }
}