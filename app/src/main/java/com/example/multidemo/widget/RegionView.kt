package com.example.multidemo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.multidemo.model.Point
import com.pengxh.kt.lite.extensions.getScreenHeight
import com.pengxh.kt.lite.extensions.getScreenWidth

class RegionView(private val ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {

    private val routePath: Path = Path()
    private val rectPath: Path = Path()
    private val routePaint: Paint = Paint()
    private val borderPaint: Paint = Paint()
    private var routes = ArrayList<Point>()

    init {
        routePaint.isAntiAlias = true
        routePaint.color = Color.RED
        routePaint.style = Paint.Style.STROKE
        routePaint.strokeWidth = 7f //设置线宽
        routePaint.isAntiAlias = true

        borderPaint.isAntiAlias = true
        borderPaint.color = Color.BLUE
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 7f //设置线宽
        borderPaint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(routePath, routePaint)

        canvas.drawPath(rectPath, borderPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val x = event.x
        val y = event.y
        routes.add(Point(x, y))

        when (event.action) {
            MotionEvent.ACTION_DOWN -> routePath.moveTo(x, y)
            MotionEvent.ACTION_MOVE -> routePath.lineTo(x, y)
            MotionEvent.ACTION_UP -> {
                routePath.lineTo(x, y)

                /**
                 * 找出最大的（x1，y1）和最小的（x2，y2）
                 *
                 * 左上（x2，y2）
                 * 右上（x1，y2）
                 * 左下（x2，y1）
                 * 右下（x1，y1）
                 * */
                val sortedX = routes.sortedBy { point -> point.x }
                val sortedY = routes.sortedBy { point -> point.y }
                val xMaxPoint = sortedX.last()
                val xMinPoint = sortedX.first()

                val yMaxPoint = sortedY.last()
                val yMinPoint = sortedY.first()

                /**
                 * 画出外接矩形
                 * */
                val leftTop = Point(xMinPoint.x, yMinPoint.y)
                val rightTop = Point(xMaxPoint.x, yMinPoint.y)
                val leftBottom = Point(xMinPoint.x, yMaxPoint.y)
                val rightBottom = Point(xMaxPoint.x, yMaxPoint.y)
                rectPath.moveTo(leftTop.x, leftTop.y)
                rectPath.lineTo(rightTop.x, rightTop.y)
                rectPath.lineTo(rightBottom.x, rightBottom.y)
                rectPath.lineTo(leftBottom.x, leftBottom.y)
                rectPath.lineTo(leftTop.x, leftTop.y)

                /**
                 * 计算出点的相对位置返回给一体机计算
                 * */
                val width = ctx.getScreenWidth()
                val height = ctx.getScreenHeight()

                /**
                 * 区域
                 * */
                if (region.isNotEmpty()) {
                    region.clear()
                }
                region.add(Point(leftTop.x / width, leftTop.y / height))
                region.add(Point(rightTop.x / width, rightTop.y / height))
                region.add(Point(leftBottom.x / width, leftBottom.y / height))
                region.add(Point(rightBottom.x / width, rightBottom.y / height))

                /**
                 * 点集合
                 * */
                if (points.isNotEmpty()) {
                    points.clear()
                }
                points.add(floatArrayOf(leftTop.x / width, leftTop.y / height))
                points.add(floatArrayOf(rightTop.x / width, rightTop.y / height))
                points.add(floatArrayOf(leftBottom.x / width, leftBottom.y / height))
                points.add(floatArrayOf(rightBottom.x / width, rightBottom.y / height))
            }
        }
        invalidate()
        return true
    }

    fun clearRoutePath() {
        routePath.reset()
        rectPath.reset()
        routes.clear()
        invalidate()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private var region = ArrayList<Point>()

    fun getConfirmedRegion(): ArrayList<Point> = region

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private var points = ArrayList<FloatArray>()

    fun getConfirmedPoints(): ArrayList<FloatArray> = points
}