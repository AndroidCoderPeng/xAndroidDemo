package com.example.mutidemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mutidemo.R;
import com.pengxh.androidx.lite.utils.DeviceSizeUtil;

public class SteeringWheelController extends View implements View.OnTouchListener {

    private static final String TAG = "SteeringWheelController";
    private float canvasCenterX;//画布中心x
    private float canvasCenterY;//画布中心y
    private final int borderColor;
    //外圆直径
    private final int outerCircleDiameter;
    //线条粗细
    private final int borderStroke;
    //控制板背景Paint
    private final Paint backgroundPaint;
    //外圆Paint
    private final Paint outerCirclePaint;
    //内圆Paint
    private final Paint innerCirclePaint;
    //中间开关Paint
    private final Paint centerSwitchPaint;

    //箭头Paint
    private Paint leftDirectionPaint;
    private Paint topDirectionPaint;
    private Paint rightDirectionPaint;
    private Paint bottomDirectionPaint;

    //箭头Path
    private Path leftDirectionPath;
    private Path topDirectionPath;
    private Path rightDirectionPath;
    private Path bottomDirectionPath;

    // 中间图标画图区域
    private RectF centerSwitchOval;

    //外圆区域
    private RectF outerCircleRectF;

    // 各控件使用状态
    private boolean leftTurn;
    private boolean topTurn;
    private boolean rightTurn;
    private boolean bottomTurn;
    private boolean centerTurn;

    public SteeringWheelController(Context context) {
        this(context, null, 0);
    }

    public SteeringWheelController(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SteeringWheelController(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.SteeringWheelController, defStyleAttr, 0);
        int viewBackground = type.getColor(R.styleable.SteeringWheelController_casic_backgroundColor, Color.BLACK);
        borderColor = type.getColor(R.styleable.SteeringWheelController_casic_borderColor, Color.RED);
        int temp = type.getInt(R.styleable.SteeringWheelController_casic_outerCircleDiameter, 120);
        outerCircleDiameter = DeviceSizeUtil.dp2px(context, temp);
        borderStroke = type.getInt(R.styleable.SteeringWheelController_casic_borderStroke, 6);
        type.recycle();

        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setDither(true);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(viewBackground);

        outerCirclePaint = new Paint();
        outerCirclePaint.setAntiAlias(true);
        outerCirclePaint.setDither(true);
        outerCirclePaint.setStyle(Paint.Style.STROKE);
        outerCirclePaint.setStrokeWidth(borderStroke);
        outerCirclePaint.setColor(borderColor);

        innerCirclePaint = new Paint();
        innerCirclePaint.setAntiAlias(true);
        innerCirclePaint.setDither(true);
        innerCirclePaint.setStyle(Paint.Style.STROKE);
        innerCirclePaint.setStrokeWidth(borderStroke);
        innerCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        innerCirclePaint.setColor(borderColor);

        centerSwitchPaint = new Paint();
        centerSwitchPaint.setAntiAlias(true);
        centerSwitchPaint.setDither(true);
        centerSwitchPaint.setStyle(Paint.Style.STROKE);
        centerSwitchPaint.setStrokeWidth(borderStroke);
        centerSwitchPaint.setStrokeCap(Paint.Cap.ROUND);
        centerSwitchPaint.setColor(borderColor);

        initDirection();

        //设置控件可触摸
        setOnTouchListener(this);
    }

