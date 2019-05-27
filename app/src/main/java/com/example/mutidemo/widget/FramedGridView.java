package com.example.mutidemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.GridView;

import com.example.mutidemo.R;


/**
 * item带有边框的GridView
 * Framed：有边框的
 * <p>
 * 2019年5月27日10:11:24
 * <p>
 * 布局不能加Padding，否则会影响计算子item的大小
 */
public class FramedGridView extends GridView {

    private static final String TAG = "FramedGridView";

    private Paint mLinePaint;

    public FramedGridView(Context context) {
        super(context);
    }

    public FramedGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        /**
         * 获取到attrs属性
         * */
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FramedGridView);
        int mBorderColor = typedArray.getColor(R.styleable.FramedGridView_border_color, Color.LTGRAY);
        int mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.FramedGridView_border_width,
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,//1(第二个参数)SP(第一个参数)在当前设备上对应的px值。
                        1,
                        getResources().getDisplayMetrics()));
        typedArray.recycle();
        if (mBorderColor == 0) {
            initPaint(Color.LTGRAY, mBorderWidth);
        } else {
            initPaint(mBorderColor, mBorderWidth);
        }
    }

    /**
     * 初始化画笔
     *
     * @param mBorderColor
     * @param mBorderWidth
     */
    private void initPaint(int mBorderColor, float mBorderWidth) {
        Log.d(TAG, "mBorderColor: " + mBorderColor);
        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(mBorderWidth);
        mLinePaint.setColor(mBorderColor);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        View child = getChildAt(0);
        //计算九宫格每行有几个item，俗称“列”
        int column = getWidth() / child.getWidth();
        int childCount = getChildCount();//获取整个九宫格所有子布局item的个数

        //轮流画每个item的边框
        for (int i = 0; i < childCount; i++) {
            View cellView = getChildAt(i);
            if ((i + 1) % column == 0) {
                canvas.drawLine(
                        cellView.getLeft(),
                        cellView.getBottom(),
                        cellView.getRight(),
                        cellView.getBottom(),
                        mLinePaint);
            } else if ((i + 1) > (childCount - (childCount % column))) {
                canvas.drawLine(
                        cellView.getRight(),
                        cellView.getTop(),
                        cellView.getRight(),
                        cellView.getBottom(),
                        mLinePaint);
            } else {
                canvas.drawLine(
                        cellView.getRight(),
                        cellView.getTop(),
                        cellView.getRight(),
                        cellView.getBottom(),
                        mLinePaint);
                canvas.drawLine(
                        cellView.getLeft(),
                        cellView.getBottom(),
                        cellView.getRight(),
                        cellView.getBottom(),
                        mLinePaint);
            }
        }
        if (childCount % column != 0) {
            for (int j = 0; j < (column - childCount % column); j++) {
                View lastView = getChildAt(childCount - 1);
                canvas.drawLine(
                        lastView.getRight() + lastView.getWidth() * j,
                        lastView.getTop(),
                        lastView.getRight() + lastView.getWidth() * j,
                        lastView.getBottom(),
                        mLinePaint);
            }
        }
    }
}