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

import com.pengxh.androidx.lite.utils.DeviceSizeUtil;

import org.jetbrains.annotations.NotNull;

public class FaceCollectionView extends View {

    private final Context context;
    private final TextPaint textPaint;
    private final Paint dashedPaint;
    private final Paint facePaint;
    //    private final Paint bitMapPaint;
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
        textPaint.setTextSize(DeviceSizeUtil.sp2px(context, 16));

        dashedPaint = new Paint();
        dashedPaint.setColor(lineColor);
        dashedPaint.setStyle(Paint.Style.STROKE);
        dashedPaint.setStrokeWidth(DeviceSizeUtil.dp2px(context, 8));
        dashedPaint.setPathEffect(new DashPathEffect(new float[]{8, 16}, 0));
        dashedPaint.setAntiAlias(true);

        facePaint = new Paint();
        facePaint.setColor(Color.rgb(211, 211, 211));
        facePaint.setStyle(Paint.Style.FILL);
        //扣掉前景圆，给背景Surface显示
        facePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        facePaint.setAntiAlias(true);

//        bitMapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        bitMapPaint.setFilterBitmap(true);
//        bitMapPaint.setDither(true);

//        weakReferenceHandler = new WeakReferenceHandler(this);
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
            mWidth = DeviceSizeUtil.obtainScreenWidth(context);
        }
        // 获取高
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            mHeight = heightSpecSize;
        } else {
            // wrap_content，外边界高
            mHeight = DeviceSizeUtil.obtainScreenHeight(context);
        }
        // 设置该view的宽高
        setMeasuredDimension(mWidth, mHeight);
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int radius = DeviceSizeUtil.obtainScreenWidth(context) / 2;
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
        canvas.drawCircle(centerX, (float) (centerY * 0.6), (float) (radius * 0.8), dashedPaint);

        /**
         * 抠出中心圆
         * */
        canvas.drawCircle(centerX, (float) (centerY * 0.6), (float) (radius * 0.7), facePaint);

//        drawBitmap(canvas);
    }

//    private Bitmap bitmap;
//    private static WeakReferenceHandler weakReferenceHandler;
//
//    private void drawBitmap(@NotNull Canvas canvas) {
//        int height = SizeUtil.getScreenHeight(context);
//        if (bitmap != null) {
//            canvas.drawBitmap(bitmap, 0, height >> 1, bitMapPaint);
//            invalidate();
//        }
//    }
//
//    public void setBitmap(Bitmap bitmap) {
//        new Thread(() -> {
//            Message message = weakReferenceHandler.obtainMessage();
//            message.obj = bitmap;
//            message.what = 202120118;
//            weakReferenceHandler.handleMessage(message);
//        }).start();
//    }
//
//    private static class WeakReferenceHandler extends Handler {
//        private final WeakReference<FaceCollectionView> reference;
//
//        private WeakReferenceHandler(FaceCollectionView faceCollectionView) {
//            reference = new WeakReference<>(faceCollectionView);
//        }
//
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
//            FaceCollectionView view = reference.get();
//            if (msg.what == 202120118) {
//                view.bitmap = (Bitmap) msg.obj;
//            }
//        }
//    }

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
