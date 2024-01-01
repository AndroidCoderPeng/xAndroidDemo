package com.example.multidemo.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale
import kotlin.math.max

/**
 * RecyclerView吸顶分割线
 */
class RecyclerStickDecoration : RecyclerView.ItemDecoration() {

    private val kTag = "RecyclerStickDecoration"
    private val topGapPaint by lazy { Paint() }
    private val dividerPaint by lazy { Paint() }
    private val textPaint by lazy { TextPaint() }
    private val textRect by lazy { Rect() }

    private lateinit var context: Context
    private var topGap = 0
    private lateinit var listener: ViewGroupListener

    fun setContext(context: Context): RecyclerStickDecoration {
        this.context = context
        return this
    }

    fun setTopGap(topGap: Int): RecyclerStickDecoration {
        this.topGap = topGap
        return this
    }

    fun setViewGroupListener(listener: ViewGroupListener): RecyclerStickDecoration {
        this.listener = listener
        return this
    }

    fun build(): RecyclerStickDecoration {
        topGapPaint.isAntiAlias = true
        topGapPaint.color = Color.parseColor("#F1F1F1")

        dividerPaint.isAntiAlias = true
        dividerPaint.strokeWidth = 1f
        dividerPaint.color = Color.LTGRAY

        textPaint.isAntiAlias = true
        //字体占用topGap的75%
        textPaint.textSize = topGap * 0.75f
        textPaint.color = Color.BLACK
        return this
    }

    /**
     * 调整item顶部间距作为吸顶区域
     * */
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val pos = parent.getChildAdapterPosition(view)
        val groupTag = listener.groupTag(pos)
        if (groupTag < 0) {
            return
        }
        //同组的第一个才添加padding
        outRect.top = if (pos == 0 || isSameGroup(pos)) {
            topGap
        } else {
            0
        }
    }

    /**
     * 判断是否为同组数据
     */
    private fun isSameGroup(pos: Int): Boolean {
        return if (pos == 0) {
            true
        } else {
            val prevGroupId = listener.groupTag(pos - 1)
            val groupId = listener.groupTag(pos)
            prevGroupId != groupId
        }
    }

    /**
     * 画item分割线
     * */
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            c.drawLine(
                0f,
                view.bottom.toFloat(),
                view.width.toFloat(),
                view.bottom.toFloat(),
                dividerPaint
            )
        }
    }

    //吸顶效果
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        var lastGroupId: Long
        var groupId = -1L
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(view)
            lastGroupId = groupId
            groupId = listener.groupTag(position)
            if (groupId < 0 || groupId == lastGroupId) continue
            val firstLetter = listener.groupFirstLetter(position).uppercase(Locale.getDefault())
            if (firstLetter.isEmpty()) continue
            var viewBottom = max(topGap, view.top).toFloat()
            //下一个和当前不一样移动当前
            if (position + 1 < state.itemCount) {
                val nextGroupId = listener.groupTag(position + 1)
                //组内最后一个view进入了header
                if (nextGroupId != groupId && view.bottom < viewBottom) {
                    viewBottom = view.bottom.toFloat()
                }
            }
            //绘制吸顶底部背景
            c.drawRect(
                0f,
                viewBottom - topGap,
                view.width.toFloat(),
                viewBottom,
                topGapPaint
            )

            //绘制吸顶文字
            textPaint.getTextBounds(firstLetter, 0, firstLetter.length, textRect)
            val textWidth = textRect.width()
            c.drawText(
                firstLetter,
                view.left.toFloat() + textWidth,
                viewBottom - topGap / 4f,
                textPaint
            )
        }
    }

    /**
     * 点击某个字母将RecyclerView滑动到item顶部
     */
    inner class SmoothGroupTopScroller(context: Context) : LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }
    }

    interface ViewGroupListener {
        fun groupTag(position: Int): Long

        fun groupFirstLetter(position: Int): String
    }
}