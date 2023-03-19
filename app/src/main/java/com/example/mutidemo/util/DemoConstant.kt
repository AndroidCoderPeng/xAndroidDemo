package com.example.mutidemo.util

object DemoConstant {
    const val BASE_URL = "https://way.jd.com"
    const val APP_KEY = "e957ed7ad90436a57e604127d9d8fa32"
    val ASK_DEV_CODE_COMMAND = byteArrayOf(0x01, 0x0D, 0x0A) // 查询设备编号命令
    val OPEN_TRANSFER_COMMAND = byteArrayOf(0x02, 0x0D, 0x0A) // 开启数据发送命令
    const val SERVICE_UUID = "0003cdd0-0000-1000-8000-00805f9b0131" //连接设备的UUID
    const val WRITE_CHARACTERISTIC_UUID = "0003cdd2-0000-1000-8000-00805f9b0131" //写数据特征值UUID
    const val READ_CHARACTERISTIC_UUID = "0003cdd1-0000-1000-8000-00805f9b0131" //读数据特征值UUID
    val images = listOf(
        "https://images.pexels.com/photos/1036808/pexels-photo-1036808.jpeg",
        "https://images.pexels.com/photos/796602/pexels-photo-796602.jpeg",
        "https://images.pexels.com/photos/1109543/pexels-photo-1109543.jpeg",
        "https://images.pexels.com/photos/296115/pexels-photo-296115.jpeg",
        "https://images.pexels.com/photos/4158/apple-iphone-smartphone-desk.jpg"
    )
    const val HOST = "192.168.31.14"
    const val TCP_PORT = 8000

    const val STATUS_CONNECT_SUCCESS = 1 //连接成功
    const val STATUS_CONNECT_CLOSED = 0 //关闭连接
    const val STATUS_CONNECT_ERROR = 0 //连接失败
}