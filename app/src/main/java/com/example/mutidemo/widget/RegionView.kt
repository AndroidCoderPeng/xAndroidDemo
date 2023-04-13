package com.example.mutidemo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.pengxh.kt.lite.extensions.obtainScreenHeight
import com.pengxh.kt.lite.extensions.obtainScreenWidth

class RegionView(private val ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {

    private val kTag = "RegionView"
    private val routePath: Path = Path()
    private val rectPath: Path = Path()
    private val routePaint: Paint = Paint()
    private val borderPaint: Paint = Paint()
    private var xys = ArrayList<Point>()

    init {
        routePaint.isAntiAlias = true
        routePaint.color = Color.RED
        routePaint.style = Paint.Style.STROKE
        routePaint.strokeWidth = 10f //设置线宽
        routePaint.isAntiAlias = true

        borderPaint.isAntiAlias = true
        borderPaint.color = Color.BLUE
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 10f //设置线宽
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
        xys.add(Point(x, y))

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
                val sortedX = xys.sortedBy { point -> point.x }
                val sortedY = xys.sortedBy { point -> point.y }
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
                val width = ctx.obtainScreenWidth()
                val height = ctx.obtainScreenHeight()

                confirmedListener?.onRegionConfirmed(0f, 0f)
            }
        }
        invalidate()
        return true
    }

    fun clearRoutePath() {
        routePath.reset()
        rectPath.reset()
        xys.clear()
        invalidate()
    }

    data class Point(val x: Float, val y: Float)

    private var confirmedListener: OnRegionConfirmedListener? = null

    interface OnRegionConfirmedListener {
        /**
         * @param leftTop 点距离屏幕左上角百分比
         * @param rightBottom 点距离屏幕右下角百分比
         * */
        fun onRegionConfirmed(leftTop: Float, rightBottom: Float)
    }

    fun setOnRegionConfirmedListener(listener: OnRegionConfirmedListener) {
        confirmedListener = listener
    }
}