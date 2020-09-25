package com.example.mutidemo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mutidemo.R;

public class SlideBarView extends View {

    private static final String TAG = "SlideBarView";
    private static final String[] LETTER = {"#", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private static final int viewWidth = 20;
    private Context mContext;
    private float centerX;//中心x
    private int textSize;
    private int textColor;
    private TextPaint textPaint;//文字画笔
    private int mHeight;//控件的实际尺寸

    public SlideBarView(Context context) {
        this(context, null, 0);
    }

    public SlideBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideBarView, defStyleAttr, 0);

        textSize = a.getDimensionPixelOffset(R.styleable.SlideBarView_slide_textSize
                , sp2px(context, 18));
        textColor = a.getColor(R.styleable.SlideBarView_slide_textColor, Color.LTGRAY);
        a.recycle();

        //初始化画笔
        initPaint();
    }

    private void initPaint() {
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //圆心位置
        centerX = w >> 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        // 获取宽
        int mWidth;
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            mWidth = widthSpecSize;
        } else {
            // wrap_content
            mWidth = dp2px(mContext, viewWidth);
        }
        // 获取高
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            mHeight = heightSpecSize;
        } else {
            // wrap_content
            mHeight = dp2px(mContext, viewWidth);
        }
        // 设置该view的宽高
        setMeasuredDimension(mWidth, mHeight);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //计算每个字母的坐标
        int letterHeight = mHeight / LETTER.length;
        /**
         * 每个字母的X坐标=屏幕宽度-(mWidth/2)
         * 每个字母的Y坐标=i*letterHeight+(letterHeight/2)
         * */
        for (int i = 0; i < LETTER.length; i++) {
            int y = i * letterHeight + letterHeight >> 1;

            //绘制文字
            String letter = LETTER[i];
            Rect textRect = new Rect();
            textPaint.getTextBounds(letter, 0, letter.length(), textRect);
            int textWidth = textRect.width();
            int textHeight = textRect.height();
            //计算文字左下角坐标
            float textX = centerX - (textWidth >> 1);
            float textY = y + (textHeight * 2);
            canvas.drawText(letter, textX, textY, textPaint);
        }
    }

    /**
     * sp转换成px
     */
    private int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * dp转px
     */
    private int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }
}
