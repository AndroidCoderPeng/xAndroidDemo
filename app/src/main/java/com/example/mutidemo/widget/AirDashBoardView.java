package com.example.mutidemo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mutidemo.R;
import com.pengxh.app.multilib.utils.SizeUtil;

import java.lang.ref.WeakReference;

/**
 * @description: 空气污染指数表盘，仿HUAWEI天气
 * @author: a203
 * @email: 290677893@qq.com
 * @date: 2021年10月08日12:04:03
 */
public class AirDashBoardView extends View {

    private static WeakReferenceHandler weakReferenceHandler;
    private final Context context;
    private final int currentValueTextSize;
    private final String topText;//表盘顶部文字
    private final int topTextSize;
    private final int topTextColor;
    private final int valueTextSize;
    private final int valueColor;//阈值颜色
    private final int centerTextSize;
    private final int background;//表盘圆弧背景色
    private final int ringWidth;
    private final int ringRadius;
    private int currentValue;//当前污染物测量值
    private int minValue;//污染物最小值
    private int maxValue;//污染物最大值
    private int centerX;//圆心x
    private int centerY;//圆心y
    private float sweepAngle;//当前测量值转为弧度扫过的角度
    private TextPaint valuePaint, currentValuePaint, topPaint, centerPaint;
    private Paint backPaint, forePaint;
    private String centerText;//表盘中心文字

    public AirDashBoardView(Context context) {
        this(context, null, 0);
    }

