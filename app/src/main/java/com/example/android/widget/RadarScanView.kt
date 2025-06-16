package com.example.android.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.core.graphics.toColorInt
import com.example.android.R
import com.example.android.util.ExampleConstant
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.sp2px
import kotlin.math.cos
import kotlin.math.sin


class RadarScanView(private val context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val kTag = "RadarScanView"

    //View边框线条颜色
    private val borderColor: Int

    //View边框线条粗细
    private val border: Int

    //同心圆数量
    private val circleCount: Int

    //最外层圆半径
    private var radius: Int

    //外圆文字路径半径
    private var outerRadius: Int

    //控件边长
    private val viewSideLength: Int
    private val rect: Rect

    //需要渲染的数据点集合
    private var points: ArrayList<PointF>? = null

    //距离最近的点
    private var targetPoint: PointF? = null

    //View中心X坐标
    private var centerX = 0f

    //View中心Y坐标
    private var centerY = 0f

    //雷达扫描角度步长
    private var degrees = 0f

    //方位角
    private var degreeValue = 0

    private lateinit var tickPaint: Paint
    private lateinit var backPaint: Paint
    private lateinit var needlePaint: Paint
    private lateinit var borderPaint: Paint
    private lateinit var shaderPaint: Paint
    private lateinit var dataPaint: Paint
    private lateinit var targetPaint: Paint
    private lateinit var outerPaint: TextPaint
    private lateinit var outerTextPath: Path

    //雷达扫描线后面的渐变梯度
    private lateinit var sweepGradient: SweepGradient

    //背景栅格图
    private lateinit var bitmap: Bitmap

    //针
    private lateinit var needleBitmap: Bitmap

    //雷达旋转矩阵
    private lateinit var matrix: Matrix

    //背景区域范围
    private var bgRect: Rect

    //针区域范围
    private var needleRect: Rect

    //刻度长度
    private val tickLength = 15f.dp2px(context)

    init {
        val type = context.obtainStyledAttributes(attrs, R.styleable.RadarScanView)
        borderColor = type.getColor(
            R.styleable.RadarScanView_radar_borderColor, "#8000FFF0".toColorInt()
        )
        border = type.getDimensionPixelOffset(R.styleable.RadarScanView_radar_border, 1)
        circleCount = type.getInt(R.styleable.RadarScanView_radar_circleCount, 4)
        radius = type.getDimensionPixelOffset(R.styleable.RadarScanView_radar_radius, 300)
        type.recycle()

        outerRadius = radius + 8.dp2px(context)

        //需要给外围刻度留位置
        viewSideLength = radius + 30.dp2px(context)
        //辅助框
        rect = Rect(-viewSideLength, -viewSideLength, viewSideLength, viewSideLength)

        bgRect = Rect(-radius, -radius, radius, radius)

        val needleRectRadius = (radius * 0.75).toInt()
        needleRect = Rect(-needleRectRadius, -needleRectRadius, needleRectRadius, needleRectRadius)

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

        backPaint = Paint()
        backPaint.isAntiAlias = true
        bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.bg_radar)

        needlePaint = Paint()
        needlePaint.isAntiAlias = true
        //针
        needleBitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.needle)

        borderPaint = Paint()
        borderPaint.color = borderColor
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = border.toFloat()
        borderPaint.strokeCap = Paint.Cap.ROUND //圆头
        borderPaint.isAntiAlias = true

        outerPaint = TextPaint()
        outerPaint.isAntiAlias = true
        outerPaint.textAlign = Paint.Align.CENTER
        outerPaint.textSize = 14f.sp2px(context)
        outerTextPath = Path()
        val innerRectF = RectF(
            -outerRadius.toFloat(),
            -outerRadius.toFloat(),
            outerRadius.toFloat(),
            outerRadius.toFloat()
        )
        outerTextPath.addArc(innerRectF, -90f, 360f)

        //扫描线画笔
        shaderPaint = Paint()
        shaderPaint.isAntiAlias = true
        shaderPaint.style = Paint.Style.FILL
        sweepGradient = SweepGradient(0f, 0f, borderColor, Color.TRANSPARENT)
        shaderPaint.shader = sweepGradient

        //数据点画笔
        dataPaint = Paint()
        dataPaint.color = Color.RED
        dataPaint.isAntiAlias = true
        dataPaint.style = Paint.Style.FILL

        //最近点画笔
        targetPaint = Paint()
        targetPaint.color = Color.GREEN
        targetPaint.isAntiAlias = true
        targetPaint.style = Paint.Style.FILL

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

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        /**
         * 画布移到中心位置，方便绘制一系列图形
         */
        canvas.translate(centerX, centerY)

        //画背景
        canvas.drawBitmap(bitmap, null, bgRect, backPaint)

        //每道同心圆的半径差
        var tempR = radius
        val deltaR = tempR / circleCount
        for (i in 0 until circleCount) {
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

            val hOffset = outerRadius * radians

            var direction = ""
            when (angle) {
                0 -> {
                    direction = "北"
                    outerPaint.color = Color.RED
                    outerPaint.typeface = Typeface.DEFAULT_BOLD
                }

                90 -> {
                    direction = "东"
                    outerPaint.color = Color.WHITE
                    outerPaint.typeface = Typeface.DEFAULT
                }

                180 -> {
                    direction = "南"
                    outerPaint.color = Color.WHITE
                    outerPaint.typeface = Typeface.DEFAULT
                }

                270 -> {
                    direction = "西"
                    outerPaint.color = Color.WHITE
                    outerPaint.typeface = Typeface.DEFAULT
                }
            }

            val fontMetrics = outerPaint.fontMetrics
            val top = fontMetrics.top //基线到字体上边框的距离,即上图中的top
            val bottom = fontMetrics.bottom //基线到字体下边框的距离,即上图中的bottom
            val fontHeight = top + bottom
            canvas.drawTextOnPath(
                direction,
                outerTextPath,
                hOffset.toFloat(),
                -fontHeight / 2,
                outerPaint
            )
        }

        //画实时方位角
        if (degreeValue < 180) {
            for (angle in 0..degreeValue step 3) {
                //角度需要转为弧度
                val radians = (angle - 90) * (Math.PI / 180)

                val startX = (radius + tickLength) * cos(radians)
                val startY = (radius + tickLength) * sin(radians)

                val stopX = radius * cos(radians)
                val stopY = radius * sin(radians)

                tickPaint.strokeWidth = 8f.dp2px(context)
                tickPaint.color = borderColor
                canvas.drawLine(
                    startX.toFloat(),
                    startY.toFloat(),
                    stopX.toFloat(),
                    stopY.toFloat(),
                    tickPaint
                )
            }
        } else {
            for (angle in 360 downTo degreeValue step 3) {
                //角度需要转为弧度
                val radians = (angle - 90) * (Math.PI / 180)

                val startX = (radius + tickLength) * cos(radians)
                val startY = (radius + tickLength) * sin(radians)

                val stopX = radius * cos(radians)
                val stopY = radius * sin(radians)

                tickPaint.color = borderColor
                tickPaint.strokeWidth = 8f.dp2px(context)
                canvas.drawLine(
                    startX.toFloat(),
                    startY.toFloat(),
                    stopX.toFloat(),
                    stopY.toFloat(),
                    tickPaint
                )
            }
        }

        val needleMatrix = Matrix()
        needleMatrix.postRotate(degreeValue.toFloat())
        val bmp = Bitmap.createBitmap(
            needleBitmap,
            0, 0,
            needleBitmap.width, needleBitmap.height,
            needleMatrix, true
        )
        canvas.drawBitmap(bmp, null, needleRect, needlePaint)

        //画数据点
        points?.forEach {
            canvas.drawCircle(it.x, it.y, 10f, dataPaint)
        }

        //画最近的点
        targetPoint?.apply {
            canvas.drawCircle(x, y, 10f, targetPaint)
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
     * 更新罗盘方位角度
     * @param value 方位角
     * */
    fun setDegreeValue(value: Int) {
        degreeValue = value
        //实时刷新角度值
        invalidate()
    }

    /**
     * 数据点
     * @param dataPoints 数据点集合
     * */
    fun renderPointData(dataPoints: ArrayList<DataPoint>, callback: OnGetNearestPointCallback) {
        if (dataPoints.isNotEmpty()) {
            points = ArrayList()
            dataPoints.forEach {
                points?.add(it.convertPointF())
            }

            //计算出附近最近的点
            dataPoints.sortBy(DataPoint::distance)

            val nearestPoint = dataPoints.first()
            //减少排序计算次数，回调最近的点到主界面
            callback.getNearestPoint(nearestPoint)

            //在最近的点外侧绘制同心圆
            targetPoint = nearestPoint.convertPointF()
        } else {
            points?.clear()
            targetPoint = null
            callback.getNearestPoint(null)
        }
        //不管有无数据点，都要刷新点位数据，不然从有数据到无数据这个过程，界面不会及时刷新
        invalidate()
    }

    /**
     * 数据点
     * @param angle 数据点和圆心的方位角
     * @param distance 数据点和圆心的相对距离
     * */
    data class DataPoint(var angle: Double, var distance: Float)

    interface OnGetNearestPointCallback {
        fun getNearestPoint(point: DataPoint?)
    }

    /**
     * dataPoint转为PointF
     * */
    private fun DataPoint.convertPointF(): PointF {
        /**
         * 距离最大5.5米，表盘四个环，一个环距离1.5米，半径124dp（248px）
         * */
        val dataDistance = (this.distance / ExampleConstant.MAX_DISTANCE) * radius
        val x = dataDistance * cos(this.angle).toFloat()
        val y = dataDistance * sin(this.angle).toFloat()
        return PointF(x, y)
    }
}