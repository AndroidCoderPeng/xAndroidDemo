package com.example.multidemo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.SweepGradient
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.example.multidemo.R
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.sp2px
import kotlin.math.cos
import kotlin.math.sin


class RadarScanView constructor(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val kTag = "RadarScanView"

    //View边框线条颜色
    private val borderColor: Int

    //View边框线条粗细
    private val border: Int

    //同心圆数量
    private val circleCount: Int

    //最外层圆半径
    private var radius: Int

    //内圆文字路径半径
    private var innerRadius: Int

    //控件边长
    private val viewSideLength: Int
    private val rect: Rect

    //需要渲染的数据点集合
    private val points = ArrayList<PointF>()

    //View中心X坐标
    private var centerX = 0f

    //View中心Y坐标
    private var centerY = 0f

    //雷达扫描角度步长
    private var degrees = 0f

    private lateinit var tickPaint: Paint
    private lateinit var borderPaint: Paint
    private lateinit var shaderPaint: Paint
    private lateinit var dataPaint: Paint
    private lateinit var innerPaint: TextPaint
    private lateinit var innerTextPath: Path

    //雷达扫描线后面的渐变梯度
    private lateinit var sweepGradient: SweepGradient

    //雷达旋转矩阵
    private lateinit var matrix: Matrix

    init {
        val type = context.obtainStyledAttributes(attrs, R.styleable.RadarScanView)
        borderColor = type.getColor(R.styleable.RadarScanView_radar_borderColor, Color.BLUE)
        border = type.getDimensionPixelOffset(R.styleable.RadarScanView_radar_border, 1)
        circleCount = type.getInt(R.styleable.RadarScanView_radar_circleCount, 4)
        radius = type.getDimensionPixelOffset(R.styleable.RadarScanView_radar_radius, 300)
        type.recycle()

        innerRadius = radius - 10.dp2px(context)

        //需要给外围刻度留位置
        viewSideLength = radius + 30.dp2px(context)
        //辅助框
        rect = Rect(-viewSideLength, -viewSideLength, viewSideLength, viewSideLength)

        initPaint()

        //控制转动
        postDelayed(object : Runnable {
            override fun run() {
                degrees++
                //为矩阵设置旋转坐标，顺时针。因为翻转过坐标轴，所以需要-
                matrix.setRotate(-degrees, 0f, 0f)

                invalidate()
                if (degrees == 360f) {
                    degrees = 0f
                }

                //周期10ms
                postDelayed(this, 10)
            }
            //延迟100ms启动
        }, 100)
    }

    private fun initPaint() {
        tickPaint = Paint()
        tickPaint.color = Color.RED
        tickPaint.style = Paint.Style.STROKE
        tickPaint.strokeWidth = 2f.dp2px(context)
        tickPaint.isAntiAlias = true

        borderPaint = Paint()
        borderPaint.color = borderColor
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = border.toFloat()
        borderPaint.strokeCap = Paint.Cap.ROUND //圆头
        borderPaint.isAntiAlias = true

        innerPaint = TextPaint()
        innerPaint.isAntiAlias = true
        innerPaint.textAlign = Paint.Align.CENTER
        innerPaint.textSize = 14f.sp2px(context)
        innerTextPath = Path()
        val innerRectF = RectF(
            -innerRadius.toFloat(),
            -innerRadius.toFloat(),
            innerRadius.toFloat(),
            innerRadius.toFloat()
        )
        innerTextPath.addArc(innerRectF, -90f, 360f)

        //扫描线画笔
        shaderPaint = Paint()
        shaderPaint.isAntiAlias = true
        shaderPaint.style = Paint.Style.FILL
        sweepGradient = SweepGradient(0f, 0f, borderColor, Color.TRANSPARENT)
        shaderPaint.shader = sweepGradient

        //数据点画笔
        dataPaint = Paint()
        dataPaint.color = borderColor
        dataPaint.isAntiAlias = true
        dataPaint.style = Paint.Style.FILL

        //矩阵
        matrix = Matrix()
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        /**
         * 画布移到中心位置，方便绘制一系列图形
         */
        canvas.translate(centerX, centerY)

//        drawGuides(canvas)

        //每道同心圆的半径差
        var tempR = radius
        val deltaR = tempR / circleCount
        for (i in 0..circleCount) {
            canvas.drawCircle(0f, 0f, tempR.toFloat(), borderPaint)
            tempR -= deltaR
        }

        //画十字交叉线
        canvas.drawLine(0f, -radius.toFloat(), 0f, radius.toFloat(), borderPaint)
        canvas.drawLine(-radius.toFloat(), 0f, radius.toFloat(), 0f, borderPaint)

        //画方位
        for (angle in 0 until 360 step 90) {
            //角度需要转为弧度
            val radians = (angle - 180) * (Math.PI / 180)

            val hOffset = innerRadius * radians

            var direction = ""
            when (angle) {
                0 -> {
                    direction = "北"
                    innerPaint.color = Color.RED
                    innerPaint.typeface = Typeface.DEFAULT_BOLD
                }

                90 -> {
                    direction = "东"
                    innerPaint.color = Color.BLACK
                    innerPaint.typeface = Typeface.DEFAULT
                }

                180 -> {
                    direction = "南"
                    innerPaint.color = Color.BLACK
                    innerPaint.typeface = Typeface.DEFAULT
                }

                270 -> {
                    direction = "西"
                    innerPaint.color = Color.BLACK
                    innerPaint.typeface = Typeface.DEFAULT
                }
            }

            val fontMetrics = innerPaint.fontMetrics
            val top = fontMetrics.top //基线到字体上边框的距离,即上图中的top
            val bottom = fontMetrics.bottom //基线到字体下边框的距离,即上图中的bottom
            val fontHeight = top + bottom
            canvas.drawTextOnPath(
                direction,
                innerTextPath,
                hOffset.toFloat(),
                -fontHeight / 2,
                innerPaint
            )
        }

        //画数据点
        points.forEach {
            canvas.drawCircle(it.x, it.y, 10f, dataPaint)
        }

        /**
         * 上下翻转画布，否则矩阵旋转是逆时针，因为手机等设备Y轴和生活中的坐标轴Y轴是反的
         * */
        canvas.scale(1f, -1f)

        //关联矩阵
        canvas.concat(matrix)
        canvas.drawCircle(0f, 0f, radius.toFloat(), shaderPaint)
    }

    /**
     * 辅助线
     * */
    private fun drawGuides(canvas: Canvas) {
        //最外层方框，即自定义View的边界
        canvas.drawRect(rect, tickPaint)

        //内层表盘方向文字基准线
        canvas.drawPath(innerTextPath, tickPaint)
    }

    /**
     * 数据点
     * @param dataPoints 数据点集合
     * */
    fun renderPointData(dataPoints: ArrayList<DataPoint>) {
        dataPoints.forEach {
            val result = recursionAngle(it.angle)
            //转为弧度
            val dataAngle = (result * Math.PI / 180).toFloat()
            val dataDistance = recursionDistance(it.distance.dp2px(context))

            //计算实际圆心坐标
            val x = dataDistance * cos(dataAngle)
            val y = dataDistance * sin(dataAngle)

            points.add(PointF(x, y))
        }
    }

    /**
     * 数据点
     * @param angle 数据点和圆心的方位角
     * @param distance 数据点和圆心的相对距离
     * */
    data class DataPoint(val angle: Float, val distance: Float)

    /**
     * 递归计算周期性角度
     * */
    private fun recursionAngle(angle: Float): Float {
        return if (angle < -360) {
            recursionAngle(angle + 360)
        } else if (angle > 360) {
            recursionAngle(angle - 360)
        } else {
            angle
        }
    }

    /**
     * 递归计算周期性距离
     * */
    private fun recursionDistance(distance: Float): Float {
        return if (distance <= 0) {
            0f
        } else if (distance >= radius) {
            radius.toFloat()
        } else {
            distance
        }
    }
}