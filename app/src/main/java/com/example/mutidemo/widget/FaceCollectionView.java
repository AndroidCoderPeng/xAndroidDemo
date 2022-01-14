package com.example.mutidemo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.pengxh.app.multilib.utils.SizeUtil;

import org.jetbrains.annotations.NotNull;

public class FaceCollectionView extends View {

    private static final String TAG = "FaceCollectionView";
    private Context context;
    private TextPaint textPaint;
    private Paint dashedPaint, facePaint;

    private int centerX;//圆心x
    private int centerY;//圆心y

    private String tips = "请将脸移至圆框内";
    private int lineColor = Color.rgb(211, 211, 211);

    public FaceCollectionView(Context context) {
        this(context, null, 0);
    }

    public FaceCollectionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceCollectionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        textPaint = new TextPaint();
        textPaint.setColor(Color.rgb(102, 102, 102));
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(SizeUtil.sp2px(context, 16));

        dashedPaint = new Paint();
        dashedPaint.setColor(lineColor);
        dashedPaint.setStyle(Paint.Style.STROKE);
        dashedPaint.setStrokeWidth(SizeUtil.dp2px(context, 8));
        dashedPaint.setPathEffect(new DashPathEffect(new float[]{8, 16}, 0));
        dashedPaint.setAntiAlias(true);

        facePaint = new Paint();
        facePaint.setColor(Color.rgb(211, 211, 211));
        facePaint.setStyle(Paint.Style.FILL);
        //扣掉前景圆，给背景Surface显示
        facePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        facePaint.setAntiAlias(true);
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
            // wrap_content，外边界宽
            mWidth = SizeUtil.getScreenWidth(context);
        }
        // 获取高
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            mHeight = heightSpecSize;
        } else {
            // wrap_content，外边界高
            mHeight = SizeUtil.getScreenHeight(context);
        }
        // 设置该view的宽高
        setMeasuredDimension(mWidth, mHeight);
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        /**
         * 设置View背景为白色
         * */
        canvas.drawColor(Color.WHITE);

        /**
         * 画顶部文字
         * */
        drawTopText(canvas);

        /**
         * 画虚线圆环
         * */
        canvas.drawCircle(centerX, (float) (centerY * 0.6), SizeUtil.dp2px(context, 135), dashedPaint);

        /**
         * 抠出中心圆
         * */
        canvas.drawCircle(centerX, (float) (centerY * 0.6), SizeUtil.dp2px(context, 120), facePaint);
    }

    private void drawTopText(@NotNull Canvas canvas) {
        Rect textRect = new Rect(0, (int) (centerY * 0.25), centerX * 2, 0);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        int baseLineY = (int) (textRect.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式
        canvas.drawText(tips, textRect.centerX(), baseLineY, textPaint);
        invalidate();
    }

    /**
     * 这只顶部提示语
     */
    public void setTips(String tips) {
        this.tips = tips;
    }
}
