package com.example.android.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.face.Face
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.getScreenWidth

/**
 * @author : Pengxh
 * @time : 2021/4/14 8:45
 * @email : 290677893@qq.com
 * @apiNote :人脸框
 */
class FaceDetectView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val kTag = "FaceDetectView"
    private val borderPaint by lazy { Paint() }
    private val rect by lazy { Rect() }
    private val screenWidth by lazy { context.getScreenWidth() }
    private var faces: MutableList<Face> = ArrayList()

    init {
        borderPaint.color = Color.GREEN
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 2f.dp2px(context) //设置线宽
        borderPaint.isAntiAlias = true
    }

    fun updateFacePosition(faces: MutableList<Face>) {
        this.faces = faces
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        faces.forEach {
            val box = it.boundingBox
            // 镜像翻转 left 和 right
            val mirroredLeft = screenWidth - box.right.dp2px(context)
            val mirroredRight = screenWidth - box.left.dp2px(context)
            rect.set(
                mirroredLeft,
                box.top.dp2px(context),
                mirroredRight,
                box.bottom.dp2px(context)
            )
            canvas.drawRect(rect, borderPaint)
        }
    }
}