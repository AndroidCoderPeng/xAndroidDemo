package com.example.android.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
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

    fun drawBorder(
        audioScale: Float,
        width: Float,
        height: Float,
        innerColor: Int,
        outerColor: Int,
        stroke: Float
    ) {
        // 边框粗细根据音频高低音变化
        val thickness = (stroke * audioScale).toInt()

        // 重置边框Path
        borderPath.reset()

        // 顶部边框：从左到右，渐变从outer到inner（垂直渐变，角度90度对应从上往下）
        val topGradient = LinearGradient(
            0f, 0f, 0f, thickness.toFloat(),
            outerColor, innerColor,
            Shader.TileMode.CLAMP
        )
        borderPaint.shader = topGradient
        borderPath.addRect(0f, 0f, width, thickness.toFloat(), Path.Direction.CW)

        // 底部边框：从左到右，渐变从inner到outer
        val bottomGradient = LinearGradient(
            0f, height - thickness,
            0f, height,
            innerColor,
            outerColor,
            Shader.TileMode.CLAMP
        )
        borderPaint.shader = bottomGradient
        borderPath.addRect(0f, height - thickness, width, height, Path.Direction.CW)

        // 左边框：从上到下，渐变从outer到inner（水平渐变，角度0度对应从左往右）
        val leftGradient = LinearGradient(
            0f, 0f,
            thickness.toFloat(), 0f,
            outerColor,
            innerColor,
            Shader.TileMode.CLAMP
        )
        borderPaint.shader = leftGradient
        borderPath.addRect(0f, 0f, thickness.toFloat(), height, Path.Direction.CW)

        // 右边框：从上到下，渐变从inner到outer
        val rightGradient = LinearGradient(
            width - thickness, 0f,
            width, 0f,
            innerColor,
            outerColor,
            Shader.TileMode.CLAMP
        )
        borderPaint.shader = rightGradient
        borderPath.addRect(width - thickness, 0f, width, height, Path.Direction.CW)

        // 刷新
        postInvalidate()
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