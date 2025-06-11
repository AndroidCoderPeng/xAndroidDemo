package com.example.android.util

object DemoConstant {
    const val APP_KEY = "32736cbe845d7a70"
    val ASK_DEV_CODE_COMMAND = byteArrayOf(0x01, 0x0D, 0x0A) // 查询设备编号命令
    val OPEN_TRANSFER_COMMAND = byteArrayOf(0x02, 0x0D, 0x0A) // 开启数据发送命令
    val CLOSE_TRANSFER_COMMAND = byteArrayOf(0x03, 0x0D, 0x0A) // 关闭数据发送命令

    //主服务UUID
    val UUIDS = listOf(
        "00001801-0000-1000-8000-00805f9b34fb",
        "00001800-0000-1000-8000-00805f9b34fb",
        "0003cdd0-0000-1000-8000-00805f9b0131"
    )

    //可通知不可写
    val SUB_0_UUIDS = listOf(
        "00002a05-0000-1000-8000-00805f9b34fb"
    )

    //可写不可通知
    val SUB_1_UUIDS = listOf(
        "00002a00-0000-1000-8000-00805f9b34fb",
        "00002a01-0000-1000-8000-00805f9b34fb",
        "00002a04-0000-1000-8000-00805f9b34fb"
    )

    //可写可通知
    val SUB_2_UUIDS = listOf(
        "0003cdd1-0000-1000-8000-00805f9b0131",//可通知
        "0003cdd2-0000-1000-8000-00805f9b0131"//可写
    )

    val images = listOf(
        "https://images.pexels.com/photos/1036808/pexels-photo-1036808.jpeg",
        "https://images.pexels.com/photos/796602/pexels-photo-796602.jpeg",
        "https://images.pexels.com/photos/1109543/pexels-photo-1109543.jpeg",
        "https://images.pexels.com/photos/296115/pexels-photo-296115.jpeg",
        "https://images.pexels.com/photos/4158/apple-iphone-smartphone-desk.jpg"
    )

    //海康摄像头参数
    const val HK_NET_IP = "192.168.10.101"
    const val HK_NET_PORT = "8000"
    const val HK_NET_USERNAME = "admin"
    const val HK_NET_PASSWORD = "1234qwer"

    const val MAX_DISTANCE = 5.5f //表盘最大显示距离
}