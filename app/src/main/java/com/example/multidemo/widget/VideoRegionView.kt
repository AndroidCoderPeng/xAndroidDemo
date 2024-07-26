package com.example.multidemo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.getScreenHeight
import com.pengxh.kt.lite.extensions.getScreenWidth
import kotlin.math.pow
import kotlin.math.sqrt


class VideoRegionView(private val ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val vertexPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val vertices = listOf(
        PointF(500f, 500f),  // 顶点1
        PointF(1000f, 500f),  // 顶点2
        PointF(1000f, 1000f),  // 顶点3
        PointF(500f, 1000f)   // 顶点4
    )

    init {
        borderPaint.color = Color.GREEN
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeJoin = Paint.Join.ROUND
        borderPaint.strokeWidth = 2f.dp2px(ctx)
        borderPaint.isAntiAlias = true

        vertexPaint.color = Color.RED
        vertexPaint.style = Paint.Style.FILL
        vertexPaint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val path = Path()
        path.moveTo(vertices[0].x, vertices[0].y)
        for (i in vertices.indices) {
            // 绘制四边形
            path.lineTo(vertices[i].x, vertices[i].y)

            //绘制四个顶点
            canvas.drawCircle(vertices[i].x, vertices[i].y, 5f.dp2px(ctx), vertexPaint)
        }
        path.close()
        canvas.drawPath(path, borderPaint)
    }

    private var dragVertex: PointF? = null // 当前被拖动的顶点
    private var dragOffsetX = 0f // 拖动偏移量
    private var dragOffsetY = 0f // 拖动偏移量

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                dragVertex = findClosestVertex(x, y)
                if (dragVertex != null) {
                    dragOffsetX = x - dragVertex!!.x
                    dragOffsetY = y - dragVertex!!.y
                    return true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (dragVertex != null) {
                    dragVertex!!.set(x - dragOffsetX, y - dragOffsetY)
                    invalidate() // 重新绘制视图
                    return true
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                dragVertex = null
            }
        }
        return super.onTouchEvent(event)
    }

    // 查找最近的顶点
    private fun findClosestVertex(x: Float, y: Float): PointF? {
        var closest: PointF? = null
        var minDistance = Float.MAX_VALUE
        for (vertex in vertices) {
            val distance = sqrt(
                (x - vertex.x).toDouble().pow(2.0) + (y - vertex.y).toDouble().pow(2.0)
            ).toFloat()
            if (distance < minDistance) {
                minDistance = distance
                closest = vertex
            }
        }
        return if (minDistance < 50) {
            closest
        } else null
    }

    fun getConfirmedPoints(): ArrayList<FloatArray> {
        /**
         * 计算出点的相对位置返回给一体机计算
         * */
        val width = ctx.getScreenWidth()
        val height = ctx.getScreenHeight()

        val region = ArrayList<FloatArray>()
        region.add(floatArrayOf(vertices[0].x / width, vertices[0].y / height))
        region.add(floatArrayOf(vertices[1].x / width, vertices[1].y / height))
        region.add(floatArrayOf(vertices[2].x / width, vertices[2].y / height))
        region.add(floatArrayOf(vertices[3].x / width, vertices[3].y / height))
        return region
    }
}