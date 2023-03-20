package com.example.mutidemo.widget

import android.content.Context
import android.graphics.*
import android.hardware.Camera
import android.util.AttributeSet
import android.view.View
import com.pengxh.kt.lite.extensions.dp2px

/**
 * @author : Pengxh
 * @time : 2021/4/14 8:45
 * @email : 290677893@qq.com
 * @apiNote :人脸框
 */
class FaceDetectView constructor(ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {

    private val borderPaint by lazy { Paint() }
    private var mtx: Matrix? = null
    private var faces: Array<Camera.Face>
    private var isClear = false

    init {
        borderPaint.color = Color.GREEN
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 2f.dp2px(ctx).toFloat()
        //设置抗锯齿
        borderPaint.isAntiAlias = true
        faces = arrayOf()
    }

    fun updateFace(mtx: Matrix, faces: Array<Camera.Face>) {
        this.mtx = mtx
        this.faces = faces
        invalidate()
    }

    fun removeRect() {
        isClear = true
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.setMatrix(mtx)
        for (face in faces) {
            canvas.drawRect(face.rect, borderPaint)
            if (face.leftEye != null) canvas.drawPoint(
                face.leftEye.x.toFloat(),
                face.leftEye.y.toFloat(),
                borderPaint
            )
            if (face.rightEye != null) canvas.drawPoint(
                face.rightEye.x.toFloat(),
                face.rightEye.y.toFloat(),
                borderPaint
            )
            if (face.mouth != null) canvas.drawPoint(
                face.mouth.x.toFloat(),
                face.mouth.y.toFloat(),
                borderPaint
            )
        }
        if (isClear) {
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR)
            isClear = false
        }
    }
}