package com.example.mutidemo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.mutidemo.R
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class SteeringWheelView constructor(context: Context, attrs: AttributeSet) :
    View(context, attrs), View.OnTouchListener {
    private val kTag = "SteeringWheelView"

    //画布中心x
    private var canvasCenterX = 0f

    //画布中心y
    private var canvasCenterY = 0f

    //控件直径
    private val diameter: Float

    //四个方位小点直径
    private val directionDiameter: Float

    //内部圆半径
    private var innerCircleRadius: Float = 0.0f

    //外圆区域
    private lateinit var outerCircleRectF: RectF

    //内部开关区域
    private lateinit var centerSwitchRectF: RectF

    //线条粗细
    private val borderStroke: Float

    //Paint
    private val borderPaint: Paint
    private val centerPaint: Paint
    private val leftDirectionPaint: Paint
    private val topDirectionPaint: Paint
    private val rightDirectionPaint: Paint
    private val bottomDirectionPaint: Paint
    private val switchPaint: Paint

    //Color
    private val directionColor: Int
    private val switchColor: Int

    // 各控件使用状态
    private var leftTurn = false
    private var topTurn = false
    private var rightTurn = false
    private var bottomTurn = false
    private var centerTurn = false

    init {
        val type = context.obtainStyledAttributes(attrs, R.styleable.SteeringWheelView)
        diameter = type.getDimension(
            R.styleable.SteeringWheelView_ctrl_diameter, 200f
        )
        val borderColor = type.getColor(
            R.styleable.SteeringWheelView_ctrl_borderColor, Color.CYAN
        )
        borderStroke = type.getDimension(
            R.styleable.SteeringWheelView_ctrl_borderStroke, 5f
        )
        switchColor = type.getColor(
            R.styleable.SteeringWheelView_ctrl_switchColor, Color.WHITE
        )
        directionColor = type.getColor(
            R.styleable.SteeringWheelView_ctrl_directionColor, Color.BLUE
        )
        directionDiameter = type.getDimension(
            R.styleable.SteeringWheelView_ctrl_directionDiameter, 15f
        )
        type.recycle()

        borderPaint = Paint()
        borderPaint.isAntiAlias = true
        borderPaint.isDither = true
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderStroke
        borderPaint.color = borderColor

        centerPaint = Paint()
        centerPaint.isAntiAlias = true
        centerPaint.isDither = true
        centerPaint.style = Paint.Style.FILL
        centerPaint.color = borderColor

        switchPaint = Paint()
        switchPaint.isAntiAlias = true
        switchPaint.isDither = true
        switchPaint.style = Paint.Style.STROKE
        switchPaint.strokeWidth = borderStroke
        switchPaint.strokeCap = Paint.Cap.ROUND
        switchPaint.color = switchColor

        leftDirectionPaint = Paint()
        leftDirectionPaint.isAntiAlias = true
        leftDirectionPaint.isDither = true
        leftDirectionPaint.style = Paint.Style.FILL
        leftDirectionPaint.color = directionColor

        topDirectionPaint = Paint()
        topDirectionPaint.isAntiAlias = true
        topDirectionPaint.isDither = true
        topDirectionPaint.style = Paint.Style.FILL
        topDirectionPaint.color = directionColor

        rightDirectionPaint = Paint()
        rightDirectionPaint.isAntiAlias = true
        rightDirectionPaint.isDither = true
        rightDirectionPaint.style = Paint.Style.FILL
        rightDirectionPaint.color = directionColor

        bottomDirectionPaint = Paint()
        bottomDirectionPaint.isAntiAlias = true
        bottomDirectionPaint.isDither = true
        bottomDirectionPaint.style = Paint.Style.FILL
        bottomDirectionPaint.color = directionColor

        //设置控件可触摸
        setOnTouchListener(this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasCenterX = (w shr 1).toFloat()
        canvasCenterY = (h shr 1).toFloat()

        val outerCircleRadius = diameter.toInt() shr 1 //半径

        centerSwitchRectF = RectF(
            (canvasCenterX - (outerCircleRadius shr 2) * 0.75).toFloat(),
            (canvasCenterY - (outerCircleRadius shr 2) * 0.75).toFloat(),
            (canvasCenterX + (outerCircleRadius shr 2) * 0.75).toFloat(),
            (canvasCenterY + (outerCircleRadius shr 2) * 0.75).toFloat()
        )

        // 大外圈区域
        outerCircleRectF = RectF(
            canvasCenterX - outerCircleRadius - borderStroke,
            canvasCenterY - outerCircleRadius - borderStroke,
            canvasCenterX + outerCircleRadius + borderStroke,
            canvasCenterY + outerCircleRadius + borderStroke
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minimumWidth = suggestedMinimumWidth
        val minimumHeight = suggestedMinimumHeight
        val width = measureWidth(minimumWidth, widthMeasureSpec)
        val height = measureHeight(minimumHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    private fun measureWidth(defaultWidth: Int, measureSpec: Int): Int {
        var width = defaultWidth
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        when (specMode) {
            MeasureSpec.AT_MOST -> width = (diameter + borderStroke * 2).toInt()
            MeasureSpec.EXACTLY -> width = specSize
            MeasureSpec.UNSPECIFIED -> width = defaultWidth.coerceAtLeast(specSize)
        }
        return width
    }

    private fun measureHeight(defaultHeight: Int, measureSpec: Int): Int {
        var height = defaultHeight
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        when (specMode) {
            MeasureSpec.AT_MOST -> height = (diameter + borderStroke * 2).toInt()
            MeasureSpec.EXACTLY -> height = specSize
            MeasureSpec.UNSPECIFIED -> height = defaultHeight.coerceAtLeast(specSize)
        }
        return height
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val outerCircleRadius = diameter.toInt() shr 1 //半径
        //外圆圆圈
        canvas.drawCircle(
            canvasCenterX,
            canvasCenterY,
            outerCircleRadius.toFloat(),
            borderPaint
        )

        //内部圆背景
        innerCircleRadius = (directionDiameter.toInt() shl 1).toFloat()
        canvas.drawCircle(
            canvasCenterX,
            canvasCenterY,
            innerCircleRadius,
            centerPaint
        )

        //周围四个方向小点
        canvas.drawCircle(
            (canvasCenterX - outerCircleRadius * 0.75).toFloat(),
            canvasCenterY,
            (directionDiameter.toInt() shr 1).toFloat(),
            leftDirectionPaint
        )

        canvas.drawCircle(
            (canvasCenterX + outerCircleRadius * 0.75).toFloat(),
            canvasCenterY,
            (directionDiameter.toInt() shr 1).toFloat(),
            topDirectionPaint
        )

        canvas.drawCircle(
            canvasCenterX,
            (canvasCenterY - outerCircleRadius * 0.75).toFloat(),
            (directionDiameter.toInt() shr 1).toFloat(),
            rightDirectionPaint
        )

        canvas.drawCircle(
            canvasCenterX,
            (canvasCenterY + outerCircleRadius * 0.75).toFloat(),
            (directionDiameter.toInt() shr 1).toFloat(),
            bottomDirectionPaint
        )

        //中间开关
        canvas.drawArc(
            centerSwitchRectF, -50f, 280f, false, switchPaint
        )
        canvas.drawLine(
            canvasCenterX,
            canvasCenterY - (directionDiameter * 1.2).toFloat(),
            canvasCenterX,
            canvasCenterY - (directionDiameter * 0.5).toFloat(),
            switchPaint
        )
        invalidate()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val x: Float = event.x
        val y: Float = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //（x,y）点到（canvasCenterX,canvasCenterY）的距离
                val distance = sqrt(
                    abs(x - canvasCenterX).pow(2) + abs(y - canvasCenterY).pow(2)
                )
//                Log.d(kTag, "onTouch: [$x,$y]")

                // 计算角度正弦值
                val sinAngle = (y - canvasCenterY) / distance
//                Log.d(kTag, "sinAngle: $sinAngle")

                val sin = sin(Math.PI / 4)

                // 计算点击的距离，区分点击的是环还是中心位置
                setDefaultValue()

                // 判断
                if (distance > innerCircleRadius) {
                    if ((x - canvasCenterX) < 0 && abs(sinAngle) < sin) {
                        leftTurn = true
                        listener?.onLeftTurn()
                    } else if ((y - canvasCenterY) > 0 && abs(sinAngle) > sin) {
                        bottomTurn = true
                        listener?.onBottomTurn()
                    } else if ((x - canvasCenterX) > 0 && abs(sinAngle) < sin) {
                        rightTurn = true
                        listener?.onRightTurn()
                    } else if ((y - canvasCenterY) < 0 && abs(sinAngle) > sin) {
                        topTurn = true
                        listener?.onTopTurn()
                    }
                } else {
                    centerTurn = true
                    listener?.onCenterTurn()
                }
            }

            MotionEvent.ACTION_UP -> {
                if (leftTurn) {
                    leftTurn = false
                    listener?.onActionTurnUp(Direction.LEFT)
                } else if (topTurn) {
                    topTurn = false
                    listener?.onActionTurnUp(Direction.TOP)
                } else if (rightTurn) {
                    rightTurn = false
                    listener?.onActionTurnUp(Direction.RIGHT)
                } else if (bottomTurn) {
                    bottomTurn = false
                    listener?.onActionTurnUp(Direction.BOTTOM)
                } else {
                    centerTurn = false
                    listener?.onActionTurnUp(Direction.CENTER)
                }
            }
        }
        return true
    }

    //每次手指抬起都重置方向状态
    private fun setDefaultValue() {
        leftTurn = false
        topTurn = false
        rightTurn = false
        bottomTurn = false
        centerTurn = false
    }

    private var listener: OnWheelTouchListener? = null

    interface OnWheelTouchListener {
        /**
         * 左
         */
        fun onLeftTurn()

        /**
         * 上
         */
        fun onTopTurn()

        /**
         * 右
         */
        fun onRightTurn()

        /**
         * 下
         */
        fun onBottomTurn()

        /**
         * 中间
         */
        fun onCenterTurn()

        /**
         * 松开
         */
        fun onActionTurnUp(dir: Direction)
    }

    fun setOnWheelTouchListener(listener: OnWheelTouchListener?) {
        this.listener = listener
    }

    enum class Direction {
        LEFT, TOP, RIGHT, BOTTOM, CENTER
    }
}