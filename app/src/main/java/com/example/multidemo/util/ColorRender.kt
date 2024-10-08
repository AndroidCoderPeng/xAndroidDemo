package com.example.multidemo.util

import android.graphics.Color

object ColorRender {
    /**
     * 获取渐变颜色 (HSV 颜色)
     * */
    fun getHsvColor(): IntArray {
        val color = IntArray(256 * 6)
        for (i in 0..255) {
            color[i] = Color.argb(255, 255, i, 0)
        }

        for (i in 0..255) {
            color[256 + i] = Color.argb(255, 255 - i, 255, 0)
        }

        for (i in 0..255) {
            color[512 + i] = Color.argb(255, 0, 255, i)
        }

        for (i in 0..255) {
            color[768 + i] = Color.argb(255, 0, 255 - i, 255)
        }

        for (i in 0..255) {
            color[1024 + i] = Color.argb(255, i, 0, 255)
        }

        for (i in 0..255) {
            color[1280 + i] = Color.argb(255, 255, 0, 255 - i)
        }
        return color
    }
}