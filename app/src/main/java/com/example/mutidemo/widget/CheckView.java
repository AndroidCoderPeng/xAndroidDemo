package com.example.mutidemo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mutidemo.R;
import com.pengxh.app.multilib.utils.ColorUtil;
import com.pengxh.app.multilib.utils.SizeUtil;

/**
 * @description: TODO 检查设备动画控件
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020年9月18日16:23:16
 */
public class CheckView extends View implements View.OnClickListener {
    private static final String TAG = "CheckView";

    private final Context mContext;

    private final int centerColor;
    private final int ringColor;
    private final int radius; //中心圆半径
    private final int textSize;
    private final int textColor;
    private float centerX;//圆心x
    private float centerY;//圆心y
    private Paint centerPaint; //中心圆paint
    private TextPaint textPaint;
    private String text = "开始自检";
    private Paint ringPaint; //圆环paint
    private boolean isStart = false;
    private double degree;//小球每次运行的角度
    private int x1, y1, x2, y2;//两个小球的坐标

    public CheckView(Context context) {
        this(context, null, 0);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CheckView, defStyleAttr, 0);

        centerColor = a.getColor(R.styleable.CheckView_view_centerColor, ColorUtil.getResourcesColor(context, R.color.mainColor));
        ringColor = a.getColor(R.styleable.CheckView_view_ringColor, ColorUtil.getResourcesColor(context, R.color.mainColor));
        radius = a.getDimensionPixelOffset(R.styleable.CheckView_view_radius, SizeUtil.dp2px(context, 50));
        textSize = a.getDimensionPixelOffset(R.styleable.CheckView_view_textSize, SizeUtil.sp2px(context, 18));
        text = a.getString(R.styleable.CheckView_view_text);
        textColor = a.getColor(R.styleable.CheckView_view_textColor, ColorUtil.getResourcesColor(context, R.color.white));
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

        //圆圈画笔
        ringPaint = new Paint();
        ringPaint.setColor(ringColor);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setPathEffect(new DashPathEffect(new float[]{3, 3}, 0));//虚线
        ringPaint.setAlpha(90);
        ringPaint.setStrokeWidth(SizeUtil.dp2px(mContext, 5));
        ringPaint.setAntiAlias(true);

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
            mWidth = SizeUtil.dp2px(mContext, 3 * radius >> 1);
        }
        // 获取高
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            mHeight = heightSpecSize;
        } else {
            // wrap_content
            mHeight = SizeUtil.dp2px(mContext, 3 * radius >> 1);
        }
        // 设置该view的宽高
        setMeasuredDimension(mWidth, mHeight);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //中间的圆
        canvas.drawCircle(centerX, centerY, radius, centerPaint);

        //外层圆环
        canvas.drawCircle(centerX, centerY, radius + SizeUtil.dp2px(mContext, 10), ringPaint);
        canvas.drawCircle(centerX, centerY, radius + SizeUtil.dp2px(mContext, 20), ringPaint);

        if (isStart) {
            handler.sendEmptyMessage(1);
            //圆环上面的小圆
            int ringRadius = SizeUtil.dp2px(mContext, 5);
            canvas.drawCircle(x1, y1, ringRadius, centerPaint);
            canvas.drawCircle(x2, y2, ringRadius, centerPaint);
            invalidate();
        }

        //绘制文字
        Rect textRect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textRect);
        int textWidth = textRect.width();
        int textHeight = textRect.height();
        //计算文字左下角坐标
        float textX = centerX - (textWidth >> 1);
        float textY = centerY + (textHeight >> 1);
        canvas.drawText(text, textX, textY, textPaint);
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //小球每次运行的速度
                double speed = 0.04;
                degree = speed + degree;
                if (degree >= 360) {
                    degree = degree - 360;
                }
                //顺时针运动
                x1 = (int) (centerX + (radius + SizeUtil.dp2px(mContext, 10)) * Math.sin(degree));
                y1 = (int) (centerY - (radius + SizeUtil.dp2px(mContext, 10)) * Math.cos(degree));

                //逆时针运动
                x2 = (int) (centerX + (radius + SizeUtil.dp2px(mContext, 20)) * Math.sin(-degree));
                y2 = (int) (centerY - (radius + SizeUtil.dp2px(mContext, 20)) * Math.cos(-degree));
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (isStart) {
            Log.d(TAG, "onClick: 重复启动动画");
            return;
        }
        if (startListener != null) {
            startListener.onStart(this);
        }
    }

    /**
     * 启动动画
     */
    public void start() {
        Log.d(TAG, "start: 启动动画");
        text = "正在检测";
        this.isStart = true;
        postInvalidate();
    }

    /**
     * 停止动画
     */
    public void stop() {
        Log.d(TAG, "stop: 停止动画");
        text = "检测完毕";
        this.isStart = false;
        postInvalidate();
    }

    private OnAnimationStartListener startListener;

    public interface OnAnimationStartListener {
        void onStart(CheckView view);
    }

    public void setOnAnimationStartListener(OnAnimationStartListener listener) {
        this.startListener = listener;
    }
}
