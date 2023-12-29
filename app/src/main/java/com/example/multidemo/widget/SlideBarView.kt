package com.example.multidemo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.multidemo.R
import com.example.multidemo.util.StringHelper
import kotlin.math.abs

class SlideBarView constructor(context: Context, attrs: AttributeSet) : View(context, attrs),
    View.OnTouchListener {

    private val kTag = "SlideBarView"
    private val letterArray = arrayOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    )
    private var dataSet: MutableList<String> = ArrayList()

    private val viewWidth: Float
    private val roundRadius: Float
    private val textSize: Float
    private val textColor: Int

    private var centerX = 0f
    private var centerY = 0f

    private var touchIndex = -1
    private var letterHeight = 0
    private var showBackground = true

    private lateinit var tickPaint: Paint
    private lateinit var ancherPaint: Paint
    private lateinit var viewBgRectF: RectF
    private lateinit var backgroundPaint: Paint
    private lateinit var textPaint: TextPaint
    private lateinit var textRect: Rect

    init {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.SlideBarView)
        viewWidth = attr.getDimension(R.styleable.SlideBarView_slide_width, 30f)
        roundRadius = viewWidth / 2f
        textSize = attr.getDimension(R.styleable.SlideBarView_slide_textSize, 18f)
        textColor = attr.getColor(R.styleable.SlideBarView_slide_textColor, Color.LTGRAY)
        attr.recycle()

        //初始化画笔
        initPaint()
        //触摸事件
        setOnTouchListener(this)
    }


    private fun initPaint() {
        tickPaint = Paint()
        tickPaint.color = Color.DKGRAY
        tickPaint.style = Paint.Style.STROKE
        tickPaint.strokeWidth = 1f
        tickPaint.isAntiAlias = true

        ancherPaint = Paint()
        ancherPaint.color = Color.RED
        ancherPaint.isAntiAlias = true

        //背景色画笔
        backgroundPaint = Paint()
        backgroundPaint.color = Color.LTGRAY
        backgroundPaint.isAntiAlias = true

        //文字画笔
        textPaint = TextPaint()
        textPaint.isAntiAlias = true
        textPaint.textSize = textSize
        textPaint.color = textColor
        textPaint.textAlign = Paint.Align.CENTER
        textRect = Rect()
    }

    fun setSlideDataSet(dataSet: MutableList<String>) {
        this.dataSet = dataSet
    }

    fun getFirstLetterIndex(letter: String): Int {
        var index = -1
        for (i in dataSet.indices) {
            val firstLetter = StringHelper.obtainHanYuPinyin(dataSet[i])
            if (letter == firstLetter) {
                index = i
                //当有相同的首字母之后就跳出循环
                break
            }
        }
        return index
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = (w shr 1).toFloat()
        centerY = (h shr 1).toFloat()

        // 设置背景圆角矩形外边框范围
        viewBgRectF = RectF(-centerX, -centerY, centerX, centerY)

        // 每个字母的高度
        letterHeight = h / letterArray.size
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        // 获取宽
        val mWidth = if (widthSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            widthSpecSize
        } else {
            // wrap_content，外边界宽
            viewWidth.toInt()
        }
        setMeasuredDimension(mWidth, heightSpecSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        /**
         * 画布移到中心位置，方便绘制一系列图形
         */
        canvas.translate(centerX, centerY)
        if (showBackground) {
            canvas.drawRoundRect(viewBgRectF, roundRadius, roundRadius, backgroundPaint)
        }

        for (i in letterArray.indices) {
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

            //绘制辅助线
//            drawGuides(canvas, i)

            //绘制文字
            val letter = letterArray[i]

            /**
             *   |
             * --|------------------------>
             *   |   ---
             *   |  |   |
             *   |  | · | h
             *   |  |   |
             *   |  |---|
             *   |  |   |
             *   |  | · | h
             *   |  |   |
             *   |  |---|
             *   |  |   |
             *   |  | · | h
             *   |  |   |
             *   |   ---
             *   ∨
             *
             *   每个字母的纵坐标 = View上边纵坐标+每个字母所在位置的纵坐标
             *   每个字母所在位置的纵坐标 = (2 * i + 1) / 2 * 字母平均高度
             * */

            //每个文字左下角坐标
            val textY = -centerY + (2 * i + 1) * 0.5 * letterHeight

            //计算文字高度
            textPaint.getTextBounds(letter, 0, letter.length, textRect)
            val textHeight = textRect.height()

            canvas.drawText(letter, 0f, textY.toFloat() + textHeight / 2, textPaint)
        }
    }

    /**
     * 辅助线
     * */
    private fun drawGuides(canvas: Canvas, i: Int) {
        canvas.drawCircle(
            0f,
            (-centerY + (2 * i + 1) * 0.5 * letterHeight).toFloat(),
            5f,
            ancherPaint
        )

        canvas.drawLine(
            -centerX,
            -centerY + letterHeight * (i + 1).toFloat(),
            centerX,
            -centerY + letterHeight * (i + 1).toFloat(),
            tickPaint
        )
    }

    //侧边栏滑动事件
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val y = abs(event.y) //取绝对值，不然y可能会取到负值
                val index = (y / letterHeight).toInt() //字母的索引
                if (index != touchIndex) {
                    touchIndex = index.coerceAtMost(letterArray.size - 1)
                    //点击设置中间字母
                    onLetterIndexChangeListener?.onLetterIndexChange(letterArray[touchIndex])
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

    private var onLetterIndexChangeListener: OnLetterIndexChangeListener? = null

    fun setOnLetterIndexChangeListener(listener: OnLetterIndexChangeListener?) {
        onLetterIndexChangeListener = listener
    }

    interface OnLetterIndexChangeListener {
        fun onLetterIndexChange(letter: String)
    }
}