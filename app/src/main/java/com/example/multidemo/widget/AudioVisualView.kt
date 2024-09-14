package com.example.multidemo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import com.pengxh.kt.lite.extensions.getScreenHeight
import kotlin.math.abs

class AudioVisualView constructor(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val kTag = "AudioVisualView"

    init {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        // 获取高
        val mHeight: Int = if (heightSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            heightSpecSize
        } else {
            // wrap_content，外边界高
            (context.getScreenHeight() * 0.3).toInt()
        }
        // 设置该view的宽高
        setMeasuredDimension(widthMeasureSpec, mHeight)
    }

    fun updateVisualizer(waveBytes: ByteArray) {

        postInvalidate()
    }

    fun updateVisualizerByFFT(fft: ByteArray) {
        //visualizer 回调中的数据中是存在负数的，需要转换一下，用于显示
        val bytes = ByteArray(256)
        var b: Byte
        for (i in 0 until 256) {
            b = abs(fft[i].toInt()).toByte()
            //描述：Math.abs -128时越界
            bytes[i] = if (b < 0) 127 else b
        }

        postInvalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //背景黑色
        canvas?.drawColor(Color.BLACK)
    }
}