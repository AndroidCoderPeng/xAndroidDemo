package com.example.multidemo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.face.Face
import com.pengxh.kt.lite.extensions.dp2px

/**
 * @author : Pengxh
 * @time : 2021/4/14 8:45
 * @email : 290677893@qq.com
 * @apiNote :人脸框
 */
class FaceDetectView constructor(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val kTag = "FaceDetectView"
    private val borderPaint by lazy { Paint() }
    private var faces: MutableList<Face> = ArrayList()

    init {
        borderPaint.color = Color.GREEN
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 3f.dp2px(context) //设置线宽
        borderPaint.isAntiAlias = true
    }

    fun updateFacePosition(faces: MutableList<Face>) {
        this.faces = faces
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        faces.forEach {
            canvas.drawRect(it.boundingBox, borderPaint)
        }
    }
}