    public AirDashBoardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AirDashBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.AirDashBoardView, defStyleAttr, 0);
        /**
         * getDimension()返回的是float
         * getDimensionPixelSize()返回的是实际数值的四舍五入
         * getDimensionPixelOffset返回的是实际数值去掉后面的小数点
         * */
        valueTextSize = type.getDimensionPixelOffset(R.styleable.AirDashBoardView_air_valueSize, SizeUtil.dp2px(context, 12));
        valueColor = type.getColor(R.styleable.AirDashBoardView_air_valueColor, getResourcesColor(R.color.mainBackground));

        currentValueTextSize = type.getDimensionPixelOffset(R.styleable.AirDashBoardView_air_current_valueSize, SizeUtil.dp2px(context, 24));

        topText = type.getString(R.styleable.AirDashBoardView_air_top_text);
        topTextSize = type.getDimensionPixelOffset(R.styleable.AirDashBoardView_air_top_textSize, SizeUtil.dp2px(context, 16));
        topTextColor = type.getColor(R.styleable.AirDashBoardView_air_top_textColor, getResourcesColor(R.color.white));

        centerText = type.getString(R.styleable.AirDashBoardView_air_center_text);
        centerTextSize = type.getDimensionPixelOffset(R.styleable.AirDashBoardView_air_center_textSize, SizeUtil.dp2px(context, 12));

        background = type.getColor(R.styleable.AirDashBoardView_air_ring_background, getResourcesColor(R.color.mainBackground));
        ringWidth = type.getDimensionPixelOffset(R.styleable.AirDashBoardView_air_ring_width, SizeUtil.dp2px(context, 5));
        ringRadius = type.getDimensionPixelOffset(R.styleable.AirDashBoardView_air_ring_radius, SizeUtil.dp2px(context, 100));

        type.recycle();

        //初始化画笔
        initPaint();
        weakReferenceHandler = new WeakReferenceHandler(this);
    }

    private void initPaint() {
        valuePaint = new TextPaint();
        valuePaint.setColor(valueColor);
        valuePaint.setAntiAlias(true);
        valuePaint.setTextAlign(Paint.Align.CENTER);
        valuePaint.setTextSize(valueTextSize);

        currentValuePaint = new TextPaint();
        currentValuePaint.setAntiAlias(true);
        currentValuePaint.setTextAlign(Paint.Align.CENTER);
        currentValuePaint.setTextSize(currentValueTextSize);

        topPaint = new TextPaint();
        topPaint.setColor(topTextColor);
        topPaint.setAntiAlias(true);
        topPaint.setTextAlign(Paint.Align.CENTER);
        topPaint.setTextSize(topTextSize);

        centerPaint = new TextPaint();
        centerPaint.setAntiAlias(true);
        centerPaint.setTextAlign(Paint.Align.CENTER);
        centerPaint.setTextSize(centerTextSize);

        backPaint = new Paint();
        backPaint.setColor(background);
        backPaint.setStrokeCap(Paint.Cap.ROUND);
        backPaint.setStyle(Paint.Style.STROKE);
        backPaint.setStrokeWidth(SizeUtil.dp2px(context, ringWidth));
        backPaint.setAntiAlias(true);
        //设置背景光晕
        backPaint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.SOLID));

        forePaint = new Paint();
        forePaint.setStrokeCap(Paint.Cap.ROUND);
        forePaint.setStyle(Paint.Style.STROKE);
        forePaint.setStrokeWidth(SizeUtil.dp2px(context, ringWidth));
        forePaint.setAntiAlias(true);
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
            mWidth = SizeUtil.dp2px(context, (float) (ringRadius * 1.2));
        }
        // 获取高
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            mHeight = heightSpecSize;
        } else {
            // wrap_content，外边界高
            mHeight = SizeUtil.dp2px(context, (float) (ringRadius * 1.2));
        }
        // 设置该view的宽高
        setMeasuredDimension(mWidth, mHeight);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 画布移到中心位置
         * */
        canvas.translate(centerX, centerY);
        /**
         * 画矩形  以两个点来画，起点和终点，通常是左上为起点，右下为终点  以下面这个图来看
         * 参数一：起点的Y轴坐标
         * 参数二：起点的X轴坐标
         * 参数三：终点的Y轴坐标
         * 参数四：终点的Y轴坐标
         *      *
         *      *  top
         *  ****************
         *      *          *
         * left *          *  right
         *      *          *
         *      *          *
         *      ******************
         *         bottom  *
         *                 *
         */
        drawBackgroundArc(canvas);

        /**
         * 绘制顶部文字
         * */
        drawTopText(canvas);
        /**
         * 绘制左边最小值
         * */
        drawMinValue(canvas);

        /**
         * 绘制右边最大值
         * */
        drawMaxValue(canvas);

        /**
         * 绘制中间实际值
         * */
        drawCurrentValue(canvas);

        /**
         * 绘制中间文字
         * */
        drawCenterText(canvas);

        /**
         *
         * 绘制前景进度
         * */
        drawForegroundArc(canvas);
    }

    private void drawBackgroundArc(Canvas canvas) {
        /**
         * 从左往右画，顺时针，左边是180度
         * */
        RectF rectF = new RectF(-ringRadius, -ringRadius, ringRadius, ringRadius);
        canvas.drawArc(rectF, 135, 270, false, backPaint);
    }

    private void drawForegroundArc(Canvas canvas) {
        RectF rectF = new RectF(-ringRadius, -ringRadius, ringRadius, ringRadius);
        canvas.drawArc(rectF, 135, sweepAngle, false, forePaint);
        invalidate();
    }

    private void drawTopText(Canvas canvas) {
        Rect textRect = new Rect(
                -ringRadius,
                -(2 * ringRadius + SizeUtil.dp2px(context, (float) ringRadius / 5)),
                ringRadius,
                0);
        Paint.FontMetrics fontMetrics = topPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        int baseLineY = (int) (textRect.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式
        canvas.drawText(topText, textRect.centerX(), baseLineY, topPaint);
    }

    private void drawMinValue(Canvas canvas) {
        Rect textRect = new Rect(
                -(int) (ringRadius * 1.25),
                0,
                0,
                ringRadius + SizeUtil.dp2px(context, (float) ringRadius / 4));
        Paint.FontMetrics fontMetrics = valuePaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        int baseLineY = (int) (textRect.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式
        canvas.drawText(String.valueOf(minValue), textRect.centerX(), baseLineY, valuePaint);
    }

    private void drawMaxValue(Canvas canvas) {
        Rect textRect = new Rect(
                0,
                0,
                (int) (ringRadius * 1.25),
                ringRadius + SizeUtil.dp2px(context, (float) ringRadius / 4));
        Paint.FontMetrics fontMetrics = valuePaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        int baseLineY = (int) (textRect.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式
        canvas.drawText(String.valueOf(maxValue), textRect.centerX(), baseLineY, valuePaint);
    }

    private void drawCurrentValue(Canvas canvas) {
        Rect textRect = new Rect(0, 0, 0, 0);
        Paint.FontMetrics fontMetrics = currentValuePaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        int baseLineY = (int) (textRect.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式
        canvas.drawText(String.valueOf(currentValue), textRect.centerX(), baseLineY, currentValuePaint);
    }

    private void drawCenterText(Canvas canvas) {
        Rect textRect = new Rect(0, 0, 0, -SizeUtil.dp2px(context, (float) ringRadius / 7));
        Paint.FontMetrics fontMetrics = centerPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        int baseLineY = (int) (textRect.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式
        canvas.drawText(centerText, textRect.centerX(), baseLineY, centerPaint);
    }

    /**
     * 获取xml颜色值
     */
    private int getResourcesColor(int res) {
        return context.getResources().getColor(res, null);
    }

    /***********************************************************/
    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setCurrentValue(int value) {
        if (value < 0) {
            this.currentValue = 0;
        } else this.currentValue = Math.min(value, 500);

        new Thread(() -> {
            for (int i = 0; i < currentValue; i++) {
                Message message = weakReferenceHandler.obtainMessage();
                message.arg1 = i;
                message.what = 20211009;
                weakReferenceHandler.handleMessage(message);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static class WeakReferenceHandler extends Handler {
        private final WeakReference<AirDashBoardView> reference;

        private WeakReferenceHandler(AirDashBoardView boardView) {
            reference = new WeakReference<>(boardView);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            AirDashBoardView boardView = reference.get();
            if (msg.what == 20211009) {
                boardView.sweepAngle = (float) msg.arg1 * 270 / boardView.maxValue;
            }
        }
    }

    public void setCenterText(String centerText) {
        this.centerText = centerText;
    }

    public void setAirRingForeground(int color) {
        forePaint.setColor(color);
    }

    public void setAirCenterTextColor(int color) {
        centerPaint.setColor(color);
    }

    public void setAirCurrentValueColor(int color) {
        currentValuePaint.setColor(color);
    }
}
