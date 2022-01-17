package com.example.mutidemo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mutidemo.R;

import java.util.Random;

/**
 * @description: TODO 雷达扫描动画控件
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/9/19 23:05
 */
public class RadarView extends View {

    private final Context mContext;
    private final double speed = 3;//线扫描的速度
    private final int lineColor;
    private final int radius; //中心圆半径
    private final int ringNumber;
    private final int maxRingRadius;//最外环的半径
    private float centerX;//圆心x
    private float centerY;//圆心y
    private double degree;//线每次扫描的角度
    private Paint linePaint; //圆环paint
    private Matrix matrix;
    private Paint sectorPaint;

    public RadarView(Context context) {
        this(context, null, 0);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RadarView, defStyleAttr, 0);

        lineColor = a.getColor(R.styleable.RadarView_radar_lineColor, Color.GREEN);
        radius = a.getDimensionPixelOffset(R.styleable.RadarView_radar_radius, dp2px(context, 70));
        int distance = a.getDimensionPixelOffset(R.styleable.RadarView_radar_distance, dp2px(context, 10));
        a.recycle();
        //计算圆环数量和最外层圆环半径
        ringNumber = (radius / distance);
        maxRingRadius = ringNumber * dp2px(mContext, 15);
        //初始化画笔
        initPaint();
        //扫描线动画线程
        new ScanThread(this).start();
    }

    private void initPaint() {
        //圆圈画笔
        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(dp2px(mContext, 1));
        linePaint.setAntiAlias(true);

        //扫描线
        sectorPaint = new Paint();
        sectorPaint.setAntiAlias(true);
        sectorPaint.setStyle(Paint.Style.FILL);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画雷达圆环
        for (int i = 1; i <= ringNumber; i++) {
            canvas.drawCircle(centerX, centerY, i * dp2px(mContext, 15), linePaint);
        }

        //画十字线
        canvas.drawLine(
                centerX - maxRingRadius, centerY,
                centerX + maxRingRadius, centerY,
                linePaint);
        canvas.drawLine(
                centerX, centerY - ringNumber * dp2px(mContext, 15),
                centerX, centerY + ringNumber * dp2px(mContext, 15),
                linePaint);

        //扫描线动画
        Shader sectorShader = new SweepGradient(centerX, centerY,
                new int[]{Color.TRANSPARENT, Color.GREEN},
                new float[]{0, 1.0f});
        sectorPaint.setShader(sectorShader);
        canvas.concat(matrix);
        canvas.drawCircle(centerX, centerY, maxRingRadius, sectorPaint);
    }

    class ScanThread extends Thread {
        private RadarView view;

        ScanThread(RadarView view) {
            this.view = view;
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    view.post(() -> {
                        degree = speed + degree;
                        if (degree >= 360) {
                            degree = degree - 360;
                        }
                        //扫描线矩阵
                        matrix = new Matrix();
                        matrix.setRotate((float) degree, centerX, centerY);
                        view.invalidate();
                    });
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //噪点X坐标，X在最大圆的X坐标间取[centerX - radius,centerX + radius]
    //int randNumber = rand.nextInt(MAX - MIN + 1) + MIN;
    private int getPointX() {
        Random rand = new Random();
        int x1 = (int) (centerX - radius);
        int x2 = (int) (centerX + radius);
        return rand.nextInt(x2 - x1 + 1) + x1;
    }

    private int getPointY() {
        Random rand = new Random();
        int y1 = (int) (centerY - radius);
        int y2 = (int) (centerY + radius);
        return rand.nextInt(y2 - y1 + 1) + y1;
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
        int mWidth, mHeight;//控件外轮廓
        // 获取宽
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            mWidth = widthSpecSize;
        } else {
            // wrap_content
            mWidth = dp2px(mContext, 3 * radius >> 1);
        }
        // 获取高
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            mHeight = heightSpecSize;
        } else {
            // wrap_content
            mHeight = dp2px(mContext, 3 * radius >> 1);
        }
        // 设置该view的宽高
        setMeasuredDimension(mWidth, mHeight);
    }

    /**
     * dp转px
     */
    private int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }
}
