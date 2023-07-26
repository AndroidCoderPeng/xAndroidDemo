package com.example.multidemo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import com.pengxh.kt.lite.extensions.dp2px

/**
 * @author : Pengxh
 * @time : 2021/4/14 8:45
 * @email : 290677893@qq.com
 * @apiNote :人脸框
 */
class FaceDetectView constructor(private val ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {

    private val borderPaint by lazy { Paint() }

    init {
        borderPaint.isAntiAlias = true
        borderPaint.color = Color.RED
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 5f //设置线宽
        borderPaint.isAntiAlias = true
    }

    private var pointF = PointF()
    private var eyesDistance = 0f

    fun updateFacePosition(pointF: PointF, eyesDistance: Float) {
        this.pointF = pointF
        this.eyesDistance = eyesDistance.dp2px(ctx).toFloat()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(
            ((pointF.x.dp2px(ctx).toFloat() - eyesDistance)),
            ((pointF.y.dp2px(ctx).toFloat() - eyesDistance)),
            ((pointF.x.dp2px(ctx).toFloat() + eyesDistance / 2)),//宽度减半，画矩形
            ((pointF.y.dp2px(ctx).toFloat() + eyesDistance)),
            borderPaint
        )
    }
}