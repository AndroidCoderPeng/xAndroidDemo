package com.example.multidemo.util.netty

import android.util.Log
import com.example.multidemo.MainActivity
import com.example.multidemo.util.DemoConstant

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
        val message = MainActivity.weakReferenceHandler.obtainMessage()
        message.what = 20231101
        message.obj = data.contentToString()
        MainActivity.weakReferenceHandler.sendMessage(message)
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
}