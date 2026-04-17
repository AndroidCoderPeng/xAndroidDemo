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

    private val borderPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }

    // 四个边框各自独立的 Path 和 Paint
    private val topBorder = BorderData()
    private val bottomBorder = BorderData()
    private val leftBorder = BorderData()
    private val rightBorder = BorderData()

    private data class BorderData(
        val path: Path = Path(),
        val paint: Paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    )

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
        stroke: Int
    ) {
        val thickness = stroke * audioScale

        // 顶部边框
        topBorder.path.apply {
            reset()
            addRect(0f, 0f, width, thickness, Path.Direction.CW)
        }
        topBorder.paint.shader = LinearGradient(
            0f, 0f, 0f, thickness,
            outerColor, innerColor,
            Shader.TileMode.CLAMP
        )

        // 底部边框
        bottomBorder.path.apply {
            reset()
            addRect(0f, height - thickness, width, height, Path.Direction.CW)
        }
        bottomBorder.paint.shader = LinearGradient(
            0f, height - thickness, 0f, height,
            innerColor, outerColor,
            Shader.TileMode.CLAMP
        )

        // 左边框
        leftBorder.path.apply {
            reset()
            addRect(0f, 0f, thickness, height, Path.Direction.CW)
        }
        leftBorder.paint.shader = LinearGradient(
            0f, 0f, thickness, 0f,
            outerColor, innerColor,
            Shader.TileMode.CLAMP
        )

        // 右边框
        rightBorder.path.apply {
            reset()
            addRect(width - thickness, 0f, width, height, Path.Direction.CW)
        }
        rightBorder.paint.shader = LinearGradient(
            width - thickness, 0f, width, 0f,
            innerColor, outerColor,
            Shader.TileMode.CLAMP
        )

        postInvalidate()
    }

    fun drawPath(data: TimeDomainData, width: Float, height: Float, color: Int, xOffset: Float) {
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
        // 直接绘制缓存的四个边框
        canvas.drawPath(topBorder.path, topBorder.paint)
        canvas.drawPath(bottomBorder.path, bottomBorder.paint)
        canvas.drawPath(leftBorder.path, leftBorder.paint)
        canvas.drawPath(rightBorder.path, rightBorder.paint)

        canvas.drawPath(path, paint)
    }
}