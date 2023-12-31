package com.example.multidemo.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextPaint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import com.example.multidemo.annotations.WaterMarkPosition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * 绘制水印
 */
class WaterMarkEngine : LifecycleOwner {

    private val registry = LifecycleRegistry(this)
    private val textPaint by lazy { TextPaint() }
    private val textRect by lazy { Rect() }

    private lateinit var context: Context
    private lateinit var originalBitmap: Bitmap
    private lateinit var marker: String
    private var textColor = Color.WHITE
    private var textSize = 16f
    private var textPadding = 10f
    private lateinit var addedListener: OnWaterMarkerAddedListener
    private var position = WaterMarkPosition.RIGHT_BOTTOM
    private lateinit var fileName: String

    /**
     * 设置上下文
     * */
    fun setContext(context: Context): WaterMarkEngine {
        this.context = context
        return this
    }

    /**
     * 设置原始Bitmap
     * */
    fun setOriginalBitmap(bitmap: Bitmap): WaterMarkEngine {
        this.originalBitmap = bitmap
        return this
    }

    /**
     * 设置水印文字
     * */
    fun setTextMaker(marker: String): WaterMarkEngine {
        this.marker = marker
        return this
    }

    /**
     * 设置水印文字颜色
     * */
    fun setTextColor(textColor: Int): WaterMarkEngine {
        this.textColor = textColor
        return this
    }

    /**
     * 设置水印文字大小
     * */
    fun setTextSize(textSize: Float): WaterMarkEngine {
        this.textSize = textSize
        return this
    }

    /**
     * 设置水印文字位置
     * */
    fun setMarkerPosition(@WaterMarkPosition position: Int): WaterMarkEngine {
        this.position = position
        return this
    }

    /**
     * 设置水印文字距离Bitmap内边距
     * */
    fun setTextPadding(textPadding: Float): WaterMarkEngine {
        this.textPadding = textPadding
        return this
    }

    /**
     * 设置水印图片保存路径
     * */
    fun setMarkedSavePath(fileName: String): WaterMarkEngine {
        this.fileName = fileName
        return this
    }

    /**
     * 设置水印图片回调监听
     * */
    fun setOnWaterMarkerAddedListener(addedListener: OnWaterMarkerAddedListener): WaterMarkEngine {
        this.addedListener = addedListener
        return this
    }

    /**
     * 开始添加水印
     * */
    fun start() {
        addedListener.onStart()
        //初始化画笔
        textPaint.color = textColor
        textPaint.typeface = Typeface.DEFAULT_BOLD
        textPaint.isDither = true // 获取清晰的图像采样
        textPaint.isFilterBitmap = true
        textPaint.textSize = textSize
        textPaint.getTextBounds(marker, 0, marker.length, textRect)

        //添加水印
        val bitmapConfig = originalBitmap.config
        val copyBitmap = originalBitmap.copy(bitmapConfig, true)
        lifecycleScope.launch(Dispatchers.IO) {
            val canvas = Canvas(copyBitmap)
            val bitmapWidth = copyBitmap.width
            val bitmapHeight = copyBitmap.height

            when (position) {
                WaterMarkPosition.LEFT_TOP -> {
                    canvas.drawText(marker, textPadding, textPadding, textPaint)
                }

                WaterMarkPosition.RIGHT_TOP -> {
                    canvas.drawText(
                        marker, bitmapWidth - textRect.width() - textPadding, textPadding, textPaint
                    )
                }

                WaterMarkPosition.LEFT_BOTTOM -> {
                    canvas.drawText(marker, textPadding, bitmapHeight - textPadding, textPaint)
                }

                WaterMarkPosition.RIGHT_BOTTOM -> {
                    canvas.drawText(
                        marker,
                        bitmapWidth - textRect.width() - textPadding, bitmapHeight - textPadding,
                        textPaint
                    )
                }

                WaterMarkPosition.CENTER -> {
                    canvas.drawText(
                        marker,
                        (bitmapWidth - textRect.width()) / 2f, bitmapHeight / 2f,
                        textPaint
                    )
                }
            }

            //编码照片是耗时操作，需要在子线程或者协程里面
            val file = File(fileName)
            val fileOutputStream = FileOutputStream(file)
            /**
             * 第一个参数如果是Bitmap.CompressFormat.PNG,那不管第二个值如何变化，图片大小都不会变化，不支持png图片的压缩
             * 第二个参数是压缩比重，图片存储在磁盘上的大小会根据这个值变化。值越小存储在磁盘的图片文件越小
             * */
            copyBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()

            withContext(Dispatchers.Main) {
                addedListener.onMarkAdded(file)
            }
        }
    }

    override fun getLifecycle(): Lifecycle {
        return registry
    }

    interface OnWaterMarkerAddedListener {
        fun onStart()

        fun onMarkAdded(file: File)
    }
}