    //箭头
    private void initDirection() {
        leftDirectionPaint = new Paint();
        leftDirectionPaint.setAntiAlias(true);
        leftDirectionPaint.setDither(true);
        leftDirectionPaint.setStyle(Paint.Style.STROKE);
        leftDirectionPaint.setStrokeWidth(borderStroke);
        leftDirectionPaint.setStrokeCap(Paint.Cap.ROUND);
        leftDirectionPaint.setColor(borderColor);
        //路径
        leftDirectionPath = new Path();

        topDirectionPaint = new Paint();
        topDirectionPaint.setAntiAlias(true);
        topDirectionPaint.setDither(true);
        topDirectionPaint.setStyle(Paint.Style.STROKE);
        topDirectionPaint.setStrokeWidth(borderStroke);
        topDirectionPaint.setStrokeCap(Paint.Cap.ROUND);
        topDirectionPaint.setColor(borderColor);
        //路径
        topDirectionPath = new Path();

        rightDirectionPaint = new Paint();
        rightDirectionPaint.setAntiAlias(true);
        rightDirectionPaint.setDither(true);
        rightDirectionPaint.setStyle(Paint.Style.STROKE);
        rightDirectionPaint.setStrokeWidth(borderStroke);
        rightDirectionPaint.setStrokeCap(Paint.Cap.ROUND);
        rightDirectionPaint.setColor(borderColor);
        //路径
        rightDirectionPath = new Path();

        bottomDirectionPaint = new Paint();
        bottomDirectionPaint.setAntiAlias(true);
        bottomDirectionPaint.setDither(true);
        bottomDirectionPaint.setStyle(Paint.Style.STROKE);
        bottomDirectionPaint.setStrokeWidth(borderStroke);
        bottomDirectionPaint.setStrokeCap(Paint.Cap.ROUND);
        bottomDirectionPaint.setColor(borderColor);
        //路径
        bottomDirectionPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasCenterX = w >> 1;
        canvasCenterY = h >> 1;

        centerSwitchOval = new RectF(canvasCenterX - (float) outerCircleDiameter / 15, canvasCenterY - (float) outerCircleDiameter / 15 + 2, canvasCenterX + (float) outerCircleDiameter / 15, canvasCenterY + (float) outerCircleDiameter / 15 + 2);

        int outerCircleRadius = outerCircleDiameter >> 1;//半径
        // 大外圈区域
        outerCircleRectF = new RectF(canvasCenterX - outerCircleRadius - borderStroke, canvasCenterY - outerCircleRadius - borderStroke, canvasCenterX + outerCircleRadius + borderStroke, canvasCenterY + outerCircleRadius + borderStroke);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int minimumWidth = getSuggestedMinimumWidth();
        int minimumHeight = getSuggestedMinimumHeight();
        int width = measureWidth(minimumWidth, widthMeasureSpec);
        int height = measureHeight(minimumHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int defaultWidth, int measureSpec) {
        int width = defaultWidth;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                width = (outerCircleDiameter + borderStroke * 2 + getPaddingLeft() + getPaddingRight());
                break;
            case MeasureSpec.EXACTLY:
                width = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                width = Math.max(defaultWidth, specSize);
                break;
            default:
                break;
        }
        return width;
    }

    private int measureHeight(int defaultHeight, int measureSpec) {
        int height = defaultHeight;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                height = (outerCircleDiameter + borderStroke * 2 + getPaddingTop() + getPaddingBottom());
                break;
            case MeasureSpec.EXACTLY:
                height = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                height = Math.max(defaultHeight, specSize);
                break;
            default:
                break;
        }
        return height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //背景
        int outerCircleRadius = outerCircleDiameter >> 1;//半径
        canvas.drawCircle(canvasCenterX, canvasCenterY, outerCircleRadius + borderStroke, backgroundPaint);

        //外圆圆圈，+1是为了完全覆盖背景色的边缘
        canvas.drawCircle(canvasCenterX, canvasCenterY, outerCircleRadius + (borderStroke >> 1) + 1, outerCirclePaint);

        // 箭头长度
        int directionLength = 25;

        //左箭头
        float leftX = canvasCenterX - outerCircleRadius + directionLength;
        leftDirectionPath.moveTo(leftX, canvasCenterY);
        leftDirectionPath.lineTo(leftX + directionLength, canvasCenterY - directionLength);
        leftDirectionPath.moveTo(leftX, canvasCenterY);
        leftDirectionPath.lineTo(leftX + directionLength, canvasCenterY + directionLength);
        canvas.drawPath(leftDirectionPath, leftDirectionPaint);

        // 上箭头
        float topY = canvasCenterY - outerCircleRadius + directionLength;
        topDirectionPath.moveTo(canvasCenterX, topY);
        topDirectionPath.lineTo(canvasCenterX - directionLength, topY + directionLength);
        topDirectionPath.moveTo(canvasCenterX, topY);
        topDirectionPath.lineTo(canvasCenterX + directionLength, topY + directionLength);
        canvas.drawPath(topDirectionPath, topDirectionPaint);

        // 右箭头
        float rightX = canvasCenterX + outerCircleRadius - directionLength;
        rightDirectionPath.moveTo(rightX, canvasCenterY);
        rightDirectionPath.lineTo(rightX - directionLength, canvasCenterY - directionLength);
        rightDirectionPath.moveTo(rightX, canvasCenterY);
        rightDirectionPath.lineTo(rightX - directionLength, canvasCenterY + directionLength);
        canvas.drawPath(rightDirectionPath, rightDirectionPaint);

        // 下箭头
        float bottomY = canvasCenterY + outerCircleRadius - directionLength;
        bottomDirectionPath.moveTo(canvasCenterX, bottomY);
        bottomDirectionPath.lineTo(canvasCenterX - directionLength, bottomY - directionLength);
        bottomDirectionPath.moveTo(canvasCenterX, bottomY);
        bottomDirectionPath.lineTo(canvasCenterX + directionLength, bottomY - directionLength);
        canvas.drawPath(bottomDirectionPath, bottomDirectionPaint);

