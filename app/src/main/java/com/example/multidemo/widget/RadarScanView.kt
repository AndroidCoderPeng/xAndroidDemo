package com.example.multidemo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.SweepGradient
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.multidemo.R


class RadarScanView constructor(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val kTag = "RadarScanView"
    private val borderColor: Int
    private val border: Int
    private val circleCount: Int
    private var radius: Int

    private var centerX = 0f
    private var centerY = 0f
    private var degrees = 0f

    private lateinit var borderPaint: Paint
    private lateinit var shaderPaint: Paint
    private lateinit var sweepGradient: SweepGradient
    private lateinit var matrix: Matrix

    private val rotateAngleHandler = Handler(Looper.getMainLooper())

    init {
        val type = context.obtainStyledAttributes(attrs, R.styleable.RadarScanView)
        borderColor = type.getColor(R.styleable.RadarScanView_radar_borderColor, Color.GRAY)
        border = type.getDimensionPixelOffset(R.styleable.RadarScanView_radar_border, 5)
        circleCount = type.getInt(R.styleable.RadarScanView_radar_circleCount, 4)
        radius = type.getDimensionPixelOffset(R.styleable.RadarScanView_radar_radius, 300)
        type.recycle()

        initPaint()

        //控制转动
        rotateAngleHandler.postDelayed(object : Runnable {
            override fun run() {
                degrees += 1f
                //为矩阵设置旋转坐标
                matrix.setRotate(degrees, 0f, 0f)

                Log.d(kTag, "degrees => $degrees")

                invalidate()
                if (degrees == 360f) {
                    degrees = 0f
                }

                postDelayed(this, 50)
            }
        }, 50)
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

        //新建扫描渲染，扫描边由透明->指定颜色进行渐变
        canvas.concat(matrix)
        canvas.drawCircle(0f, 0f, radius.toFloat(), shaderPaint)
    }
}