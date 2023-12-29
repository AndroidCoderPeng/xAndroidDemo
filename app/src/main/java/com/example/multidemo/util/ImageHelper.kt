package com.example.multidemo.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextPaint
import android.text.TextUtils
import com.example.multidemo.base.BaseApplication
import com.example.multidemo.callback.ICompressListener
import com.example.multidemo.callback.IWaterMarkAddListener
import com.pengxh.kt.lite.extensions.timestampToCompleteDate
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

object ImageHelper {
    /**
     * 绘制文字到右下角
     */
    fun drawTextToRightBottom(bitmap: Bitmap, markAddListener: IWaterMarkAddListener) {
        //初始化画笔
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.DEV_KERN_TEXT_FLAG)
        textPaint.typeface = Typeface.DEFAULT // 采用默认的宽度
        textPaint.color = Color.RED
        textPaint.isDither = true // 获取跟清晰的图像采样
        textPaint.isFilterBitmap = true
        textPaint.textSize = 36f
        val timeBounds = Rect()
        val time = System.currentTimeMillis().timestampToCompleteDate()
        textPaint.getTextBounds(time, 0, time.length, timeBounds)

        //添加水印
        val bitmapConfig: Bitmap.Config = bitmap.config
        val copyBitmap: Bitmap = bitmap.copy(bitmapConfig, true)
        val canvas = Canvas(copyBitmap)
        val bitmapWidth: Int = copyBitmap.width
        val bitmapHeight: Int = copyBitmap.height

        //图片像素不一样，间距也需要设置不一样
        val paddingRight: Int = QMUIDisplayHelper.dp2px(BaseApplication.get(), 20)
        val paddingBottom: Int = QMUIDisplayHelper.dp2px(BaseApplication.get(), 20)
        //有几行就写几行
        canvas.drawText(
            time,
            (bitmapWidth - timeBounds.width() - paddingRight).toFloat(),
            (bitmapHeight - paddingBottom).toFloat(),
            textPaint
        )
        //将带有水印的图片保存
        val file = FileUtils.waterImageFile
        val fos = FileOutputStream(file)
        copyBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()
//        markAddListener.onSuccess(file)
    }

    /**
     * 压缩图片
     */
    fun compressImage(imagePath: String, targetDir: String, listener: ICompressListener) {
        Luban.with(BaseApplication.get())
            .load(imagePath)
            .ignoreBy(100)
            .setTargetDir(targetDir)
            .filter { path ->
                !(TextUtils.isEmpty(path) || path.lowercase(Locale.getDefault()).endsWith(".gif"))
            }
            .setCompressListener(object : OnCompressListener {
                override fun onStart() {}
                override fun onSuccess(file: File) {
                    listener.onSuccess(file)
                }

                override fun onError(e: Throwable) {
                    listener.onError(e)
                }
            }).launch()
    }
}