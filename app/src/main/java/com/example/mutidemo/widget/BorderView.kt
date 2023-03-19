package com.example.mutidemo.widget

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import com.pengxh.kt.lite.extensions.sp2px

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/11/18 20:31
 */
class BorderView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {

    private val kTag = "BorderView"
    private val TEXT = "请将银行卡置于方框内，便于识别卡号"
    private val borderPaint: Paint = Paint()
    private val textPaint: Paint
    private var centerX = 0f
    private var centerY = 0f

    init {
        borderPaint.isAntiAlias = true
        borderPaint.color = Color.GREEN
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 5f //设置线宽
        borderPaint.isAntiAlias = true
        borderPaint.alpha = 255

        //文字画笔
        textPaint = TextPaint()
        textPaint.color = Color.GREEN
        textPaint.isAntiAlias = true
        textPaint.textSize = 16f.sp2px(context).toFloat()
        textPaint.alpha = 255
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //中心位置
        centerX = (w shr 1).toFloat()
        centerY = (h shr 1).toFloat()
        Log.d(kTag, "中心位置: [$centerX,$centerY]")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制文字
        val textRect = Rect()
        textPaint.getTextBounds(TEXT, 0, TEXT.length, textRect)
        val textWidth = textRect.width()
        val textHeight = textRect.height()
        //计算文字左下角坐标
        val textX = centerX - (textWidth shr 1)
        val textY = centerY + (textHeight shr 1)
        canvas.drawText("请将银行卡置于方框内，便于识别卡号", textX, textY, textPaint)
        //绘制圆角矩形
        val rectF = RectF(centerX - 425, centerY - 225, centerX + 425, centerY + 225)
        canvas.drawRoundRect(rectF, 25f, 25f, borderPaint) //第二个参数是x半径，第三个参数是y半径
    }
}