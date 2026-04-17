package com.example.android.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.example.android.model.CanvasPoint
import com.example.android.model.TimeDomainData
import kotlin.math.absoluteValue

class GradientCurveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val borderPath by lazy { Path() }
    private val borderPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }

    private val path by lazy { Path() }
    private val paint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }

    fun drawBorder(audioScale: Float, innerColor: Int, outerColor: Int, stroke: Float) {

    }

    fun draw(data: TimeDomainData, width: Float, height: Float, color: Int, xOffset: Float) {
        val timeAxis = data.timeAxis
        val amplitude = data.amplitude
        val pointCount = timeAxis.size

        var maxAmplitude = 0.0
        for (value in amplitude) {
            val amp = value.absoluteValue
            if (amp > maxAmplitude) {
                maxAmplitude = amp
            }
        }

        // 波峰波谷缩放比例
        val scale = if (maxAmplitude > 0) (height / 2) / maxAmplitude else 0.0

        // 创建点数组
        val pointArray = Array(pointCount) { i ->
            val x = i * width / pointCount + xOffset
            val y = height / 2 - amplitude[i] * scale
            CanvasPoint(x, y.toFloat())
        }

        // 构建Path：移动到起点，然后连线
        path.reset()
        if (pointArray.isNotEmpty()) {
            path.moveTo(pointArray[0].x, pointArray[0].y)
            for (i in 1 until pointArray.size) {
                path.lineTo(pointArray[i].x, pointArray[i].y)
            }
        }

        // 设置画笔
        paint.color = color
        paint.strokeWidth = 2f
        paint.style = Paint.Style.STROKE

        // 刷新
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(borderPath, borderPaint)
        canvas.drawPath(path, paint)
    }
}