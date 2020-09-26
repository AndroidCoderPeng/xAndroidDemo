package com.example.mutidemo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mutidemo.R;

public class SlideBarView extends View implements View.OnTouchListener {

    private static final String TAG = "SlideBarView";
    private static final String[] LETTER = {"A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    private static final int viewWidth = 20;
    private Context mContext;
    private float centerX;//中心x
    private int textSize;
    private int textColor;
    private int circleColor;
    private TextPaint textPaint;//文字画笔
    private int mHeight;//控件的实际尺寸
    private int touchIndex = -1;
    private int letterHeight;
    private Paint paint;

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
        circleColor = a.getColor(R.styleable.SlideBarView_slide_circleColor, Color.parseColor("#43DB87"));
        a.recycle();

        //初始化画笔
        initPaint();
        //触摸事件
        setOnTouchListener(this);
    }

    private void initPaint() {
        //文字画笔
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);

        //被按到的字母底部背景
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(circleColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w >> 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        // 获取宽
        int mWidth = dp2px(mContext, viewWidth);
        // 获取高
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            mHeight = heightSpecSize;
        }
        // 设置该view的宽高
        setMeasuredDimension(mWidth, mHeight);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        letterHeight = mHeight / LETTER.length;
        for (int i = 0; i < LETTER.length; i++) {
            int y = (i + 1) * letterHeight;//每个字母的占位高度(不是字体高度)

            //字母变色
            if (touchIndex == i) {
                //让当前字母变色
                textPaint.setColor(Color.WHITE);
                canvas.drawCircle(centerX, (float) ((i + 0.4) * letterHeight), dp2px(mContext, 10.0f), paint);
            } else {
                //其他字母不变色
                textPaint.setColor(textColor);
            }

            //绘制文字
            String letter = LETTER[i];
            Rect textRect = new Rect();
            textPaint.getTextBounds(letter, 0, letter.length(), textRect);
            int textWidth = textRect.width();
            int textHeight = textRect.height();
            //计算文字左下角坐标
            float textX = centerX - (textWidth >> 1);
            float textY = y - (textHeight >> 1);
            canvas.drawText(letter, textX, textY, textPaint);
        }
    }

    //侧边栏滑动事件
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float y = Math.abs(event.getY());//取绝对值，不然y可能会取到负值
                int index = (int) (y / letterHeight);//字母的索引
                if (index != touchIndex) {
                    touchIndex = Math.min(index, LETTER.length - 1);
                    //点击设置中间字母
                    if (onIndexChangeListener != null) {
                        onIndexChangeListener.OnIndexChange(LETTER[touchIndex]);
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                touchIndex = -1;
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    private OnIndexChangeListener onIndexChangeListener;

    public void setOnIndexChangeListener(OnIndexChangeListener listener) {
        onIndexChangeListener = listener;
    }

    public interface OnIndexChangeListener {
        void OnIndexChange(String letter);
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
