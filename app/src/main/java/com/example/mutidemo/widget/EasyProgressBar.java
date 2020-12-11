package com.example.mutidemo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mutidemo.R;

public class EasyProgressBar extends View {

    private static final String TAG = "EasyProgressBar";
    private int viewHeight;
    private int viewWidth;
    private int radius;
    private Context mContext;
    private int backgroundColor;
    private int startColor;
    private int endColor;
    private int textColor;
    private String text;
    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private TextPaint textPaint;
    private float centerY;
    private float maxProgress;
    private float currentProgress;

    public EasyProgressBar(Context context) {
        this(context, null, 0);
    }

    public EasyProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EasyProgressBar, defStyleAttr, 0);
        backgroundColor = a.getColor(R.styleable.EasyProgressBar_progress_backgroundColor
                , getResourcesColor(context, R.color.lightGray));
        startColor = a.getColor(R.styleable.EasyProgressBar_progress_startColor
                , getResourcesColor(context, R.color.sky));
        endColor = a.getColor(R.styleable.EasyProgressBar_progress_endColor
                , getResourcesColor(context, R.color.sky));
        textColor = a.getColor(R.styleable.EasyProgressBar_progress_textColor
                , getResourcesColor(context, R.color.white));
        text = a.getString(R.styleable.EasyProgressBar_progress_text);
        a.recycle();
        //初始化画笔
        initPaint();
    }

    private void initPaint() {
        //背景色画笔
        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setAntiAlias(true);

        //前景色画笔
        foregroundPaint = new Paint();
        foregroundPaint.setAntiAlias(true);

        //文字画笔
        textPaint = new TextPaint();
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(sp2px(mContext, 14));
    }

    //计算出中心位置，便于定位
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //圆心位置
        centerY = h >> 1;
    }

    //计算控件实际大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        // 获取宽
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            viewWidth = widthSpecSize;
        } else {
            // wrap_content
            viewWidth = dp2px(mContext, 300);
        }
        // 获取高
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            viewHeight = heightSpecSize;
        } else {
            // wrap_content
            viewHeight = dp2px(mContext, 20);
        }
        // 设置该view的宽高
        this.radius = viewHeight;
        setMeasuredDimension(viewWidth, viewHeight);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制进度条背景，圆角矩形
        RectF bgRectF = new RectF();
        bgRectF.left = 0;
        bgRectF.top = 0;
        bgRectF.right = viewWidth;
        bgRectF.bottom = viewHeight;
        canvas.drawRoundRect(bgRectF, radius, radius, backgroundPaint);

        //绘制进度条前景色，圆角矩形
        RectF fgRectF = new RectF();
        float ratio = currentProgress / maxProgress;
        fgRectF.left = 0;
        fgRectF.top = 0;
        fgRectF.right = viewWidth * ratio;
        fgRectF.bottom = viewHeight;

        int[] colors = {startColor, endColor};
        float[] position = {0.25f, 0.75f};
        LinearGradient linearGradient = new LinearGradient(
                0, viewHeight, viewWidth, viewHeight,
                colors, position, Shader.TileMode.CLAMP);
        foregroundPaint.setShader(linearGradient);

        canvas.drawRoundRect(fgRectF, radius, radius, foregroundPaint);

        //绘制文字
        Rect textRect = new Rect();
        this.text = (int) (100 * ratio) + "%";
        textPaint.getTextBounds(text, 0, text.length(), textRect);
        int textWidth = textRect.width();
        int textHeight = textRect.height();
        //计算文字左下角坐标
        float textX = (float) (viewWidth * ratio - textWidth * 1.25);
        float textY = centerY + (textHeight >> 1);
        canvas.drawText(text, textX, textY, textPaint);

        //刷新控件进度
        invalidate();
    }

    public float getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
    }

    /**
     * 设置当前的进度值
     */
    public void setCurrentProgress(float progress) {
        this.currentProgress = Math.min(progress, maxProgress);
        invalidate();
    }

    /**
     * 获取xml颜色值
     */
    private int getResourcesColor(Context context, int res) {
        return context.getResources().getColor(res);
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
