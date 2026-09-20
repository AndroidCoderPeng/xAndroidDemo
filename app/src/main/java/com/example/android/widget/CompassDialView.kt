package com.example.android.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.withRotation
import com.example.android.R
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.sp2px
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * 指南针表盘
 *
 * 角度约定：正北为 0°，顺时针递增
 * */
class CompassDialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private companion object {
        const val FULL_CIRCLE = 360
        const val HALF_CIRCLE = 180
        const val DIRECTION_STEP = 90   // 方位文字间隔角度
        const val DEGREE_STEP = 30      // 外环角度文字间隔
        const val TICK_STEP = 3         // 刻度间隔角度

        const val DEFAULT_RADIUS_DP = 144f
        const val DEFAULT_VALUE_TEXT_SP = 30f // 中心读数
        const val DEFAULT_INNER_TEXT_SP = 16f // 方位文字
        const val DEFAULT_OUTER_TEXT_SP = 14f // 外环角度文字

        const val TICK_LENGTH_DP = 15f
        const val INNER_TEXT_GAP_DP = 30f // 方位文字相对表盘内缩的距离
        const val OUTER_TEXT_GAP_DP = 15f // 角度文字相对表盘外扩的距离
        const val VIEW_MARGIN_DP = 30f    // wrap_content 时为刻度、文字预留的边距
        const val END_TICK_SCALE = 1.5f   // 末端刻度相对普通刻度的倍数
    }

    // ---------- 自定义属性 ----------
    //先给默认值，init 中再按 XML 覆盖；因需在 lambda 内赋值，故为 var
    private var radius: Float = DEFAULT_RADIUS_DP.dp2px(context)
    private var valueTextSize: Float = DEFAULT_VALUE_TEXT_SP.sp2px(context)
    private var innerTextSize: Float = DEFAULT_INNER_TEXT_SP.sp2px(context)
    private var outerTextSize: Float = DEFAULT_OUTER_TEXT_SP.sp2px(context)

    // ---------- 画笔 ----------
    private val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.DKGRAY
        strokeWidth = 2f.dp2px(context)
    }
    private val currentTickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.RED
        strokeWidth = 2f.dp2px(context)
    }
    private val trianglePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.RED
    }
    private val valuePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
    }
    private val innerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }
    private val outerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = Color.DKGRAY
    }

    // ---------- 派生尺寸 ----------
    private val innerTextRadius: Float // 方位文字所在圆
    private val outerTextRadius: Float // 外环角度文字所在圆
    private val tickLength: Float
    private val halfSide: Float        // wrap_content 时的半边长
    private val trianglePath: Path

    //文字基线偏移，正值朝圆心，使文字视觉中心压在所在圆上
    private val valueOffsetY: Float
    private val innerOffsetY: Float

    // ---------- 数据 ----------
    private val directions = listOf("北", "东", "南", "西")
    private var centerX = 0f
    private var centerY = 0f
    private var degreeValue = 0        // 设备罗盘上报的方位角

    init {
        //withStyledAttributes 在 lambda 结束时自动 recycle
        context.withStyledAttributes(attrs, R.styleable.CompassDialView, defStyleAttr) {
            //属性自身即默认值，无需把默认值写两遍
            radius = getDimension(R.styleable.CompassDialView_cps_radius, radius)
            valueTextSize = getDimension(
                R.styleable.CompassDialView_cps_degree_textSize, valueTextSize
            )
            innerTextSize = getDimension(
                R.styleable.CompassDialView_cps_inner_textSize, innerTextSize
            )
            outerTextSize = getDimension(
                R.styleable.CompassDialView_cps_outer_textSize, outerTextSize
            )
        }

        //以下尺寸全部由半径派生，坐标系以圆心为原点
        tickLength = TICK_LENGTH_DP.dp2px(context)
        innerTextRadius = radius - INNER_TEXT_GAP_DP.dp2px(context)
        outerTextRadius = radius + OUTER_TEXT_GAP_DP.dp2px(context)
        halfSide = radius + VIEW_MARGIN_DP.dp2px(context)

        valuePaint.textSize = valueTextSize
        innerPaint.textSize = innerTextSize
        outerPaint.textSize = outerTextSize

        valueOffsetY = valuePaint.fontMetrics.run { -(top + bottom) / 2f }
        innerOffsetY = innerPaint.fontMetrics.run { -(top + bottom) / 2f }

        //正北方向的小三角，紧贴外环角度文字的内侧，顶点朝上
        trianglePath = Path().apply {
            moveTo(0f, -outerTextRadius + tickLength * 0.25f)
            lineTo(tickLength * 0.25f, -outerTextRadius + tickLength * 0.75f)
            lineTo(-tickLength * 0.25f, -outerTextRadius + tickLength * 0.75f)
            close()
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
     * 调用顺序即图层顺序：表盘刻度 → 方位文字 → 角度文字 → 正北三角 → 中心读数 → 当前方位刻度。
     * 先把原点平移到圆心，后续所有坐标都以圆心为 (0, 0)
     * */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate(centerX, centerY)
        drawDialTicks(canvas)
        drawDirections(canvas)
        drawDegreeTexts(canvas)
        drawNorthTriangle(canvas)
        drawDegreeValue(canvas)
        drawCurrentTicks(canvas)
    }

    //整圈表盘刻度，由表盘边缘向内延伸
    private fun drawDialTicks(canvas: Canvas) {
        for (angle in 0 until FULL_CIRCLE step TICK_STEP) {
            canvas.drawTick(angle, tickLength, tickPaint)
        }
    }

    //方位文字沿内圈排布，正北标红
    private fun drawDirections(canvas: Canvas) {
        directions.forEachIndexed { index, text ->
            innerPaint.color = if (index == 0) Color.RED else Color.DKGRAY
            val degrees = (index * DIRECTION_STEP).toFloat()
            canvas.drawTextOnCircle(text, innerTextRadius, degrees, innerPaint, innerOffsetY)
        }
    }

    //外环角度文字，基线落在角度文字所在圆上，故不传径向偏移
    private fun drawDegreeTexts(canvas: Canvas) {
        for (angle in 0 until FULL_CIRCLE step DEGREE_STEP) {
            canvas.drawTextOnCircle(angle.toString(), outerTextRadius, angle.toFloat(), outerPaint)
        }
    }

    private fun drawNorthTriangle(canvas: Canvas) {
        canvas.drawPath(trianglePath, trianglePaint)
    }

    private fun drawDegreeValue(canvas: Canvas) {
        canvas.drawText("${degreeValue}°", 0f, valueOffsetY, valuePaint)
    }

    /**
     * 画当前方位角刻度：从正北出发沿「劣弧」补齐到当前角度，末端那根加长，兼作指示。
     *
     * 拆成两段是为了让填充范围始终不超过半圈：
     * - 方位角 < 180°：0 → degreeValue，顺时针补齐；
     * - 方位角 ≥ 180°：360 → degreeValue，等价于逆时针补齐另一侧。
     *
     * 两种写法覆盖的弧长都是 min(θ, 360 - θ)，观感是刻度从正北向两侧对称生长
     * */
    private fun drawCurrentTicks(canvas: Canvas) {
        val progression = if (degreeValue < HALF_CIRCLE) {
            0..degreeValue
        } else {
            FULL_CIRCLE downTo degreeValue
        }
        for (angle in progression step TICK_STEP) {
            //序列末端离当前角度最近（差值必小于步长），把它加长成指示针
            val isEndTick = abs(angle - degreeValue) < TICK_STEP
            val length = if (isEndTick) tickLength * END_TICK_SCALE else tickLength
            canvas.drawTick(angle, length, currentTickPaint)
        }
    }

    /**
     * 在方位角 degrees 处画一根刻度，由表盘边缘向内延伸 length。
     * 正北为 0°，而屏幕正上方是 -Y，故统一减 90° 对齐
     * */
    private fun Canvas.drawTick(degrees: Int, length: Float, paint: Paint) {
        val radians = Math.toRadians((degrees - 90).toDouble())
        val unitX = cos(radians).toFloat()
        val unitY = sin(radians).toFloat()
        val inner = radius - length
        drawLine(radius * unitX, radius * unitY, inner * unitX, inner * unitY, paint)
    }

    /**
     * 把文字画在半径 textRadius、方位角 degrees 处，并沿圆周切线旋转。
     * 不用 drawTextOnPath：整圆 Path（sweep = 360）会被 Skia 退化处理，起点与走向均不可控。
     *
     * @param radialOffset 基线沿半径方向的偏移，正值朝圆心
     * */
    private fun Canvas.drawTextOnCircle(
        text: String,
        textRadius: Float,
        degrees: Float,
        paint: TextPaint,
        radialOffset: Float = 0f
    ) {
        val radians = Math.toRadians(degrees.toDouble())
        //x 向东为正，y 向南为正
        val x = (textRadius * sin(radians)).toFloat()
        val y = (-textRadius * cos(radians)).toFloat()
        withRotation(degrees, x, y) {
            drawText(text, x, y + radialOffset, paint)
        }
    }

    /**
     * 更新方位角，驱动中心读数与当前方位刻度
     * @param value 方位角，超出 [0, 360) 会归一化
     * */
    fun setDegreeValue(value: Int) {
        val normalized = ((value % FULL_CIRCLE) + FULL_CIRCLE) % FULL_CIRCLE
        //传感器回调频率很高，角度没变就不重绘
        if (degreeValue == normalized) return
        degreeValue = normalized
        invalidate()
    }
}
