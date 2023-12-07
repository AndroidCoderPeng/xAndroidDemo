package com.example.multidemo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.example.multidemo.R
import com.pengxh.kt.lite.extensions.dp2px
import kotlin.math.cos
import kotlin.math.sin

/**
 * 指南针自定义表盘
 * */
class CompassDialView constructor(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val kTag = "CompassDialView"

    //View中心X坐标
    private var centerX = 0f

    //View中心Y坐标
    private var centerY = 0f

    //控件边长
    private val viewSideLength: Int
    private val rect: Rect

    //内部表盘半径
    private val compassDialRadius: Int
    private var innerRadius = 0
    private var outerRadius = 0
    private var degreeValue = 0
    private val valueTextSize: Int
    private val innerTextSize: Int
    private val outerTextSize: Int

    //表盘刻度
    private lateinit var tickPaint: Paint
    private lateinit var currentTickPaint: Paint
    private lateinit var valuePaint: TextPaint
    private lateinit var innerPaint: TextPaint
    private lateinit var outerPaint: TextPaint

    private lateinit var outerTextPath: Path

    //刻度长度
    private val tickLength = 10f.dp2px(context)

    init {
        val type = context.obtainStyledAttributes(attrs, R.styleable.CompassDialView)
        compassDialRadius = type.getDimensionPixelOffset(
            R.styleable.CompassDialView_cps_radius, 300
        )
        //需要给外围刻度留位置
        viewSideLength = compassDialRadius + 20f.dp2px(context)

        valueTextSize = type.getDimensionPixelOffset(
            R.styleable.CompassDialView_cps_degree_textSize, 30
        )
        innerTextSize = type.getDimensionPixelOffset(
            R.styleable.CompassDialView_cps_inner_textSize, 16
        )
        outerTextSize = type.getDimensionPixelOffset(
            R.styleable.CompassDialView_cps_outer_textSize, 14
        )
        type.recycle()

        initPaint()

        //辅助框
        rect = Rect(-viewSideLength, -viewSideLength, viewSideLength, viewSideLength)
    }

    private fun initPaint() {
        tickPaint = Paint()
        tickPaint.color = Color.DKGRAY
        tickPaint.style = Paint.Style.STROKE
        tickPaint.strokeWidth = 2f.dp2px(context).toFloat()
        tickPaint.isAntiAlias = true

        valuePaint = TextPaint()
        valuePaint.color = Color.BLACK
        valuePaint.isAntiAlias = true
        valuePaint.textAlign = Paint.Align.CENTER
        valuePaint.textSize = valueTextSize.toFloat()

        innerPaint = TextPaint()
        innerPaint.isAntiAlias = true
        innerPaint.textAlign = Paint.Align.CENTER
        innerPaint.textSize = innerTextSize.toFloat()

        innerRadius = compassDialRadius - 25f.dp2px(context)

        outerPaint = TextPaint()
        outerPaint.color = Color.DKGRAY
        outerPaint.isAntiAlias = true
        outerPaint.textAlign = Paint.Align.CENTER
        outerPaint.textSize = outerTextSize.toFloat()

        outerTextPath = Path()
        outerRadius = compassDialRadius + 5f.dp2px(context)
        val rectF = RectF(
            -outerRadius.toFloat(),
            -outerRadius.toFloat(),
            outerRadius.toFloat(),
            outerRadius.toFloat()
        )
        //起点是正北方
        outerTextPath.addArc(rectF, -90f, 360f)

        currentTickPaint = Paint()
        currentTickPaint.color = Color.RED
        currentTickPaint.style = Paint.Style.STROKE
        currentTickPaint.strokeWidth = 2f.dp2px(context).toFloat()
        currentTickPaint.isAntiAlias = true
    }

    //计算出中心位置，便于定位
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = (w shr 1).toFloat()
        centerY = (h shr 1).toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        // 获取宽
        val mWidth: Int = if (widthSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            widthSpecSize
        } else {
            // wrap_content，外边界宽
            (viewSideLength * 2)
        }
        // 获取高
        val mHeight: Int = if (heightSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            heightSpecSize
        } else {
            // wrap_content，外边界高
            (viewSideLength * 2)
        }
        // 设置该view的宽高
        setMeasuredDimension(mWidth, mHeight)
    }

    /**
     * 注意：坐标系和生活中不一样，需要逆时针-90。已作处理
     * */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        /**
         * 画布移到中心位置，方便绘制一系列图形
         */
        canvas.translate(centerX, centerY)

