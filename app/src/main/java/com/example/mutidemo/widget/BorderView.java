package com.example.mutidemo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.pengxh.app.multilib.utils.SizeUtil;

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/11/18 20:31
 */
public class BorderView extends AppCompatImageView {

    private static final String TAG = "BorderView";
    private static final String TEXT = "请将银行卡置于方框内，便于识别卡号";
    private final Paint borderPaint;
    private final Paint textPaint;
    private float centerX, centerY;

    public BorderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(Color.GREEN);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(5f);//设置线宽
        borderPaint.setAntiAlias(true);
        borderPaint.setAlpha(255);

        //文字画笔
        textPaint = new TextPaint();
        textPaint.setColor(Color.GREEN);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(SizeUtil.sp2px(context, 16));
        textPaint.setAlpha(255);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //中心位置
        centerX = w >> 1;
        centerY = h >> 1;
        Log.d(TAG, "中心位置: " + "[" + centerX + "," + centerY + "]");
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制文字
        Rect textRect = new Rect();
        textPaint.getTextBounds(TEXT, 0, TEXT.length(), textRect);
        int textWidth = textRect.width();
        int textHeight = textRect.height();
        //计算文字左下角坐标
        float textX = centerX - (textWidth >> 1);
        float textY = centerY + (textHeight >> 1);
        canvas.drawText("请将银行卡置于方框内，便于识别卡号", textX, textY, textPaint);
        //绘制圆角矩形
        RectF rectF = new RectF((centerX - 425), centerY - 225, centerX + 425, centerY + 225);
        canvas.drawRoundRect(rectF, 25, 25, borderPaint);//第二个参数是x半径，第三个参数是y半径
    }
}
