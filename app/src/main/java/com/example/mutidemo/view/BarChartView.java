package com.example.mutidemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import org.xclcharts.chart.BarChart;
import org.xclcharts.chart.BarData;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.plot.PlotGrid;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/15.
 */

public class BarChartView extends ChartBaseView {

    private static final String TAG = "BarChartView";
    private BarChart chart = new BarChart();

    //标签轴
    private List<String> chartLabels = new ArrayList<>();
    private List<BarData> chartData = new ArrayList<>();

    public BarChartView(Context context) {
        super(context);
        initView();
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        chartLabels();
        chartDataSet();
        chartRender();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //图所占范围大小
        if (null != chart) {
            chart.setChartRange(w, h);
        }
    }


    private void chartRender() {
        try {
            int[] ltrb = getBarLnDefaultSpadding();
            chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);

            chart.setTitleVerticalAlign(XEnum.VerticalAlign.MIDDLE);
            chart.setTitleAlign(XEnum.HorizontalAlign.LEFT);

            chart.showRoundBorder();
            //数据源
            chart.setDataSource(chartData);
            chart.setCategories(chartLabels);

            //数据轴
            chart.getDataAxis().setAxisMax(getYSum());
            chart.getDataAxis().setAxisSteps(getYSteps());

            /**
             * 设置X、Y轴刻度粗细和颜色
             * */
            chart.getDataAxis().getTickMarksPaint().setStrokeWidth(1);
            chart.getCategoryAxis().getTickMarksPaint().setStrokeWidth(1);

            /**
             * 设置X、Y轴线粗细和颜色
             * */
            chart.getCategoryAxis().getAxisPaint().setStrokeWidth(1);
            chart.getDataAxis().getAxisPaint().setStrokeWidth(1);

            /**
             * 背景网格
             * */
            PlotGrid plot = chart.getPlotGrid();
            plot.showHorizontalLines();
            plot.showVerticalLines();

            /**
             * 横向网格线
             * */
            plot.getHorizontalLinePaint().setStrokeWidth(0.5f);
            plot.getHorizontalLinePaint().setColor(Color.rgb(204, 204, 204));
            plot.setHorizontalLineStyle(XEnum.LineStyle.DASH);
            /**
             * 纵向网格线
             * */
            plot.getVerticalLinePaint().setStrokeWidth(0.5f);
            plot.getVerticalLinePaint().setColor(Color.rgb(204, 204, 204));
            plot.setVerticalLineStyle(XEnum.LineStyle.DASH);

            //横向显示柱形
            chart.setChartDirection(XEnum.Direction.HORIZONTAL);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void chartDataSet() {
        //标签对应的柱形数据集
        List<Double> dataSeriesA = new ArrayList<>();
        dataSeriesA.add((double) 200);
        dataSeriesA.add((double) 250);
        dataSeriesA.add((double) 400);
        dataSeriesA.add((double) 100);
        dataSeriesA.add((double) 500);
        BarData BarDataA = new BarData("分类", dataSeriesA, Color.rgb(0, 0, 255));
        chartData.add(BarDataA);
    }

    private void chartLabels() {
        chartLabels.add("擂茶");
        chartLabels.add("槟榔");
        chartLabels.add("纯净水");
        chartLabels.add("擂茶");
        chartLabels.add("槟榔");
    }


    @Override
    public void render(Canvas canvas) {
        try {
            chart.render(canvas);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * 设置Y轴最大值
     */
    private double getYSum() {
        //TODO 获取Sum
        int Sum = 0;

        return 2000;
    }

    /**
     * 设置Y轴刻度大小
     */
    private double getYSteps() {
        int ysteps = 0;
        float axisMax = chart.getDataAxis().getAxisMax();
        float axisMin = chart.getDataAxis().getAxisMin();
        ysteps = (int) ((axisMax - axisMin) / 10);
        return ysteps;
    }
}
