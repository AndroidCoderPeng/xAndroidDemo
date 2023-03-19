package com.example.mutidemo.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.mutidemo.callback.DecorationCallback
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.sp2px
import java.util.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/9/27 22:47
 */
class VerticalItemDecoration(private val context: Context, decorationCallback: DecorationCallback) :
    RecyclerView.ItemDecoration() {

    private val topLinePaint: Paint
    private val bottomLinePaint: Paint
    private val textPaint: TextPaint
    private val callback: DecorationCallback
    private val topGap: Int

    init {
        callback = decorationCallback
        topLinePaint = Paint()
        topLinePaint.isAntiAlias = true
        topLinePaint.color = Color.parseColor("#F1F1F1")
        bottomLinePaint = Paint()
        bottomLinePaint.isAntiAlias = true
        bottomLinePaint.color = Color.LTGRAY
        textPaint = TextPaint()
        textPaint.isAntiAlias = true
        textPaint.textSize = 20f.sp2px(context).toFloat()
        textPaint.color = Color.BLACK
        textPaint.textAlign = Paint.Align.LEFT
        topGap = 30f.dp2px(context)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val pos: Int = parent.getChildAdapterPosition(view)
        val groupTag: Long = callback.getGroupTag(pos)
        if (groupTag < 0) return
        if (pos == 0 || isFirstInGroup(pos)) { //同组的第一个才添加padding
            outRect.top = topGap
        } else {
            outRect.top = 0
        }
    }

    /**
     * 判断是否为同组数据
     */
    private fun isFirstInGroup(pos: Int): Boolean {
        return if (pos == 0) {
            true
        } else {
            val prevGroupId: Long = callback.getGroupTag(pos - 1)
            val groupId: Long = callback.getGroupTag(pos)
            prevGroupId != groupId
        }
    }

    //画分割线
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount: Int = parent.childCount
        for (i in 0 until childCount) {
            val view: View = parent.getChildAt(i)
            c.drawRect(
                15f.dp2px(context).toFloat(),
                view.bottom.toFloat(),
                parent.width.toFloat(),
                (view.bottom + 1).toFloat(),
                bottomLinePaint
            )
        }
    }

    //吸顶效果
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val itemCount: Int = state.itemCount
        val childCount: Int = parent.childCount
        val left: Int = parent.paddingLeft + 15f.dp2px(context)
        val right: Int = parent.width
        var preGroupId: Long
        var groupId: Long = -1
        for (i in 0 until childCount) {
            val view: View = parent.getChildAt(i)
            val position: Int = parent.getChildAdapterPosition(view)
            preGroupId = groupId
            groupId = callback.getGroupTag(position)
            if (groupId < 0 || groupId == preGroupId) continue
            val firstLetter: String =
                callback.getGroupFirstLetter(position).uppercase(Locale.getDefault())
            if (TextUtils.isEmpty(firstLetter)) continue
            val viewBottom = view.bottom
            var textY = topGap.coerceAtLeast(view.top).toFloat()
            if (position + 1 < itemCount) { //下一个和当前不一样移动当前
                val nextGroupId: Long = callback.getGroupTag(position + 1)
                if (nextGroupId != groupId && viewBottom < textY) { //组内最后一个view进入了header
                    textY = viewBottom.toFloat()
                }
            }
            c.drawRect(0f, textY - topGap, right.toFloat(), textY, topLinePaint)
            c.drawText(
                firstLetter,
                left.toFloat(),
                textY - 7f.dp2px(context),
                textPaint
            )
        }
    }

    /**
     * 点击某个字母将RecyclerView滑动到item顶部
     */
    class TopSmoothScroller(context: Context?) : LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int = SNAP_TO_START
    }
}