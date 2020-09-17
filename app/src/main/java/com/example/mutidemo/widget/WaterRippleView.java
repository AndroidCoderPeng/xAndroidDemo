package com.example.mutidemo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mutidemo.R;

import java.util.ArrayList;
import java.util.List;


/**
 * @description: TODO 呼叫设备水波纹扩散动画控件
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020年9月17日14:24:45
 */
public class WaterRippleView extends View implements View.OnClickListener {

    private static final String TAG = "WaterRippleView";
    private Context mContext;
    private Paint centerPaint; //中心圆paint
    private int radius; //中心圆半径
    private Paint spreadPaint; //扩散圆paint
    private float centerX;//圆心x
    private float centerY;//圆心y
    private int distance; //每次圆递增间距，值越大，扩散速度越快
    private int maxDistance; //最大扩散距离，值越小，扩散效果越明显
    private int animDuration;//扩散延迟间隔，越大扩散越慢
    private List<Integer> spreadRadius = new ArrayList<>();//扩散圆层级数，元素为扩散的距离
    private List<Integer> alphas = new ArrayList<>();//对应每层圆的透明度
    private TextPaint textPaint;
    private String text = "正在呼叫";
    private int textSize;
    private int textColor;
    private int spreadColor;
    private int centerColor;
    private Paint imagePaint;
    private int imageResourceId;//图片资源
    private boolean isStart = false;

    public WaterRippleView(Context context) {
        this(context, null, 0);
    }

    public WaterRippleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterRippleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaterRippleView, defStyleAttr, 0);

        centerColor = a.getColor(R.styleable.WaterRippleView_ripple_centerColor
                , getResourcesColor(context, R.color.main_colcor_blue));
        spreadColor = a.getColor(R.styleable.WaterRippleView_ripple_spreadColor
                , getResourcesColor(context, R.color.main_colcor_blue));
        animDuration = a.getInteger(R.styleable.WaterRippleView_ripple_animDuration, animDuration);
        radius = a.getDimensionPixelOffset(R.styleable.WaterRippleView_ripple_radius, dp2px(context, 50));
        distance = a.getDimensionPixelOffset(R.styleable.WaterRippleView_ripple_distance
                , dp2px(context, 3));
        maxDistance = a.getDimensionPixelOffset(R.styleable.WaterRippleView_ripple_maxDistance
                , dp2px(context, 30));
        textSize = a.getDimensionPixelOffset(R.styleable.WaterRippleView_ripple_textSize
                , sp2px(context, 16));
        text = a.getString(R.styleable.WaterRippleView_ripple_text);
        textColor = a.getColor(R.styleable.WaterRippleView_ripple_textColor
                , getResourcesColor(context, R.color.white));
        imageResourceId = a.getResourceId(R.styleable.WaterRippleView_ripple_image, R.mipmap.hujiao);
        a.recycle();

        //最开始不透明且扩散距离为0
        alphas.add(255);
        spreadRadius.add(0);
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
        spreadPaint = new Paint();
        spreadPaint.setAntiAlias(true);
        spreadPaint.setAlpha(255);
        spreadPaint.setColor(spreadColor);

        //中心圆文字画笔
        textPaint = new TextPaint();
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);

        //中心圆上面图片画笔
        imagePaint = new Paint();
        imagePaint.setAntiAlias(true);
        imagePaint.setAlpha(255);
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
            mWidth = dp2px(mContext, 2 * (maxDistance + radius));
        }
        // 获取高
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            mHeight = heightSpecSize;
        } else {
            // wrap_content
            mHeight = dp2px(mContext, 2 * (maxDistance + radius));
        }
        // 设置该view的宽高
        setMeasuredDimension(mWidth, mHeight);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isStart) {
            for (int i = 0; i < spreadRadius.size(); i++) {
                int alpha = alphas.get(i);
                spreadPaint.setAlpha(alpha);
                int width = spreadRadius.get(i);
                //绘制扩散的圆
                canvas.drawCircle(centerX, centerY, radius + width, spreadPaint);
                //每次扩散圆半径递增，圆透明度递减
                if (alpha > 0 && width < dp2px(mContext, maxDistance + radius)) {
                    alpha = Math.max(alpha - distance, 0);
                    alphas.set(i, alpha);
                    spreadRadius.set(i, width + distance);
                }
            }
            //当最外层扩散圆半径达到最大半径时添加新扩散圆
            Integer maxRadius = spreadRadius.get(spreadRadius.size() - 1);
            if (maxRadius > maxDistance) {
                spreadRadius.add(0);
                alphas.add(255);
            }
            //超过5个扩散圆，删除最先绘制的圆，即最外层的圆
            if (spreadRadius.size() >= 5) {
                alphas.remove(0);
                spreadRadius.remove(0);
            }
            postInvalidateDelayed(animDuration);
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

        //绘制图片
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResourceId);//实例化
        /**
         * 方法一：此方法局限性大，对于图片要求较高，必须是尺寸合适的图片，否则会显示异常
         * */
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        //计算图片左上角坐标
        float imageX = centerX - (imageWidth >> 1);
        float imageY = centerY - 4 * imageHeight / 3;//让图片靠上点，所以Y坐标小一点
        canvas.drawBitmap(bitmap, imageX, imageY, imagePaint);
        /**
         * 方法二：此方法是为将要绘制的图片划定显示区域，可以缓解图片尺寸导致显示异常问题
         * */
//        RectF rectF = new RectF(centerX - (textWidth >> 2), centerY - (textHeight * 2),
//                centerX + (textWidth >> 2), centerY - (textHeight >> 1));
//        Log.d(TAG, "height: " + rectF.height() + " ==== width: " + rectF.width());
//        canvas.drawBitmap(bitmap, null, rectF, imagePaint);
        /**
         * 方法三名：采用矩阵方式实现，这可彻底解决图片尺寸导致显示异常的问题
         *
         * Matrix matrix=new Matrix();
         * */
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

    /**
     * 启动动画
     */
    public void start() {
        Log.d(TAG, "start: 启动动画");
        this.isStart = true;
    }

    /**
     * 停止动画
     */
    public void stop() {
        Log.d(TAG, "stop: 停止动画");
        this.isStart = false;
        postInvalidate();
    }

    @Override
    public void onClick(View v) {
        if (isStart) {
            Log.d(TAG, "onClick: 重复启动动画");
            return;
        }
        if (startListener != null) {
            startListener.onStart(this);
        }
        //标志位改变之后手动刷新onDraw
        postInvalidate();
    }

    private OnAnimationStartListener startListener;

    public interface OnAnimationStartListener {
        void onStart(WaterRippleView view);
    }

    public void setOnAnimationStartListener(OnAnimationStartListener listener) {
        this.startListener = listener;
    }
}
