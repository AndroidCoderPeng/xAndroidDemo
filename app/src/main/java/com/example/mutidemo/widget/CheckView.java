package com.example.mutidemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mutidemo.R;

/**
 * @description: TODO 检查设备动画控件
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020年9月18日16:23:16
 */
public class CheckView extends View implements View.OnClickListener {
    private static final String TAG = "CheckView";

    private Context mContext;
    private Paint centerPaint; //中心圆paint
    private int centerColor;
    private int radius; //中心圆半径
    private float centerX;//圆心x
    private float centerY;//圆心y
    private TextPaint textPaint;
    private String text = "开始自检";
    private int textSize;
    private int textColor;
    private boolean isStart = false;

    public CheckView(Context context) {
        this(context, null, 0);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaterRippleView, defStyleAttr, 0);

        centerColor = a.getColor(R.styleable.WaterRippleView_ripple_centerColor
                , getResourcesColor(context, R.color.main_colcor_blue));
        radius = a.getDimensionPixelOffset(R.styleable.WaterRippleView_ripple_radius, dp2px(context, 50));
        textSize = a.getDimensionPixelOffset(R.styleable.WaterRippleView_ripple_textSize
                , sp2px(context, 16));
        text = a.getString(R.styleable.WaterRippleView_ripple_text);
        textColor = a.getColor(R.styleable.WaterRippleView_ripple_textColor
                , getResourcesColor(context, R.color.white));
        a.recycle();

        //初始化画笔
        initPaint();
        //点击事件
        setOnClickListener(this);
    }

    private void initPaint() {
        //中心圆画笔
        centerPaint = new Paint();
        centerPaint.setColor(centerColor);
        centerPaint.setAntiAlias(true);

        //扩散圆圈画笔

        //中心圆文字画笔
        textPaint = new TextPaint();
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //圆心位置
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
        int mWidth, mHeight;
        // 获取宽
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            mWidth = widthSpecSize;
        } else {
            // wrap_content
            mWidth = dp2px(mContext, 3 * radius);
        }
        // 获取高
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            mHeight = heightSpecSize;
        } else {
            // wrap_content
            mHeight = dp2px(mContext, 3 * radius);
        }
        // 设置该view的宽高
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isStart) {

        }
        //中间的圆
        canvas.drawCircle(centerX, centerY, radius, centerPaint);

        //绘制文字
        Rect textRect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textRect);
        int textWidth = textRect.width();
        int textHeight = textRect.height();
        //计算文字左下角坐标
        float textX = centerX - (textWidth >> 1);
        float textY = centerY + (4 * textHeight / 3);//让文字靠下点，所以Y坐标大一点
        canvas.drawText(text, textX, textY, textPaint);
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * dp转px
     */
    private int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转换成px
     */
    private int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取xml颜色值
     */
    private int getResourcesColor(Context context, int res) {
        return context.getResources().getColor(res);
    }
}
