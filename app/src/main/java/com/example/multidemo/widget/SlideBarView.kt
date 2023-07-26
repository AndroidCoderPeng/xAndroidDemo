package com.example.multidemo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.multidemo.R
import com.example.multidemo.util.StringHelper
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.sp2px
import kotlin.math.abs

class SlideBarView constructor(
    private val ctx: Context, attrs: AttributeSet
) : View(ctx, attrs), View.OnTouchListener {

    private var data: List<String> = ArrayList()
    private val textSize: Int
    private val textColor: Int
    private val viewWidth = 25
    private val letterArray = arrayOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    )
    private var centerX = 0f
    private var radius = 0f
    private var mHeight = 0
    private var touchIndex = -1
    private var letterHeight = 0
    private var showBackground = false
    private lateinit var textPaint: TextPaint
    private lateinit var backgroundPaint: Paint

    init {
        val a = ctx.obtainStyledAttributes(attrs, R.styleable.SlideBarView)
        textSize = a.getDimensionPixelOffset(
            R.styleable.SlideBarView_slide_textSize, 18f.sp2px(ctx)
        )
        textColor = a.getColor(R.styleable.SlideBarView_slide_textColor, Color.LTGRAY)
        a.recycle()

        //初始化画笔
        initPaint()
        //触摸事件
        setOnTouchListener(this)
    }

    fun setData(cities: List<String>) {
        data = cities
    }

    private fun initPaint() {
        //背景色画笔
        backgroundPaint = Paint()
        backgroundPaint.color = Color.parseColor("#3F3F3F")
        backgroundPaint.isAntiAlias = true

        //文字画笔
        textPaint = TextPaint()
        textPaint.isAntiAlias = true
        textPaint.textSize = textSize.toFloat()
        textPaint.color = textColor
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = (w shr 1).toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        // 获取宽
        val mWidth: Int = viewWidth.toFloat().dp2px(ctx)
        // 获取高
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            mHeight = heightSpecSize
        }
        radius = (mWidth shr 1).toFloat()
        // 设置该view的宽高
        setMeasuredDimension(mWidth, mHeight)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        letterHeight = mHeight / letterArray.size
        if (showBackground) {
            //绘制进度条背景，圆角矩形
            val bgRectF = RectF()
            bgRectF.left = (centerX - radius) * 2
            bgRectF.top = 0f
            bgRectF.right = centerX * 2
            bgRectF.bottom = mHeight.toFloat()
            canvas.drawRoundRect(bgRectF, radius, radius, backgroundPaint)
        }
        for (i in letterArray.indices) {
            val y = (i + 1) * letterHeight //每个字母的占位高度(不是字体高度)

            //字母变色
            if (touchIndex == i) {
                //让当前字母变色
                textPaint.color = Color.parseColor("#00CB87")
                textPaint.typeface = Typeface.DEFAULT_BOLD
            } else {
                //其他字母不变色
                textPaint.color = textColor
                textPaint.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            }

            //绘制文字
            val letter = letterArray[i]
            val textRect = Rect()
            textPaint.getTextBounds(letter, 0, letter.length, textRect)
            val textWidth = textRect.width()
            val textHeight = textRect.height()
            //计算文字左下角坐标
            val textX = centerX - (textWidth shr 1)
            val textY = (y - (textHeight shr 1)).toFloat()
            canvas.drawText(letter, textX, textY, textPaint)
        }
    }

    //侧边栏滑动事件
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val y: Float = abs(event.y) //取绝对值，不然y可能会取到负值
                val index = (y / letterHeight).toInt() //字母的索引
                if (index != touchIndex) {
                    touchIndex = index.coerceAtMost(letterArray.size - 1)
                    //点击设置中间字母
                    onIndexChangeListener?.onIndexChange(letterArray[touchIndex])
                    invalidate()
                }
                showBackground = true
            }
            MotionEvent.ACTION_UP -> {
                showBackground = false
                touchIndex = -1
                invalidate()
            }
        }
        return true
    }

    private var onIndexChangeListener: OnIndexChangeListener? = null

    fun setOnIndexChangeListener(listener: OnIndexChangeListener?) {
        onIndexChangeListener = listener
    }

    interface OnIndexChangeListener {
        fun onIndexChange(letter: String)
    }

    fun obtainFirstLetterIndex(letter: String): Int {
        var index = -1
        for (i in data.indices) {
            val firstLetter = StringHelper.obtainHanYuPinyin(data[i])
            if (letter == firstLetter) {
                index = i
                //当有相同的首字母之后就跳出循环
                break
            }
        }
        return index
    }
}