//        drawGuides(canvas)

        //画表盘刻度
        for (angle in 0 until 360 step 3) {
            //角度需要转为弧度
            val radians = (angle - 90) * (Math.PI / 180)

            val startX = (compassDialRadius - tickLength) * cos(radians)
            val startY = (compassDialRadius - tickLength) * sin(radians)
            val stopX = compassDialRadius * cos(radians)
            val stopY = compassDialRadius * sin(radians)

            canvas.drawLine(
                startX.toFloat(),
                startY.toFloat(),
                stopX.toFloat(),
                stopY.toFloat(),
                tickPaint
            )
        }

        //画方位
        for (angle in 0 until 360 step 90) {
            //角度需要转为弧度
            val radians = (angle - 90) * (Math.PI / 180)

            val startX = innerRadius * cos(radians)
            val stopY = innerRadius * sin(radians)

            var direction = ""
            when (angle) {
                0 -> {
                    direction = "北"
                    innerPaint.color = Color.RED
                }

                90 -> {
                    direction = "东"
                    innerPaint.color = Color.DKGRAY
                }

                180 -> {
                    direction = "南"
                    innerPaint.color = Color.DKGRAY
                }

                270 -> {
                    direction = "西"
                    innerPaint.color = Color.DKGRAY
                }
            }

            val fontMetrics = innerPaint.fontMetrics
            val top = fontMetrics.top //基线到字体上边框的距离,即上图中的top
            val bottom = fontMetrics.bottom //基线到字体下边框的距离,即上图中的bottom
            val fontHeight = top + bottom
            canvas.drawText(
                direction,
                startX.toFloat(),
                (stopY - fontHeight / 2).toFloat(),
                innerPaint
            )
        }

        //画外围刻度
        for (angle in 0 until 360 step 30) {
            //角度需要转为弧度。减180是为了将drawTextOnPath的起点和outerTextPath的起点保持一致
            val radians = (angle - 180) * (Math.PI / 180)

            val hOffset = outerRadius * radians

            /**
             * hOffset : 与路径起始点的水平偏移距离。直线上为直线距离，曲线上为弧长
             * vOffset : 与路径中心的垂直偏移量
             * */
            canvas.drawTextOnPath(
                angle.toString(),
                outerTextPath,
                hOffset.toFloat(),
                0f,
                outerPaint
            )
        }

        val fontMetrics = valuePaint.fontMetrics
        val top = fontMetrics.top
        val bottom = fontMetrics.bottom
        val fontHeight = top + bottom
        canvas.drawText("${degreeValue}°", 0f, -fontHeight / 2, valuePaint)

        //绘制实际角度值刻度。
        /**
         * 判断是否是大于180
         * [0,180]，顺时针
         * [180,360]，逆时针
         * */
        if (degreeValue < 180) {
            for (angle in 0..degreeValue step 3) {
                //角度需要转为弧度
                val radians = (angle - 90) * (Math.PI / 180)

                //判断是否是最后一个元素
                val startX: Double
                val startY: Double
                if ((degreeValue - angle) <= 2) {
                    startX = (compassDialRadius - tickLength * 1.5) * cos(radians)
                    startY = (compassDialRadius - tickLength * 1.5) * sin(radians)
                } else {
                    startX = (compassDialRadius - tickLength) * cos(radians)
                    startY = (compassDialRadius - tickLength) * sin(radians)
                }

                val stopX = compassDialRadius * cos(radians)
                val stopY = compassDialRadius * sin(radians)

                canvas.drawLine(
                    startX.toFloat(),
                    startY.toFloat(),
                    stopX.toFloat(),
                    stopY.toFloat(),
                    currentTickPaint
                )
            }
        } else {
            for (angle in 360 downTo degreeValue step 3) {
                //角度需要转为弧度
                val radians = (angle - 90) * (Math.PI / 180)

                //判断是否是最后一个元素
                val startX: Double
                val startY: Double
                if ((angle - degreeValue) <= 2) {
                    startX = (compassDialRadius - tickLength * 1.5) * cos(radians)
                    startY = (compassDialRadius - tickLength * 1.5) * sin(radians)
                } else {
                    startX = (compassDialRadius - tickLength) * cos(radians)
                    startY = (compassDialRadius - tickLength) * sin(radians)
                }

                val stopX = compassDialRadius * cos(radians)
                val stopY = compassDialRadius * sin(radians)

                canvas.drawLine(
                    startX.toFloat(),
                    startY.toFloat(),
                    stopX.toFloat(),
                    stopY.toFloat(),
                    currentTickPaint
                )
            }
        }
    }

    /**
     * 辅助线
     * */
    private fun drawGuides(canvas: Canvas) {
        //最外层方框，即自定义View的边界
        canvas.drawRect(rect, tickPaint)

        //外层表盘刻度文字基准线
        canvas.drawPath(outerTextPath, tickPaint)

        //中心横线
        canvas.drawLine(
            -viewSideLength.toFloat(),
            0f,
            viewSideLength.toFloat(),
            0f,
            tickPaint
        )

        //中心竖线
        canvas.drawLine(
            0f,
            -viewSideLength.toFloat(),
            0f,
            viewSideLength.toFloat(),
            tickPaint
        )
    }

    fun setDegreeValue(value: Int) {
        degreeValue = value
        //实时刷新角度值
        invalidate()
    }
}