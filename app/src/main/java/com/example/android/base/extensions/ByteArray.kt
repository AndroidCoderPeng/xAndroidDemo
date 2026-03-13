package com.example.android.base.extensions

import java.util.Locale

/**
 * 2进制to 16进制
 * @return
 */
fun ByteArray.toHex(): String? {
    val sb = StringBuffer()
    if (this.isEmpty()) {
        return null
    }
    var sTemp: String
    for (i in this.indices) {
        sTemp = Integer.toHexString(0xFF and this[i].toInt())
        if (sTemp.length < 2) {
            sb.append(0)
        }
        sb.append(sTemp.uppercase(Locale.getDefault()))
    }
    return sb.toString()
}

/**
 * 将 NV21 转换为 NV12
 *
 * @suppress libyuv 的价值在于计算密集型操作，而不是简单的数据交换，NV21 转 NV12 没必要使用libyuv
 *
 * @param width 宽
 * @param height 高
 * @param dst 目标NV12缓冲区
 * */
fun ByteArray.toNV12(width: Int, height: Int, dst: ByteArray) {
    require(width > 0 && height > 0) { "width and height must be positive, got ${width}x${height}" }

    val ySize = width * height
    val uvSize = ySize / 2
    val totalSize = ySize + uvSize

    require(this.size >= totalSize) {
        "Source NV21 buffer size ${this.size} is too small, need at least $totalSize"
    }
    require(dst.size >= totalSize) {
        "Destination NV12 buffer size ${dst.size} is too small, need at least $totalSize"
    }

    // 复制 Y 分量（NV21 和 NV12 完全相同）
    System.arraycopy(this, 0, dst, 0, ySize)

    // 转换 UV 分量：NV21 是 VU 交替，NV12 是 UV 交替
    for (i in ySize until totalSize step 2) {
        dst[ySize + (i - ySize)] = this[i + 1]     // U (原 NV21 的第二个字节)
        dst[ySize + (i - ySize) + 1] = this[i]     // V (原 NV21 的第一个字节)
    }
}