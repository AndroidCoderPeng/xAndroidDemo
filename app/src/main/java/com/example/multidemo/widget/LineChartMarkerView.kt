package com.example.multidemo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import com.example.multidemo.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.pengxh.kt.lite.extensions.convertColor
import com.pengxh.kt.lite.extensions.convertDrawable
import com.pengxh.kt.lite.extensions.dp2px


class LineChartMarkerView(context: Context) : MarkerView(context, R.layout.popu_line_chart_marker) {

    private val timeView: TextView = findViewById(R.id.timeView)
    private val valueView: TextView = findViewById(R.id.valueView)
    private var xAxisDate = ArrayList<String>()
    private val dotBitmap = R.drawable.ic_chart_dot.convertDrawable(context)!!.toBitmap()
    private var arrowPaint = Paint()
    private val arrowHeight = 10.dp2px(context) // 箭头的高度
    private val arrowWidth = 15.dp2px(context) // 箭头的宽度
    private val arrowOffset = 2f.dp2px(context) //箭头偏移量

    init {
        arrowPaint.style = Paint.Style.FILL
        arrowPaint.isAntiAlias = true
        arrowPaint.color = R.color.mainColor.convertColor(context)
    }

    fun setXAxisDate(date: ArrayList<String>) {
        this.xAxisDate = date
    }

    override fun refreshContent(e: Entry, highlight: Highlight) {
        super.refreshContent(e, highlight)
        try {
            timeView.text = xAxisDate[(e.x).toInt()]
            valueView.text = "${"%.2f".format(e.y)}%LEL"
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width shr 1)).toFloat(), -height.toFloat())
    }

    /**
     * 绘制自适应界面的Marker
     * */
    override fun draw(canvas: Canvas, posX: Float, posY: Float) {
        if (chartView == null) {
            super.draw(canvas, posX, posY)
            return
        }

        val saveId = canvas.save()
        canvas.translate(posX, posY)

        canvas.drawBitmap(dotBitmap, -dotBitmap.width / 2f, -dotBitmap.height / 2f, null)

        drawArrow(canvas, posX, posY)

        draw(canvas)
        canvas.restoreToCount(saveId)
    }

    private fun drawArrow(canvas: Canvas, posX: Float, posY: Float) {
        val path = Path()
        if (posY < height + arrowHeight + dotBitmap.height / 2f) {
            //处理超过上边界
            canvas.translate(0f, height + arrowHeight + dotBitmap.height / 2f)
            if (posX > chartView.width - (width / 2f)) {
                //超过右边界
                canvas.translate(-(width / 2 - (chartView.width - posX)), 0f)
                path.moveTo(
                    width / 2 - (chartView.width - posX) - arrowOffset,
                    -(height + arrowHeight + arrowOffset)
                )
                path.lineTo(arrowWidth / 2f, -height.toFloat())
                path.lineTo(-arrowWidth / 2f, -height.toFloat())
                path.moveTo(
                    width / 2 - (chartView.width - posX) - arrowOffset,
                    -(height + arrowHeight + arrowOffset)
                )
            } else {
                if (posX > width / 2f) {
                    //在图表中间
                    path.moveTo(0f, -(height + arrowHeight).toFloat())
                    path.lineTo(arrowWidth / 2f, -height.toFloat())
                    path.lineTo(-arrowWidth / 2f, -height.toFloat())
                    path.lineTo(0f, -(height + arrowHeight).toFloat())
                } else {
                    //超过左边界
                    canvas.translate(width / 2f - posX, 0f)
                    path.moveTo(
                        -(width / 2f - posX) - arrowOffset,
                        -(height + arrowHeight + arrowOffset)
                    )
                    path.lineTo(arrowWidth / 2f, -height.toFloat())
                    path.lineTo(-arrowWidth / 2f, -height.toFloat())
                    path.moveTo(
                        -(width / 2f - posX) - arrowOffset,
                        -(height + arrowHeight + arrowOffset)
                    )
                }
            }
            canvas.drawPath(path, arrowPaint)
            canvas.translate(-width / 2f, -height.toFloat())
        } else {
            canvas.translate(0f, -height - arrowHeight - dotBitmap.height / 2f)
            if (posX < width / 2f) {
                //超过左边界
                canvas.translate(width / 2f - posX, 0f)
                path.moveTo(
                    -(width / 2f - posX) + arrowOffset,
                    height + arrowHeight + arrowOffset
                )
                path.lineTo(arrowWidth / 2f, height.toFloat())
                path.lineTo(-arrowWidth / 2f, height.toFloat())
                path.moveTo(
                    -(width / 2f - posX) + arrowOffset,
                    height + arrowHeight + arrowOffset
                )
            } else {
                if (posX > chartView.width - (width / 2f)) {
                    //超过右边界
                    canvas.translate(-(width / 2 - (chartView.width - posX)), 0f)
                    path.moveTo(
                        width / 2 - (chartView.width - posX) + arrowOffset,
                        height + arrowHeight + arrowOffset
                    )
                    path.lineTo(arrowWidth / 2f, height.toFloat())
                    path.lineTo(-arrowWidth / 2f, height.toFloat())
                    path.moveTo(
                        width / 2 - (chartView.width - posX) + arrowOffset,
                        height + arrowHeight + arrowOffset
                    )
                } else {
                    path.moveTo(0f, (height + arrowHeight).toFloat())
                    path.lineTo(arrowWidth / 2f, height.toFloat())
                    path.lineTo(-arrowWidth / 2f, height.toFloat())
                    path.moveTo(0f, (height + arrowHeight).toFloat())
                }
            }
            canvas.drawPath(path, arrowPaint)
            canvas.translate(-width / 2f, 0f)
        }
    }
}