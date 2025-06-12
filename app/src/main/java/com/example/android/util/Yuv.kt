package com.example.android.util

object Yuv {
    init {
        System.loadLibrary("yuv")
    }

    /**
     * 旋转 NV21 数据
     * @param input 原始 NV21 数据
     * @param width 图像宽度
     * @param height 图像高度
     * @param rotation 需要旋转的角度，比如，画面被顺时针旋转90度，那么nv21就需要逆时针旋转90度才能正常显示（0, 90, 180, 270）
     * @return 旋转后的 NV21 数据
     */
    external fun rotate(input: ByteArray, width: Int, height: Int, rotation: Int): ByteArray
}