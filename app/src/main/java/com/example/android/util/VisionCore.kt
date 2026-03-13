package com.example.android.util

import java.nio.ByteBuffer

/**
 * 图形图像处理Native库
 * */
object VisionCore {
    init {
        System.loadLibrary("imgproc")
    }

    /**
     * 旋转摄像头数据
     *
     * @param input     NV21数据
     * @param width     宽
     * @param height    高
     * @param rotation  旋转角度
     * @param output    旋转后的数据
     */
    external fun rotateYuv(
        input: ByteBuffer,
        width: Int,
        height: Int,
        rotation: Int,
        output: ByteBuffer
    )
}