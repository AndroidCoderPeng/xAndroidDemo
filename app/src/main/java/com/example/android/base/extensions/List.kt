package com.example.android.base.extensions

import android.hardware.Camera
import android.util.Log
import kotlin.math.abs

private const val kTag = "Extensions"

/**
 * 获取最优的预览尺寸
 *
 * 该函数通过遍历相机支持的预览尺寸列表，找到与目标宽高比最接近且高度差最小的尺寸
 *
 * @param width 目标宽度
 * @param height 目标高度
 * @return 最优的预览尺寸，如果找不到合适的尺寸或输入为空则返回null
 */
fun List<Camera.Size>.chooseOptimalSize(width: Int, height: Int): Camera.Size {
    if (height == 0) {
        throw IllegalArgumentException("Height cannot be zero")
    }

    val widthHeight = width.toDouble() / height.toDouble()
    val heightWidth = if (widthHeight > 1) widthHeight else 1.0 / widthHeight
    Log.d(kTag, "targetSize: [$width, $height], w/h: $widthHeight, h/w: $heightWidth")

    var optimalSize: Camera.Size? = null
    var minDiff = Double.MAX_VALUE

    // 遍历所有支持的尺寸，找到最匹配的
    forEach {
        val sizeRatio = it.width.toDouble() / it.height.toDouble()
        val adjustedRatio = if (sizeRatio > 1) sizeRatio else 1.0 / sizeRatio
        Log.d(kTag, "size: [${it.width}, ${it.height}], w/h: $sizeRatio, h/w: $adjustedRatio")

        // 如果宽高能满足要求，则返回该尺寸
        if (it.width == width && it.height == height || it.width == height && it.height == width) {
            optimalSize = it
            return@forEach
        }

        if (abs(adjustedRatio - heightWidth) < minDiff) {
            optimalSize = it
            minDiff = abs(adjustedRatio - heightWidth)
        }
    }
    val result = optimalSize ?: this[0]
    Log.d(kTag, "optimalSize: [${result.width}, ${result.height}]")
    return result
}