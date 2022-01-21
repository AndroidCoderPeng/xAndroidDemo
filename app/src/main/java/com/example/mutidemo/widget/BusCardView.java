package com.example.mutidemo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.mutidemo.R;
import com.pengxh.app.multilib.utils.ColorUtil;
import com.pengxh.app.multilib.utils.SizeUtil;

public class BusCardView extends AppCompatImageView {

    private static final String TAG = "BusCardView";
    private final int innerPadding = 20;
    private final Context context;
    private final int backgroundColor;
    private final int radius, padding;
    private TextPaint textPaint;
    private Paint backPaint;
    private int centerX;//View中心x
    private int centerY;//View中心y
    private int viewWidth, viewHeight, textWidth;
    private String tagText;

    public BusCardView(Context context) {
        this(context, null, 0);
    }

    public BusCardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BusCardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BusCardView, defStyleAttr, 0);
        backgroundColor = a.getColor(R.styleable.BusCardView_card_tag_background
                , ColorUtil.getResourcesColor(context, R.color.lightGray));
        padding = a.getDimensionPixelOffset(R.styleable.BusCardView_card_tag_padding, SizeUtil.dp2px(context, 10));
        radius = a.getDimensionPixelOffset(R.styleable.BusCardView_card_radius, SizeUtil.dp2px(context, 5));
        a.recycle();
        //初始化画笔
        initPaint();
    }

    private void initPaint() {
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(SizeUtil.sp2px(context, 12));

        backPaint = new Paint();
        backPaint.setColor(backgroundColor);
        backPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w >> 1;
        centerY = h >> 1;
    }

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
            // wrap_content，外边界宽
            viewWidth = SizeUtil.dp2px(context, 300);
        }
        // 获取高
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            viewHeight = heightSpecSize;
        } else {
            // wrap_content，外边界高
            viewHeight = SizeUtil.dp2px(context, 180);
        }
        // 设置该view的宽高
        setMeasuredDimension(viewWidth, viewHeight);
    }

    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        /**
         * 裁剪画布为圆角
         * */
        clipRoundPath(canvas);
        super.onDraw(canvas);
        /**
         * 画布移到中心位置
         * */
        canvas.translate(centerX, centerY);
        /**
         * 画右下角Text
         * */
        drawRightBottomText(canvas);
        /**
         * 画右下角Tag
         * */
        drawRightBottomTag(canvas);
    }

    private void clipRoundPath(Canvas canvas) {
        Path roundPath = new Path();
        //左上
        roundPath.moveTo(0, radius);
        roundPath.quadTo(0, 0, radius, 0);
        //右上
        roundPath.lineTo(viewWidth - radius, 0);
        roundPath.quadTo(viewWidth, 0, viewWidth, radius);
        //右下
        roundPath.lineTo(viewWidth, viewHeight - radius);
        roundPath.quadTo(viewWidth, viewHeight, viewWidth - radius, viewHeight);
        //
        roundPath.lineTo(radius, viewHeight);
        roundPath.quadTo(0, viewHeight, 0, viewHeight - radius);
        //闭合路径
        roundPath.close();
        canvas.clipPath(roundPath);
    }

    public void setTagText(String value) {
        this.tagText = value;
        textWidth = (int) textPaint.measureText(tagText);
        Log.d(TAG, "textWidth ===> " + textWidth);
    }

    private void drawRightBottomText(Canvas canvas) {
        Rect textRect = new Rect(-centerX + padding, centerY - (padding + 60), -centerX + (padding + textWidth + innerPadding), centerY - padding);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离
        int baseLineY = (int) (textRect.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式
        canvas.drawText(tagText, textRect.centerX(), baseLineY, textPaint);
    }

    private void drawRightBottomTag(Canvas canvas) {
        /**
         * Tag高度60px
         * */
        RectF roundRectF = new RectF(-centerX + padding, centerY - (padding + 60), -centerX + (padding + textWidth + innerPadding), centerY - padding);
        canvas.drawRoundRect(roundRectF, radius >> 1, radius >> 1, backPaint);
    }
}
