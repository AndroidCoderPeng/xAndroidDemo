package com.example.android.widget

import android.content.Context
import android.graphics.Canvas
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

class GradientStripView @JvmOverloads constructor(
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
     * 绘制渐变条
     *
     * @param data 频域数据
     * @param width 绘制频谱图的视图宽度
     * @param height 绘制频谱图的视图高度
     * @param bottomColor 渐变条底色
     * @param topColor 渐变条顶色
     * @param xOffset X轴偏移量
     * @param spacing 频域数据之间的间隔
     */
    fun drawPath(
        data: FrequencyDomainData,
        width: Float,
        height: Float,
        bottomColor: Int,
        topColor: Int,
        xOffset: Float,
        spacing: Float
    ) {
        // 后续如果需要更专业的频谱分布方式，再考虑引入 frequencies。
        val frequencies = data.frequencies
        val magnitudes = data.magnitudes
        val stripCount = magnitudes.size

        //竖条宽度
        val stripWidth = (width - spacing * stripCount) / stripCount

        val linearGradient = LinearGradient(
            0f, 0f,           // 起点：左上角
            0f, height,       // 终点：左下角（垂直向下）
            topColor,         // 起点颜色（对应Y=0，即顶部）
            bottomColor,      // 终点颜色（对应Y=height，即底部）
            Shader.TileMode.CLAMP
        )
        paint.shader = linearGradient

        var maxMagnitude = 0.0
        for (mag in magnitudes) {
            val magnitude = mag.absoluteValue
            if (magnitude > maxMagnitude) {
                maxMagnitude = magnitude
            }
        }

        // 竖条高度缩放比例
        val scale = if (maxMagnitude > 0) height / maxMagnitude else 0.0

        // 创建点数组
        val pointArray = Array(stripCount) { i ->
            val x = stripWidth * i + spacing * i + xOffset

            // 计算每个竖条的高度，但是不能超过绘制频谱图的视图高度
            val y = magnitudes[i].absoluteValue * scale
            CanvasPoint(x, y.toFloat())
        }

        // 找到最小高度作为基准线
        val minHeight = pointArray.minOf { it.y.absoluteValue }

        // 重置Path
        path.reset()

        pointArray.forEach { point ->
            val absY = point.y.absoluteValue

            // 只绘制超出基准线的部分
            val stripHeight = absY - minHeight

            // Y轴翻转：Android 坐标系Y向下为正，竖条应从底部向上绘制
            val yBase = height
            val stripTopY = yBase - stripHeight

            //每根竖条的四个角坐标。圆点在视图的左上角
            val endPoints = arrayOf(
                PointF(point.x, yBase), //左下角
                PointF(point.x, stripTopY), //左上角
                PointF(point.x + stripWidth, stripTopY), //右上角
                PointF(point.x + stripWidth, yBase) //右下角
            )

            // 构建PathFigure：移动到起点，然后连线
            path.moveTo(endPoints[0].x, endPoints[0].y)
            for (i in 1 until endPoints.size) {
                path.lineTo(endPoints[i].x, endPoints[i].y)
            }
            path.close()

            // 刷新
            postInvalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }
}