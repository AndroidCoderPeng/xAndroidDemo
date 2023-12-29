package com.example.multidemo.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.text.TextPaint
import com.example.multidemo.callback.IWaterMarkAddListener
import com.example.multidemo.enums.WaterMarkPosition
import com.pengxh.kt.lite.extensions.timestampToCompleteDate

fun Bitmap.addWaterMark(
    textPaint: TextPaint, position: WaterMarkPosition, listener: IWaterMarkAddListener
) {
    val textBoundsRect = Rect()
    val time = System.currentTimeMillis().timestampToCompleteDate()
    textPaint.getTextBounds(time, 0, time.length, textBoundsRect)

    //添加水印
    val bitmapConfig = this.config
    val copyBitmap = this.copy(bitmapConfig, true)
    val canvas = Canvas(copyBitmap)
    val bitmapWidth = copyBitmap.width
    val bitmapHeight = copyBitmap.height

    when (position) {
        WaterMarkPosition.LEFT_TOP -> {}
        WaterMarkPosition.RIGHT_TOP -> {}
        WaterMarkPosition.LEFT_BOTTOM -> {}
        WaterMarkPosition.RIGHT_BOTTOM -> {}
        WaterMarkPosition.CENTER -> {}
    }
    canvas.drawText(
        time,
        0f,
        0f,
        textPaint
    )

    listener.onSuccess(copyBitmap)
}