package com.example.multidemo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import com.example.multidemo.util.ColorRender
import com.pengxh.kt.lite.extensions.getScreenHeight


class AudioVisualView constructor(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val kTag = "AudioVisualView"
    private var fftArray = ByteArray(1024)
    private val wavePaint = Paint()
    private val spectrumPaint = Paint()
    private var wavePath = Path()
    private var hsvColor: IntArray

    /**
     * 频谱数量
     * */
    private val spectrumCount = 128
    private var pointArray = ArrayList<Point>()

    init {
        wavePaint.isAntiAlias = true
        wavePaint.strokeWidth = 2f
        wavePaint.style = Paint.Style.STROKE

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
        val waveArray = ByteArray(1024)
        waveBytes.forEachIndexed { index, it ->
            val byte = if (it < 0) {
                (-it).toByte()
            } else {
                it
            }
            waveArray[index] = byte
        }

        //每次设置数据需要清空之前的数据，不然会覆盖原先的折现
        if (pointArray.isNotEmpty()) {
            pointArray.clear()
        }
        waveArray.forEachIndexed { index, byte ->
            val point = Point(8 * (index + 1), byte.toInt())
            pointArray.add(point)
        }
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

        //绘制音频波形曲线
        drawAudioWave(canvas)

        //绘制快速傅里叶变换之后的数据
        drawFFTSpectrum(canvas)
    }

    private fun drawAudioWave(canvas: Canvas) {
        wavePath.reset()
        //每次刷新界面都用不同的颜色
        colorIndex++
        wavePaint.color = hsvColor[colorIndex % hsvColor.size]

        for (i in 0 until pointArray.size - 1) {
            val point = pointArray[i]
            val nextPoint = pointArray[i + 1]
            val midX = (point.x + nextPoint.x) / 2
            if (i == 0) {
                wavePath.moveTo(point.x.toFloat(), point.y.toFloat())
            }

            //通过贝塞尔曲线绘制，波形更形象，更平滑
            wavePath.cubicTo(
                midX.toFloat(),
                point.y.toFloat(),
                midX.toFloat(),
                nextPoint.y.toFloat(),
                nextPoint.x.toFloat(),
                nextPoint.y.toFloat()
            )
        }

        canvas.drawPath(wavePath, wavePaint)
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