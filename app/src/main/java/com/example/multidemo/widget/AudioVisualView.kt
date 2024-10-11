package com.example.multidemo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import com.example.multidemo.util.ColorRender
import com.pengxh.kt.lite.extensions.getScreenHeight


class AudioVisualView constructor(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val kTag = "AudioVisualView"
    private var fftArray = ByteArray(1024)
    private val spectrumPaint = Paint()
    private var hsvColor: IntArray

    /**
     * 频谱数量
     * */
    private val spectrumCount = 128

    init {
        spectrumPaint.isAntiAlias = true
        spectrumPaint.style = Paint.Style.FILL
        hsvColor = ColorRender.getHsvColor()
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
        fft.forEachIndexed { index, it ->
            val byte = if (it < 0) {
                (-it).toByte()
            } else {
                it
            }
            this.fftArray[index] = byte
        }
        postInvalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //背景黑色
        canvas.drawColor(Color.BLACK)

        //绘制快速傅里叶变换之后的数据
        drawFFTSpectrum(canvas)
    }

    private val rectMinHeight = 1
    private var colorIndex = 0

    private fun drawFFTSpectrum(canvas: Canvas) {
        //频谱图每根矩形的宽度
        val rectWidth = width.toFloat() / spectrumCount
        val rectHeight = height.toFloat()
        val scale = rectHeight / spectrumCount

        //每次刷新界面都用不同的颜色
        colorIndex++
        val color1 = hsvColor[colorIndex % hsvColor.size]
        val color2 = hsvColor[(colorIndex + 200) % hsvColor.size]

        val linearGradient = LinearGradient(
            rectWidth, rectHeight, 0f, 0f, color1, color2, Shader.TileMode.CLAMP
        )
        spectrumPaint.shader = linearGradient

        for (i in 0 until spectrumCount) {
            val top = rectHeight - (rectMinHeight + fftArray[i] * scale)
            canvas.drawRect(
                rectWidth * i,
                top,
                rectWidth * i + rectWidth,
                rectHeight,
                spectrumPaint
            )
        }
    }
}