        //内圆圆圈
        canvas.drawCircle(canvasCenterX, canvasCenterY, (float) outerCircleDiameter / 6, innerCirclePaint);

        //中间开关
        canvas.drawArc(centerSwitchOval, (-90 + 25), (360 - 50), false, centerSwitchPaint);
        canvas.drawLine(canvasCenterX, canvasCenterY - (float) outerCircleDiameter / 15 - 2, canvasCenterX, canvasCenterY - (float) outerCircleDiameter / 15 + 15, centerSwitchPaint);

        //根据点击位置设置外圆环颜色
        if (leftTurn) {
            leftDirectionPaint.setColor(Color.WHITE);
            canvas.drawArc(outerCircleRectF, (float) (90 * 2 - 45), 90f, false, leftDirectionPaint);
        } else {
            leftDirectionPaint.setColor(borderColor);
        }

        if (topTurn) {
            topDirectionPaint.setColor(Color.WHITE);
            canvas.drawArc(outerCircleRectF, (float) (90 * 3 - 45), 90f, false, topDirectionPaint);
        } else {
            topDirectionPaint.setColor(borderColor);
        }

        if (rightTurn) {
            rightDirectionPaint.setColor(Color.WHITE);
            canvas.drawArc(outerCircleRectF, -45f, 90f, false, rightDirectionPaint);
        } else {
            rightDirectionPaint.setColor(borderColor);
        }

        if (bottomTurn) {
            bottomDirectionPaint.setColor(Color.WHITE);
            canvas.drawArc(outerCircleRectF, 45f, 90f, false, bottomDirectionPaint);
        } else {
            bottomDirectionPaint.setColor(borderColor);
        }

        if (centerTurn) {
            innerCirclePaint.setColor(Color.WHITE);
            centerSwitchPaint.setColor(borderColor);
        } else {
            innerCirclePaint.setColor(borderColor);
            centerSwitchPaint.setColor(Color.WHITE);
        }

        invalidate();
    }

    private OnWheelTouchListener listener;

    public interface OnWheelTouchListener {
        /**
         * 左
         */
        void onLeftTurn();

        /**
         * 上
         */
        void onTopTurn();

        /**
         * 右
         */
        void onRightTurn();

        /**
         * 下
         */
        void onBottomTurn();

        /**
         * 中间
         */
        void onCenterTurn();

        /**
         * 松开
         */
        void onActionTurnUp(Direction dir);
    }

    public void setOnWheelTouchListener(OnWheelTouchListener listener) {
        this.listener = listener;
    }

    public enum Direction {
        LEFT, TOP, RIGHT, BOTTOM, CENTER
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                // 计算角度
                double mc = Math.atan2(y - canvasCenterY, x - canvasCenterX);
                double mk = 180 * mc / Math.PI;
                /**
                 *             |
                 *  [-180,-90] |  [-90,0]
                 *             |
                 * -------------------------
                 *             |
                 *  [180,90]   |  [90,0]
                 *             |
                 * */
                // 计算点击的距离，区分点击的是环还是中心位置
                double mj = Math.sqrt(Math.pow((x - canvasCenterX), 2) + Math.pow(y - canvasCenterY, 2));
                setDefaultValue();

                // 判断
                if (mj > (float) outerCircleDiameter / 15 + 20) {
                    if (mk < -45 && mk > -180 + 45) {
                        topTurn = true;
                        listener.onTopTurn();
                    } else if (mk > -45 && mk < 45) {
                        rightTurn = true;
                        listener.onRightTurn();
                    } else if (mk > 45 && mk < 180 - 45) {
                        bottomTurn = true;
                        listener.onBottomTurn();
                    } else {
                        leftTurn = true;
                        listener.onLeftTurn();
                    }
                } else {
                    centerTurn = true;
                    listener.onCenterTurn();
                }
                // 重绘
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (leftTurn) {
                    leftTurn = false;
                    listener.onActionTurnUp(Direction.LEFT);
                } else if (topTurn) {
                    topTurn = false;
                    listener.onActionTurnUp(Direction.TOP);
                } else if (rightTurn) {
                    rightTurn = false;
                    listener.onActionTurnUp(Direction.RIGHT);
                } else if (bottomTurn) {
                    bottomTurn = false;
                    listener.onActionTurnUp(Direction.BOTTOM);
                } else {
                    centerTurn = false;
                    listener.onActionTurnUp(Direction.CENTER);
                }
                invalidate();
                break;
        }
        return true;
    }

    //每次手指抬起都重置方向状态
    private void setDefaultValue() {
        leftTurn = false;
        topTurn = false;
        rightTurn = false;
        bottomTurn = false;
        centerTurn = false;
    }
}
