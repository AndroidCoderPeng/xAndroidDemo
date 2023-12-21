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
    private val borderPaint by lazy { Paint() }

    init {
        borderPaint.isAntiAlias = true
        borderPaint.color = Color.RED
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 5f //设置线宽
        borderPaint.isAntiAlias = true
    }

    private var eyeRectF = RectF()

    fun updateFacePosition(eyeRectF: RectF) {
        this.eyeRectF = eyeRectF
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(eyeRectF, borderPaint)
    }
}