package com.example.multidemo.util

import android.view.View
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.pengxh.kt.lite.extensions.dp2px
import kotlin.math.abs
import kotlin.math.max


class GalleryScaleHelper {

    private val kTag = "GalleryScaleHelper"
    private val snapHelper by lazy { LinearSnapHelper() }

    // 卡片的padding, 卡片间的距离等于2倍的pagePadding
    private val pagePadding = 15

    // 左边卡片显示大小
    private val leftCardShowWidth = 15

    // 卡片宽度
    private var cardWidth = 0
    private var currentItemOffset = 0

    //当前卡片的index
    private var currentItemPos = 0

    // 两边视图缩放比例
    private val scale = 0.9f

    fun attachToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.post {
            val galleryWidth = recyclerView.width
            cardWidth = galleryWidth - 2 * (pagePadding + leftCardShowWidth).toFloat()
                .dp2px(recyclerView.context)
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // dx>0则表示右滑, dx<0表示左滑, dy<0表示上滑, dy>0表示下滑
                if (dx != 0) {
                    currentItemOffset += dx

                    currentItemPos = currentItemOffset / cardWidth

                    val offset = currentItemOffset - currentItemPos * cardWidth
                    val percent = max(abs(offset) * 1.0 / cardWidth, 0.0001).toFloat()

                    var leftView: View? = null
                    var rightView: View? = null

                    recyclerView.layoutManager?.apply {
                        if (currentItemPos > 0) {
                            leftView = findViewByPosition(currentItemPos - 1)
                        }
                        val currentView = findViewByPosition(currentItemPos)
                        recyclerView.adapter?.apply {
                            if (currentItemPos < itemCount - 1) {
                                rightView = findViewByPosition(currentItemPos + 1)
                            }
                        }

                        leftView?.apply {
                            scaleY = (1 - scale) * percent + scale
                        }
                        currentView?.apply {
                            scaleY = (scale - 1) * percent + 1
                        }
                        rightView?.apply {
                            scaleY = (1 - scale) * percent + scale
                        }
                    }
                }
            }
        })
        snapHelper.attachToRecyclerView(recyclerView)
    }

    fun getCurrentIndex(): Int {
        return currentItemPos
    }
}