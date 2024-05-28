package com.example.multidemo.util

object DemoConstant {
    const val APP_KEY = "32736cbe845d7a70"
    val ASK_DEV_CODE_COMMAND = byteArrayOf(0x01, 0x0D, 0x0A) // 查询设备编号命令
    val OPEN_TRANSFER_COMMAND = byteArrayOf(0x02, 0x0D, 0x0A) // 开启数据发送命令
    const val SERVICE_UUID = "0003cdd0-0000-1000-8000-00805f9b0131" //连接设备的UUID
    val images = listOf(
        "https://images.pexels.com/photos/1036808/pexels-photo-1036808.jpeg",
        "https://images.pexels.com/photos/796602/pexels-photo-796602.jpeg",
        "https://images.pexels.com/photos/1109543/pexels-photo-1109543.jpeg",
        "https://images.pexels.com/photos/296115/pexels-photo-296115.jpeg",
        "https://images.pexels.com/photos/4158/apple-iphone-smartphone-desk.jpg"
    )
    const val HOST = "192.168.16.213"
    const val TCP_PORT = 8000

    //海康摄像头参数
    const val HK_NET_IP = "192.168.10.101"
    const val HK_NET_PORT = "8000"
    const val HK_NET_USERNAME = "admin"
    const val HK_NET_PASSWORD = "1234qwer"
}