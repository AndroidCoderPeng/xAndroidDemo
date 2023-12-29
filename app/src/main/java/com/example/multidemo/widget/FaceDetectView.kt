package com.example.multidemo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * @author : Pengxh
 * @time : 2021/4/14 8:45
 * @email : 290677893@qq.com
 * @apiNote :人脸框
 */
class FaceDetectView constructor(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val kTag = "FaceDetectView"
    private var borderPaint = Paint()
    private var midPointPaint = Paint()

    init {
        borderPaint.isAntiAlias = true
        borderPaint.color = Color.RED
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 5f //设置线宽
        borderPaint.isAntiAlias = true

        midPointPaint.isAntiAlias = true
        midPointPaint.color = Color.RED
        midPointPaint.isAntiAlias = true
    }

    private var eyeRectF = RectF()
    private var midPointX = 0f
    private var midPointY = 0f

    fun updateFacePosition(eyeRectF: RectF, x: Float, y: Float) {
        this.eyeRectF = eyeRectF
        this.midPointX = x
        this.midPointY = y
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(eyeRectF, borderPaint)

        canvas.drawCircle(midPointX, midPointY, 15f, midPointPaint)
    }
}