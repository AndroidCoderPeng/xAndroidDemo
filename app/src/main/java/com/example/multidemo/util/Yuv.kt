package com.example.multidemo.util

object Yuv {
    init {
        System.loadLibrary("yuv")
    }

    /**
     * 旋转 NV21 数据
     * @param input 原始 NV21 数据
     * @param width 图像宽度
     * @param height 图像高度
     * @param rotation 旋转角度（0, 90, 180, 270）
     * @return 旋转后的 NV21 数据
     */
    external fun rotate(input: ByteArray, width: Int, height: Int, rotation: Int): ByteArray
}