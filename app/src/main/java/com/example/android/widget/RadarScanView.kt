package com.example.android.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.SweepGradient
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withRotation
import androidx.core.graphics.withScale
import com.example.android.R
import com.example.android.util.ExampleConstant
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.sp2px
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * 雷达扫描表盘
 *
 * 角度约定：正北为 0°，顺时针递增
 * */
class RadarScanView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private companion object {
        const val FULL_CIRCLE = 360
        const val HALF_CIRCLE = 180
        const val DIRECTION_STEP = 90
        const val TICK_STEP = 3
        const val SCAN_DURATION_MS = 3600L

        const val DEFAULT_CIRCLE_COUNT = 4
        const val DEFAULT_RADIUS_DP = 144f
        const val DEFAULT_BORDER_DP = 1f
        const val DEFAULT_BORDER_COLOR = "#8000FFF0"

        const val TEXT_GAP_DP = 8f      // 方位文字与外圆之间留出的间距
        const val VIEW_MARGIN_DP = 30f  // wrap_content 时为刻度、方位文字预留的边距
        const val NEEDLE_SCALE = 0.75f  // 指针长度占半径的比例
        const val TICK_LENGTH_DP = 15f
        const val POINT_RADIUS_DP = 6f
    }

    // ---------- 自定义属性 ----------
    //先给默认值，init 中再按 XML 覆盖；因需在 lambda 内赋值，故为 var
    private var borderColor: Int = DEFAULT_BORDER_COLOR.toColorInt()
    private var borderWidth: Float = DEFAULT_BORDER_DP.dp2px(context)
    private var circleCount: Int = DEFAULT_CIRCLE_COUNT
    private var radius: Float = DEFAULT_RADIUS_DP.dp2px(context)
    private val outerTextRadius: Float  // 方位文字所在圆的半径
    private val tickLength: Float
    private val pointRadius: Float
    private val halfSide: Float         // wrap_content 时的半边长

    // ---------- 画笔 ----------
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isFilterBitmap = true
    }
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f.dp2px(context)
    }
    private val directionPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 14f.sp2px(context)
    }
    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.RED
    }
    private val nearestPointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.GREEN
    }

    private val sweepPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val needlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isFilterBitmap = true
    }

    // ---------- 绘制资源 ----------
    private val backgroundBitmap = BitmapFactory.decodeResource(resources, R.mipmap.bg_radar)
    private val needleBitmap = BitmapFactory.decodeResource(resources, R.mipmap.needle)
    private val backgroundRect = Rect()
    private val needleRect = Rect()

    //文字基线在径向上的偏移，使文字的视觉中心压在方位点上
    private val directionOffsetY = directionPaint.fontMetrics.run {
        -(top + bottom) / 2f
    }

    // ---------- 数据 ----------
    private val directions = listOf("北", "东", "南", "西")
    private val points = mutableListOf<PointF>()
    private var nearestPoint: PointF? = null

    private var centerX = 0f
    private var centerY = 0f
    private var scanDegrees = 0f
    private var degreeValue = 0  //设备罗盘上报的方位角，与扫描角 scanDegrees 无关

    //0 → 360 即完整一圈，动画值直接当作扫描线当前扫过的角度；启停见下方生命周期回调
    private val scanAnimator = ValueAnimator.ofFloat(0f, FULL_CIRCLE.toFloat()).apply {
        duration = SCAN_DURATION_MS
        interpolator = LinearInterpolator()
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener { animator ->
            scanDegrees = animator.animatedValue as Float
            invalidate()
        }
    }

    init {
        //withStyledAttributes 在 lambda 结束时自动 recycle
        context.withStyledAttributes(attrs, R.styleable.RadarScanView, defStyleAttr) {
            //属性自身即默认值，无需把默认值写两遍
            borderColor = getColor(R.styleable.RadarScanView_radar_borderColor, borderColor)
            borderWidth = getDimension(R.styleable.RadarScanView_radar_border, borderWidth)
            circleCount = getInt(
                R.styleable.RadarScanView_radar_circleCount, circleCount
            ).coerceAtLeast(1)
            radius = getDimension(R.styleable.RadarScanView_radar_radius, radius)
        }

        //以下尺寸全部由半径派生，坐标系以圆心为原点
        outerTextRadius = radius + TEXT_GAP_DP.dp2px(context)
        tickLength = TICK_LENGTH_DP.dp2px(context)
        pointRadius = POINT_RADIUS_DP.dp2px(context)
        halfSide = radius + VIEW_MARGIN_DP.dp2px(context)

        circlePaint.color = borderColor
        circlePaint.strokeWidth = borderWidth
        tickPaint.color = borderColor
        sweepPaint.shader = SweepGradient(0f, 0f, borderColor, Color.TRANSPARENT)

        //背景铺满整圆，指针按 NEEDLE_SCALE 略短于半径
        val half = radius.roundToInt()
        backgroundRect.set(-half, -half, half, half)
        val needleHalf = (radius * NEEDLE_SCALE).roundToInt()
        needleRect.set(-needleHalf, -needleHalf, needleHalf, needleHalf)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        scanAnimator.start()
    }

    override fun onDetachedFromWindow() {
        scanAnimator.cancel()
        super.onDetachedFromWindow()
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility == VISIBLE) {
            //只有已 start 的动画才能 resume，避免 View 尚未 attach 时就被启动
            if (scanAnimator.isStarted) {
                scanAnimator.resume()
            }
        } else {
            scanAnimator.pause()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = paddingLeft + (w - paddingLeft - paddingRight) / 2f
        centerY = paddingTop + (h - paddingTop - paddingBottom) / 2f
    }

    /**
     * wrap_content 时给出期望边长：直径 + 预留边距 + padding，
     * 其余模式交给 View.resolveSize() 按父容器约束收敛，无需手写 MeasureSpec 分支
     * */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val side = (halfSide * 2).roundToInt()
        setMeasuredDimension(
            resolveSize(side + paddingLeft + paddingRight, widthMeasureSpec),
            resolveSize(side + paddingTop + paddingBottom, heightMeasureSpec)
        )
    }

    /**
     * 调用顺序即图层顺序：底图 → 同心圆 → 十字线 → 方位文字 → 方位刻度 → 指针 → 数据点 → 扫描光束。
     * 先把原点平移到圆心，后续所有坐标都以圆心为 (0, 0)
     * */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate(centerX, centerY)
        drawBackground(canvas)
        drawCircles(canvas)
        drawCrossLines(canvas)
        drawDirections(canvas)
        drawDegreeTicks(canvas)
        drawNeedle(canvas)
        drawPoints(canvas)
        drawSweep(canvas)
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawBitmap(backgroundBitmap, null, backgroundRect, gridPaint)
    }

    //由外向内等间距分布，最外圈半径为 radius，共 circleCount 圈
    private fun drawCircles(canvas: Canvas) {
        val deltaR = radius / circleCount
        for (index in 0 until circleCount) {
            canvas.drawCircle(0f, 0f, radius - deltaR * index, circlePaint)
        }
    }

    private fun drawCrossLines(canvas: Canvas) {
        canvas.drawLine(0f, -radius, 0f, radius, circlePaint)
        canvas.drawLine(-radius, 0f, radius, 0f, circlePaint)
    }

    /**
     * 画方位文字。正北为 0°，顺时针。
     * 直接按三角函数算出外圈上四个正方位的坐标，再以该点为轴心旋转画布，
     * 文字沿圆周切线排布。不依赖 Path 的起点与弧长，位置严格对齐外圈上下左右
     * */
    private fun drawDirections(canvas: Canvas) {
        directions.forEachIndexed { index, text ->
            val degrees = index * DIRECTION_STEP.toFloat()
            val radians = Math.toRadians(degrees.toDouble())
            //外圈正方位坐标：x 向东为正，y 向南为正
            val x = (outerTextRadius * sin(radians)).toFloat()
            val y = (-outerTextRadius * cos(radians)).toFloat()

            val isNorth = index == 0
            directionPaint.color = if (isNorth) Color.RED else Color.WHITE
            directionPaint.typeface = if (isNorth) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

            canvas.withRotation(degrees, x, y) {
                //textAlign 为 CENTER，x 传方位点即水平居中；偏移量指向圆心
                drawText(text, x, y + directionOffsetY, directionPaint)
            }
        }
    }

    /**
     * 画实时方位角刻度：从正北出发，沿「劣弧」把刻度补齐到当前方位角。
     *
     * 拆成两段是为了让填充范围始终不超过半圈：
     * - 方位角 < 180°：0 → degreeValue，顺时针补齐；
     * - 方位角 ≥ 180°：360 → degreeValue，等价于逆时针补齐另一侧。
     *
     * 两种写法覆盖的弧长都是 min(θ, 360 - θ)，观感是刻度从正北向两侧对称生长，
     * 既不会把整圈画满，也省掉了一半的绘制量
     * */
    private fun drawDegreeTicks(canvas: Canvas) {
        val progression = if (degreeValue < HALF_CIRCLE) {
            0..degreeValue
        } else {
            FULL_CIRCLE downTo degreeValue
        }
        for (angle in progression step TICK_STEP) {
            //正北为 0°，而 -Y 才是屏幕正上方，故统一减 90° 对齐
            val radians = Math.toRadians((angle - 90).toDouble())
            val unitX = cos(radians).toFloat()
            val unitY = sin(radians).toFloat()
            val outer = radius + tickLength
            canvas.drawLine(
                outer * unitX, outer * unitY,
                radius * unitX, radius * unitY,
                tickPaint
            )
        }
    }

    //旋转画布即可，不必每帧 Bitmap.createBitmap 生成旋转后的新图
    private fun drawNeedle(canvas: Canvas) {
        canvas.withRotation(degreeValue.toFloat()) {
            drawBitmap(needleBitmap, null, needleRect, needlePaint)
        }
    }

    //最近点最后画，才能盖在与之重叠的普通点之上
    private fun drawPoints(canvas: Canvas) {
        points.forEach {
            canvas.drawCircle(it.x, it.y, pointRadius, pointPaint)
        }
        nearestPoint?.let {
            canvas.drawCircle(it.x, it.y, pointRadius, nearestPointPaint)
        }
    }

    /**
     * 画扫描光束。整圆填充带 [SweepGradient] 的画笔：渐变自 3 点钟方向起、
     * 沿角度增大方向由实色渐隐到透明，于是圆自带一条拖尾，看着就是扫描光束。
     *
     * 两处方向修正都是为了对齐「顺时针扫」的观感：
     * 1. canvas 的 Y 轴向下，先翻转 Y 轴，拖尾才会落在扫描前进方向的后方；
     * 2. 翻转后 [Canvas.rotate] 的视觉方向随之反转，故传 -scanDegrees 补偿回来。
     *
     * [withScale] 自带 save/restore，不会污染后续绘制
     * */
    private fun drawSweep(canvas: Canvas) {
        canvas.withScale(1f, -1f) {
            rotate(-scanDegrees)
            drawCircle(0f, 0f, radius, sweepPaint)
        }
    }

    /**
     * 更新罗盘方位角，驱动方位刻度与指针
     * @param value 方位角，超出 [0, 360) 会归一化
     * */
    fun setDegreeValue(value: Int) {
        val normalized = ((value % FULL_CIRCLE) + FULL_CIRCLE) % FULL_CIRCLE
        //传感器回调频率很高，角度没变就不重绘
        if (degreeValue == normalized) return
        degreeValue = normalized
        invalidate()
    }

    /**
     * 渲染数据点，并把距圆心最近的点回调出去
     * @param dataPoints 数据点集合，只读，不会被排序或改动
     * */
    fun renderPointData(dataPoints: List<DataPoint>, onGetNearestPoint: (DataPoint?) -> Unit) {
        points.clear()
        //用 minByOrNull 而非 sortBy，避免把调用方传入的集合就地排序
        val nearest = dataPoints.minByOrNull { it.distance }
        nearestPoint = nearest?.toPointF()
        if (nearest == null) {
            onGetNearestPoint(null)
        } else {
            dataPoints.forEach {
                points.add(it.toPointF())
            }
            onGetNearestPoint(nearest)
        }
        //不管有没有点都要刷新，否则从「有点」到「无点」界面不会更新
        invalidate()
    }

    /**
     * 数据点
     * @param angle 相对正北方向的方位角，单位「度」，顺时针
     * @param distance 距圆心的实际距离，单位米
     * */
    data class DataPoint(var angle: Double, var distance: Float)

    /**
     * 数据点转画布坐标。
     * angle 以正北为 0°，而屏幕正上方是 -Y，故统一减 90° 再转弧度；
     * 距离按 [ExampleConstant.MAX_DISTANCE] 归一化到半径，超量程的点收敛到最外环，
     * 异常负值同样被 coerceIn 收敛到圆心
     * */
    private fun DataPoint.toPointF(): PointF {
        val radians = Math.toRadians(angle - 90.0)
        val offset = (distance / ExampleConstant.MAX_DISTANCE).coerceIn(0f, 1f) * radius
        return PointF(offset * cos(radians).toFloat(), offset * sin(radians).toFloat())
    }
}