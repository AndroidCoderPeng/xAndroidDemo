package com.example.android.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import com.example.android.model.CanvasPoint
import com.example.android.model.FrequencyDomainData
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

/**
 * 根据Android自定义View的标准实践，即使没有自定义属性（attr），也应该实现4个构造函数
 * 但是可以使用Kotlin的 @JvmOverloads 注解简化
 *
 * | 构造函数 | 调用场景 |
 * |:----:|:----:|
 * | context | 代码中动态创建View |
 * | Context, AttributeSet? | XML布局中使用（最常见） |
 * | + defStyleAttr | 应用主题样式 |
 * | + defStyleRes | 特定样式资源 |
 *
 */
class CircularGradientStripView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val path by lazy { Path() }
    private val paint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }

    /**
     * 绘制圆形渐变条
     *
     * @param data 频域数据
     * @param height 绘制频谱图的视图高度
     * @param innerColor 内环颜色
     * @param outerColor 外环颜色
     * @param xOffset X轴偏移量
     * @param yOffset Y轴偏移量
     * @param radius 圆半径
     * @param spacing 频域数据之间的间隔
     * @param rotation 旋转角度
     */
    fun drawPath(
        data: FrequencyDomainData,
        height: Float,
        innerColor: Int,
        outerColor: Int,
        xOffset: Float,
        yOffset: Float,
        radius: Float,
        spacing: Float,
        rotation: Float
    ) {
        val frequencies = data.frequencies
        val magnitudes = data.magnitudes
        val stripCount = magnitudes.size

        //旋转角度转弧度
        val rotationRadian = Math.PI / 180 * rotation

        //等分圆周，每个（竖条+空白）对应的弧度
        val blockRadian = Math.PI * 2 / stripCount

        //每个空隙对应的弧度
        val spacingRadian = Math.PI / 180 * spacing

        //每个竖条对应的弧度
        val stripRadian = blockRadian - spacingRadian

        var maxMagnitude = 0.0
        for (mag in magnitudes) {
            val magnitude = mag.absoluteValue
            if (magnitude > maxMagnitude) {
                maxMagnitude = magnitude
            }
        }

        // 计算去掉圆环还剩下多少高度空间
        val remainingHeight = (height - radius * 2) / 2

        // 竖条高度缩放比例
        val scale = if (maxMagnitude > 0) remainingHeight / maxMagnitude else 0.0

        // 创建点数组
        val pointArray = Array(stripCount) { i ->
            val x = blockRadian * i + rotationRadian // 弧度
            val y = magnitudes[i].absoluteValue * scale * 0.5 // 弧度所对应的竖条的高度, 缩放到 50%
            CanvasPoint(x.toFloat(), y.toFloat())
        }

        val maxHeight = pointArray.maxOf { it.y.absoluteValue }

        val colors = intArrayOf(
            Color.TRANSPARENT,
            innerColor,
            outerColor
        )

        // 渐变位置：0 -> radius/(radius+maxHeight) -> 1
        val positions = floatArrayOf(
            0f,
            (radius / (radius + maxHeight)),
            1f
        )

        // 创建线性渐变：从 (xOffset, 0) 到 (xOffset, 1) 的垂直渐变
        val linearGradient = LinearGradient(
            xOffset, 0f,           // 起点
            xOffset, height,        // 终点（垂直方向）
            colors,
            positions,
            Shader.TileMode.CLAMP
        )
        paint.shader = linearGradient

        // 重置路径
        path.reset()

        // 绘制多边形
        pointArray.forEach { point ->
            val sinStart = sin(point.x)
            val sinEnd = sin(point.x + stripRadian)
            val cosStart = cos(point.x)
            val cosEnd = cos(point.x + stripRadian)

            val polygon = arrayOf(
                PointF(
                    cosStart * radius + xOffset,
                    sinStart * radius + yOffset
                ),
                PointF(
                    (cosEnd * radius + xOffset).toFloat(),
                    (sinEnd * radius + yOffset).toFloat()
                ),
                PointF(
                    (cosEnd * (radius + point.y) + xOffset).toFloat(),
                    (sinEnd * (radius + point.y) + yOffset).toFloat()
                ),
                PointF(
                    cosStart * (radius + point.y) + xOffset,
                    sinStart * (radius + point.y) + yOffset
                )
            )

            // 绘制多边形
            path.moveTo(polygon[0].x, polygon[0].y)
            for (i in 1 until polygon.size) {
                path.lineTo(polygon[i].x, polygon[i].y)
            }
            path.close()
        }

        // 刷新
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }
}