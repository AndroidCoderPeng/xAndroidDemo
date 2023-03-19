package com.example.mutidemo.widget

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.example.mutidemo.R
import com.pengxh.kt.lite.extensions.convertColor
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.sp2px

class EasyProgressBar constructor(
    private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(mContext, attrs, defStyleAttr) {

    private val backgroundColor: Int
    private val startColor: Int
    private val endColor: Int
    private val textColor: Int
    private var viewHeight = 0f
    private var viewWidth = 0f
    private var radius = 0f
    private var centerY = 0f
    var maxProgress = 0f
    private var currentProgress = 0f
    private var text: String
    private lateinit var backgroundPaint: Paint
    private lateinit var foregroundPaint: Paint
    private lateinit var textPaint: TextPaint

    init {
        val a = mContext.obtainStyledAttributes(attrs, R.styleable.EasyProgressBar, defStyleAttr, 0)
        backgroundColor = a.getColor(
            R.styleable.EasyProgressBar_progress_backgroundColor,
            R.color.lightGray.convertColor(mContext)
        )
        startColor = a.getColor(
            R.styleable.EasyProgressBar_progress_startColor,
            R.color.mainColor.convertColor(mContext)
        )
        endColor = a.getColor(
            R.styleable.EasyProgressBar_progress_endColor,
            R.color.mainColor.convertColor(mContext)
        )
        textColor = a.getColor(
            R.styleable.EasyProgressBar_progress_textColor,
            R.color.white.convertColor(mContext)
        )
        text = a.getString(R.styleable.EasyProgressBar_progress_text).toString()
        a.recycle()
        //初始化画笔
        initPaint()
    }

    private fun initPaint() {
        //背景色画笔
        backgroundPaint = Paint()
        backgroundPaint.color = backgroundColor
        backgroundPaint.isAntiAlias = true

        //前景色画笔
        foregroundPaint = Paint()
        foregroundPaint.isAntiAlias = true

        //文字画笔
        textPaint = TextPaint()
        textPaint.color = textColor
        textPaint.isAntiAlias = true
        textPaint.textSize = 14f.sp2px(mContext).toFloat()
    }

    //计算出中心位置，便于定位
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //圆心位置
        centerY = (h shr 1).toFloat()
    }

    //计算控件实际大小
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        // 获取宽
        viewWidth = if (widthSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            widthSpecSize.toFloat()
        } else {
            // wrap_content
            300f.dp2px(mContext).toFloat()
        }
        // 获取高
        viewHeight = if (heightSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            heightSpecSize.toFloat()
        } else {
            // wrap_content
            20f.dp2px(mContext).toFloat()
        }
        // 设置该view的宽高
        radius = viewHeight
        setMeasuredDimension(viewWidth.toInt(), viewHeight.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制进度条背景，圆角矩形
        val bgRectF = RectF()
        bgRectF.left = 0f
        bgRectF.top = 0f
        bgRectF.right = viewWidth
        bgRectF.bottom = viewHeight
        canvas.drawRoundRect(bgRectF, radius, radius, backgroundPaint)

        //绘制进度条前景色，圆角矩形
        val fgRectF = RectF()
        val ratio = currentProgress / maxProgress
        fgRectF.left = 0f
        fgRectF.top = 0f
        fgRectF.right = viewWidth * ratio
        fgRectF.bottom = viewHeight
        val colors = intArrayOf(startColor, endColor)
        val position = floatArrayOf(0.25f, 0.75f)
        val linearGradient = LinearGradient(
            0f, viewHeight, viewWidth, viewHeight,
            colors, position, Shader.TileMode.CLAMP
        )
        foregroundPaint.shader = linearGradient
        canvas.drawRoundRect(fgRectF, radius.toFloat(), radius.toFloat(), foregroundPaint)

        //绘制文字
        val textRect = Rect()
        text = (100 * ratio).toInt().toString() + "%"
        textPaint.getTextBounds(text, 0, text.length, textRect)
        val textWidth = textRect.width()
        val textHeight = textRect.height()
        //计算文字左下角坐标
        val textX = (viewWidth * ratio - textWidth * 1.25).toFloat()
        val textY = centerY + (textHeight shr 1)
        canvas.drawText(text, textX, textY, textPaint)

        //刷新控件进度
        invalidate()
    }

    /**
     * 设置当前的进度值
     */
    fun setCurrentProgress(progress: Float) {
        currentProgress = progress.coerceAtMost(maxProgress)
        invalidate()
    }
}