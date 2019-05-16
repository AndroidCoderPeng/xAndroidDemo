package com.example.mutidemo.view;

import android.content.Context;
import android.util.AttributeSet;

import org.xclcharts.common.DensityUtil;
import org.xclcharts.view.ChartView;

/**
 * Created by Administrator on 2017/12/13.
 */

public class ChartBaseView extends ChartView {
    public ChartBaseView(Context context) {
        super(context);
    }

    public ChartBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChartBaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    //bar、line chart所使用的默认偏移值。
    protected int[] getBarLnDefaultSpadding() {
        int[] ltrb = new int[4];
        ltrb[0] = DensityUtil.dip2px(getContext(), 45); //left
        ltrb[1] = DensityUtil.dip2px(getContext(), 15); //top
        ltrb[2] = DensityUtil.dip2px(getContext(), 10); //right
        ltrb[3] = DensityUtil.dip2px(getContext(), 20); //bottom
        return ltrb;
    }

    //雷达图所使用的默认偏移值。
    protected int[] getPieDefaultSpadding() {
        int[] ltrb = new int[4];
        ltrb[0] = DensityUtil.dip2px(getContext(), 20); //left
        ltrb[1] = DensityUtil.dip2px(getContext(), 65); //top
        ltrb[2] = DensityUtil.dip2px(getContext(), 20); //right
        ltrb[3] = DensityUtil.dip2px(getContext(), 20); //bottom
        return ltrb;
    }
}
