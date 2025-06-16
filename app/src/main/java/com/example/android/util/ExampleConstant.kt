package com.example.android.util

object ExampleConstant {
    val ASK_DEV_CODE_COMMAND = byteArrayOf(0x01, 0x0D, 0x0A) // 查询设备编号命令
    val OPEN_TRANSFER_COMMAND = byteArrayOf(0x02, 0x0D, 0x0A) // 开启数据发送命令
    val CLOSE_TRANSFER_COMMAND = byteArrayOf(0x03, 0x0D, 0x0A) // 关闭数据发送命令

    //主服务UUID
    val UUIDS = listOf(
        "00001801-0000-1000-8000-00805f9b34fb",
        "00001800-0000-1000-8000-00805f9b34fb",
        "0003cdd0-0000-1000-8000-00805f9b0131"
    )

    //海康摄像头参数
    const val HK_NET_IP = "192.168.10.101"
    const val HK_NET_PORT = "8000"
    const val HK_NET_USERNAME = "admin"
    const val HK_NET_PASSWORD = "1234qwer"

    const val MAX_DISTANCE = 5.5f //表盘最大显示距离
}