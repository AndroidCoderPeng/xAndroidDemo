package com.example.multidemo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.pengxh.kt.lite.extensions.getScreenHeight
import kotlin.math.abs


class AudioVisualView constructor(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val kTag = "AudioVisualView"
    private val bytes = ByteArray(256)
    private val spectrumPaint = Paint()

    /**
     * 频谱数量
     * */
    private val spectrumCount = 256

    init {
        spectrumPaint.isAntiAlias = true
//        lumpPaint.setColor(LUMP_COLOR)
        spectrumPaint.strokeWidth = 1f
        spectrumPaint.style = Paint.Style.STROKE
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

    /**
     * 更新音频波形-波形数据
     * */
    fun updateAudioWaveform(waveBytes: ByteArray) {

        postInvalidate()
    }

    /**
     * 更新音频振幅-快速傅里叶数据
     * */
    fun updateAudioAmplitude(fft: ByteArray) {
        var byte: Byte
        for (i in 0 until 256) {
            byte = abs(fft[i].toInt()).toByte()
            //visualizer 回调中的数据中是存在负数的，需要转换一下，用于显示
            bytes[i] = if (byte < 0) 127 else byte
        }

        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //背景黑色
        canvas.drawColor(Color.BLACK)

        //频谱图每根矩形的宽度
        val rectWidth = width / spectrumCount
        val rectHeight = height.toFloat()
        val scale = rectHeight / spectrumCount
        Log.d(kTag, "onDraw: [$width, $rectWidth, $rectHeight, $scale]")

        for (i in 0 until spectrumCount) {
            val top = rectHeight - (rectMinHeight + bytes[i] * scale)
            canvas.drawRect(
                (rectWidth * i).toFloat(),
                top,
                (rectWidth * i).toFloat() + rectWidth,
                rectHeight,
                spectrumPaint
            )
        }
    }

    private val rectMinHeight = 1
}