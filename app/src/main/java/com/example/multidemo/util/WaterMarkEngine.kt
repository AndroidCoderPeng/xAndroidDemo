package com.example.multidemo.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.text.TextPaint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import com.example.multidemo.enums.WaterMarkPosition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 绘制水印
 */
class WaterMarkEngine : LifecycleOwner {

    private val registry = LifecycleRegistry(this)
    private val textPaint by lazy { TextPaint() }
    private val textRect by lazy { Rect() }

    private lateinit var context: Context

    /**
     * 原始Bitmap
     * */
    private lateinit var originalBitmap: Bitmap
    private lateinit var marker: String
    private var textColor = Color.WHITE
    private var textSize = 16f
    private lateinit var addedListener: OnWaterMarkAddedListener
    private lateinit var position: WaterMarkPosition

    fun setContext(context: Context): WaterMarkEngine {
        this.context = context
        return this
    }

    fun setOriginalBitmap(bitmap: Bitmap): WaterMarkEngine {
        this.originalBitmap = bitmap
        return this
    }

    fun setTextMaker(marker: String): WaterMarkEngine {
        this.marker = marker
        return this
    }

    fun setTextColor(textColor: Int): WaterMarkEngine {
        this.textColor = textColor
        return this
    }

    fun setTextSize(textSize: Float): WaterMarkEngine {
        this.textSize = textSize
        return this
    }

    fun setMarkerPosition(position: WaterMarkPosition): WaterMarkEngine {
        this.position = position
        return this
    }

    fun setOnWaterMarkAddedListener(addedListener: OnWaterMarkAddedListener): WaterMarkEngine {
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
                WaterMarkPosition.LEFT_TOP -> {}
                WaterMarkPosition.RIGHT_TOP -> {
                    canvas.drawText(
                        marker,
                        0f,
                        0f,
                        textPaint
                    )
                }

                WaterMarkPosition.LEFT_BOTTOM -> {}
                WaterMarkPosition.RIGHT_BOTTOM -> {}
                WaterMarkPosition.CENTER -> {}
            }

            withContext(Dispatchers.Main) {
                addedListener.onMarkAdded(copyBitmap)
            }
        }
    }

    override fun getLifecycle(): Lifecycle {
        return registry
    }

    interface OnWaterMarkAddedListener {
        fun onStart()

        fun onMarkAdded(bitmap: Bitmap)
    }
}