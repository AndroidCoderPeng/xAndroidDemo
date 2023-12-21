package com.example.multidemo.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.roundToInt


private const val BITMAP_SCALE = 0.4f

fun Drawable.toBlurBitmap(context: Context, radius: Float): Bitmap {
    val bitmap = this.toBitmap()

    // 计算图片缩小后的长宽
    val width = (bitmap.width * BITMAP_SCALE).roundToInt()
    val height = (bitmap.height * BITMAP_SCALE).roundToInt()

    // 将缩小后的图片做为预渲染的图片。
    val inputBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
    // 创建一张渲染后的输出图片。
    val outputBitmap = Bitmap.createBitmap(inputBitmap)

    // 创建RenderScript内核对象
    val rs = RenderScript.create(context)
    // 创建一个模糊效果的RenderScript的工具对象
    val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
    val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
    val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
    blurScript.setRadius(radius)
    blurScript.setInput(tmpIn)
    blurScript.forEach(tmpOut)
    tmpOut.copyTo(outputBitmap)
    return outputBitmap
}