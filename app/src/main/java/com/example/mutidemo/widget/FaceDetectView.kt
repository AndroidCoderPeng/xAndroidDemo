package com.example.mutidemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.pengxh.androidx.lite.utils.DeviceSizeUtil;


/**
 * @author : Pengxh
 * @time : 2021/4/14 8:45
 * @email : 290677893@qq.com
 * @apiNote :人脸框
 **/
public class FaceDetectView extends View {
    private Matrix matrix;
    private Paint mPaint;
    private Camera.Face[] faces;
    private boolean isClear;

    public FaceDetectView(Context context) {
        super(context);
        init(context);
    }

    public FaceDetectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FaceDetectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(DeviceSizeUtil.dp2px(context, 2));
        //设置抗锯齿
        mPaint.setAntiAlias(true);
        faces = new Camera.Face[]{};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setMatrix(matrix);
        for (Camera.Face face : faces) {
            if (face == null) break;
            canvas.drawRect(face.rect, mPaint);
            if (face.leftEye != null)
                canvas.drawPoint(face.leftEye.x, face.leftEye.y, mPaint);
            if (face.rightEye != null)
                canvas.drawPoint(face.rightEye.x, face.rightEye.y, mPaint);
            if (face.mouth != null)
                canvas.drawPoint(face.mouth.x, face.mouth.y, mPaint);
        }
        if (isClear) {
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
            isClear = false;
        }
    }

    public void updateFace(Matrix matrix, Camera.Face[] faces) {
        this.matrix = matrix;
        this.faces = faces;
        invalidate();
    }

    public void removeRect() {
        isClear = true;
        invalidate();
    }
}
