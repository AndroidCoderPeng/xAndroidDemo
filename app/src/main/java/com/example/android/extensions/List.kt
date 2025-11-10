package com.example.android.extensions

import android.hardware.Camera
import android.util.Log
import kotlin.math.abs

private const val kTag = "Extensions"

/**
 * 获取最优的预览尺寸
 *
 * 该函数通过遍历相机支持的预览尺寸列表，找到与目标宽高比最接近且高度差最小的尺寸
 *
 * @param w 目标宽度
 * @param h 目标高度
 * @return 最优的预览尺寸，如果找不到合适的尺寸或输入为空则返回null
 */
fun List<Camera.Size>.selectOptimalPreviewSize(w: Int, h: Int): Camera.Size? {
    val aspect = 0.1
    val targetRatio = h.toDouble() / w

    Log.d(kTag, "Supported preview sizes:")
    forEach {
        Log.d(kTag, "  ${it.width}x${it.height}")
    }

    var optimalSize: Camera.Size? = null
    var minDiff = Double.MAX_VALUE

    for (size in this) {
        val ratio = size.height.toDouble() / size.width
        if (abs(ratio - targetRatio) > aspect) continue
        if (abs(size.height - h) < minDiff) {
            optimalSize = size
            minDiff = abs(size.height - h).toDouble()
        }
    }

    // 打印选中的最优尺寸
    optimalSize?.let {
        Log.d(kTag, "Selected optimal size: ${it.width}x${it.height}")
    }

    return optimalSize
}