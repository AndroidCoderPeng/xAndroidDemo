package com.example.multidemo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View
import com.example.multidemo.R
import kotlin.math.cos
import kotlin.math.sin


class RadarScanView constructor(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val kTag = "RadarScanView"
    private val borderColor: Int
    private val border: Int
    private val circleCount: Int
    private var radius: Int

    private var centerX = 0f
    private var centerY = 0f
    private var degrees = 0f
    private var dataAngle = 0f
    private var dataDistance = 0f

    private lateinit var borderPaint: Paint
    private lateinit var shaderPaint: Paint
    private lateinit var dataPaint: Paint
    private lateinit var sweepGradient: SweepGradient
    private lateinit var matrix: Matrix

    init {
        val type = context.obtainStyledAttributes(attrs, R.styleable.RadarScanView)
        borderColor = type.getColor(R.styleable.RadarScanView_radar_borderColor, Color.GRAY)
        border = type.getDimensionPixelOffset(R.styleable.RadarScanView_radar_border, 1)
        circleCount = type.getInt(R.styleable.RadarScanView_radar_circleCount, 4)
        radius = type.getDimensionPixelOffset(R.styleable.RadarScanView_radar_radius, 150)
        type.recycle()

        initPaint()

        //控制转动
        postDelayed(object : Runnable {
            override fun run() {
                degrees++
                //为矩阵设置旋转坐标
                matrix.setRotate(degrees, 0f, 0f)

                invalidate()
                if (degrees == 360f) {
                    degrees = 0f
                }

                //周期10ms
                postDelayed(this, 10)
            }
            //延迟100ms启动
        }, 100)

        //渲染点
//        postDelayed(object : Runnable {
//            override fun run() {
//                //周期10ms
//                postDelayed(this, 30)
//            }
//            //延迟100ms启动
//        }, 100)
    }

    private fun initPaint() {
        borderPaint = Paint()
        borderPaint.color = borderColor
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = border.toFloat()
        borderPaint.strokeCap = Paint.Cap.ROUND //圆头
        borderPaint.isAntiAlias = true

        //扫描线画笔
        shaderPaint = Paint()
        shaderPaint.isAntiAlias = true
        shaderPaint.style = Paint.Style.FILL
        sweepGradient = SweepGradient(0f, 0f, Color.TRANSPARENT, borderColor)
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        /**
         * 画布移到中心位置
         */
        canvas.translate(centerX, centerY)

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

        //画数据点
        canvas.drawCircle(
            dataDistance * cos(dataAngle),
            dataDistance * sin(dataAngle),
            15f,
            dataPaint
        )

        //关联矩阵
        canvas.concat(matrix)
        canvas.drawCircle(0f, 0f, radius.toFloat(), shaderPaint)
    }

    /**
     * 设置数据点
     * @param angle 数据点和圆心的方位角
     * @param distance 数据点和圆心的相对距离
     * */
    fun renderPointData(angle: Float, distance: Float) {
        dataAngle = recursionAngle(angle)
        dataDistance = recursionDistance(distance)
    }

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
            recursionDistance(distance - radius)
        } else {
            distance
        }
    }
}