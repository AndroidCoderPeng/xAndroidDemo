package com.example.multidemo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.RelativeLayout
import com.example.multidemo.R

/**
 * 方向盘控件
 * */
@SuppressLint("ClickableViewAccessibility")
class SteeringWheelView constructor(context: Context, attrs: AttributeSet) :
    RelativeLayout(context, attrs) {

    //画布中心x
    private var canvasCenterX = 0

    //画布中心y
    private var canvasCenterY = 0

    //控件直径
    private val diameter: Float

    //Paint
    private val backgroundPaint: Paint
    private val borderPaint: Paint
    private val directionPaint: Paint

    // 各控件使用状态
    private var leftTurn = false
    private var topTurn = false
    private var rightTurn = false
    private var bottomTurn = false

    //外圆区域
    private lateinit var outerCircleRectF: RectF

    //线条粗细
    private val borderStroke: Float

    init {
        val type = context.obtainStyledAttributes(attrs, R.styleable.SteeringWheelView)
        diameter = type.getDimension(
            R.styleable.SteeringWheelView_ctrl_diameter, 200f
        )
        val borderColor = type.getColor(
            R.styleable.SteeringWheelView_ctrl_borderColor, Color.CYAN
        )
        val backgroundColor = type.getColor(
            R.styleable.SteeringWheelView_ctrl_backgroundColor, Color.WHITE
        )
        borderStroke = type.getDimension(
            R.styleable.SteeringWheelView_ctrl_borderStroke, 5f
        )
        type.recycle()

        borderPaint = Paint()
        borderPaint.isAntiAlias = true
        borderPaint.isDither = true
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderStroke
        borderPaint.color = borderColor

        backgroundPaint = Paint()
        backgroundPaint.isAntiAlias = true
        backgroundPaint.isDither = true
        backgroundPaint.style = Paint.Style.FILL
        backgroundPaint.color = backgroundColor

        directionPaint = Paint()
        directionPaint.isAntiAlias = true
        directionPaint.isDither = true
        directionPaint.style = Paint.Style.FILL
        directionPaint.color = borderColor

        val layoutParams = LayoutParams(diameter.toInt(), diameter.toInt())

        val view = LayoutInflater.from(context).inflate(R.layout.widget_view_steering_wheel, this)

        val rootView = view.findViewById<RelativeLayout>(R.id.rootView)
        rootView.layoutParams = layoutParams

        val leftButton = view.findViewById<ImageButton>(R.id.leftButton)
        leftButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    leftTurn = true
                    listener?.onLeftTurn()
                }
                MotionEvent.ACTION_UP -> {
                    leftTurn = false
                    listener?.onActionTurnUp(Direction.LEFT)
                }
            }
            postInvalidate()
            true
        }

        view.findViewById<ImageButton>(R.id.topButton).setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    topTurn = true
                    listener?.onTopTurn()
                }
                MotionEvent.ACTION_UP -> {
                    topTurn = false
                    listener?.onActionTurnUp(Direction.TOP)
                }
            }
            postInvalidate()
            true
        }
        view.findViewById<ImageButton>(R.id.rightButton).setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    rightTurn = true
                    listener?.onRightTurn()
                }
                MotionEvent.ACTION_UP -> {
                    rightTurn = false
                    listener?.onActionTurnUp(Direction.RIGHT)
                }
            }
            postInvalidate()
            true
        }
        view.findViewById<ImageButton>(R.id.bottomButton).setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    bottomTurn = true
                    listener?.onBottomTurn()
                }
                MotionEvent.ACTION_UP -> {
                    bottomTurn = false
                    listener?.onActionTurnUp(Direction.BOTTOM)
                }
            }
            postInvalidate()
            true
        }
        view.findViewById<ImageButton>(R.id.centerButton).setOnClickListener {
            listener?.onCenterClicked()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //圆心位置
        canvasCenterX = w shr 1
        canvasCenterY = h shr 1

        val outerCircleRadius = diameter.toInt() shr 1 //半径

        // 大外圈区域
        outerCircleRectF = RectF(
            (canvasCenterX - outerCircleRadius).toFloat(),
            (canvasCenterY - outerCircleRadius).toFloat(),
            (canvasCenterX + outerCircleRadius).toFloat(),
            (canvasCenterY + outerCircleRadius).toFloat()
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val outerCircleRadius = diameter.toInt() shr 1 //半径
        //背景
        canvas.drawCircle(
            canvasCenterX.toFloat(),
            canvasCenterY.toFloat(),
            outerCircleRadius.toFloat(),
            backgroundPaint
        )

        //外圆圆圈
        canvas.drawCircle(
            canvasCenterX.toFloat(),
            canvasCenterY.toFloat(),
            outerCircleRadius.toFloat(),
            borderPaint
        )

        if (leftTurn) {
            canvas.drawArc(
                outerCircleRectF, (90 * 2 - 45).toFloat(), 90f, false, directionPaint
            )
        }

        if (topTurn) {
            canvas.drawArc(
                outerCircleRectF, (90 * 3 - 45).toFloat(), 90f, false, directionPaint
            )
        }

        if (rightTurn) {
            canvas.drawArc(outerCircleRectF, -45f, 90f, false, directionPaint)
        }

        if (bottomTurn) {
            canvas.drawArc(outerCircleRectF, 45f, 90f, false, directionPaint)
        }
    }

    enum class Direction {
        LEFT, TOP, RIGHT, BOTTOM
    }

    private var listener: OnWheelTouchListener? = null

    fun setOnWheelTouchListener(listener: OnWheelTouchListener?) {
        this.listener = listener
    }

    interface OnWheelTouchListener {
        /**
         * 中间
         */
        fun onCenterClicked()

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
         * 松开
         */
        fun onActionTurnUp(dir: Direction)
    }
}