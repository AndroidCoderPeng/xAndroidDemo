package com.example.mutidemo.view;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import com.example.mutidemo.bean.TestBean;
import com.google.gson.Gson;

import org.xclcharts.chart.PointD;
import org.xclcharts.chart.SplineChart;
import org.xclcharts.chart.SplineData;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.plot.PlotGrid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2017/12/13.
 * <p>
 * 普通折线图
 */

public class LineChartView extends ChartBaseView {

    private String TAG = "LineChartView";
    private SplineChart chart = new SplineChart();
    //分类轴标签集合
    private LinkedList<String> labels = new LinkedList<>();
    private LinkedList<SplineData> chartData = new LinkedList<>();
//    Paint pToolTip = new Paint(Paint.ANTI_ALIAS_FLAG);


    public LineChartView(Context context) {
        super(context);
        initView();
    }

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        chartLabels();
        chartDataSet();
        chartRender();

//        /**
//         * 綁定手势滑动事件
//         * */
//        this.bindTouch(this, chart);
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
            /**
             *  设置表格与外围边框的距离，四个参数分别是左上右下
             *  默认设置的图表是满屏
             * */
            int[] defaultSpadding = getBarLnDefaultSpadding();
            chart.setPadding(defaultSpadding[0], defaultSpadding[1], defaultSpadding[2], defaultSpadding[3]);

            /**
             * 显示外围边框，非表格边框
             * showBorder()---方角边框
             * showRoundBorder()---圆角边框
             * */
            chart.showRoundBorder();

            /**
             * 加载横坐标和数据源
             * */
            chart.setCategories(labels);
            chart.setDataSource(chartData);

            /**
             * Y轴属性
             * */
            chart.getDataAxis().setAxisMax(getYMax());
            chart.getDataAxis().setAxisSteps(getYSteps());

            /**
             * X轴属性
             * */
            chart.setCategoryAxisMax(getDayOfMonth());
            chart.setCategoryAxisMin(1);

            /**
             * 背景网格
             * */
            PlotGrid plot = chart.getPlotGrid();
            plot.showHorizontalLines();
//            plot.showVerticalLines();

            /**
             * 横向网格线
             * */
            plot.getHorizontalLinePaint().setStrokeWidth(0.5f);
            plot.getHorizontalLinePaint().setColor(Color.rgb(204, 204, 204));
            plot.setHorizontalLineStyle(XEnum.LineStyle.DASH);

//            /**
//             * 纵向网格线
//             * */
//            plot.getVerticalLinePaint().setStrokeWidth(0.5f);
//            plot.getVerticalLinePaint().setColor(Color.rgb(204, 204, 204));
//            plot.setVerticalLineStyle(XEnum.LineStyle.DASH);

            /**
             * 设置X、Y轴线粗细和颜色
             * */
//            chart.getCategoryAxis().getAxisPaint().setColor(Color.rgb(0x0, 0x0, 0x0));
            chart.getCategoryAxis().getAxisPaint().setStrokeWidth(1);
//            chart.getDataAxis().getAxisPaint().setColor(Color.rgb(0x0, 0x0, 0x0));
            chart.getDataAxis().getAxisPaint().setStrokeWidth(1);

//            /**
//             * 设置X、Y轴数值颜色
//             * */
//            chart.getCategoryAxis().getTickLabelPaint().setColor(Color.rgb(0x0, 0x0, 0x0));
//            chart.getDataAxis().getTickLabelPaint().setColor(Color.rgb(0x0, 0x0, 0x0));

            /**
             * 设置X、Y轴刻度粗细和颜色
             * */
//            chart.getDataAxis().getTickMarksPaint().setColor(Color.rgb(0x0, 0x0, 0x0));
            chart.getDataAxis().getTickMarksPaint().setStrokeWidth(1);
//            chart.getCategoryAxis().getTickMarksPaint().setColor(Color.rgb(0x0, 0x0, 0x0));
            chart.getCategoryAxis().getTickMarksPaint().setStrokeWidth(1);

//            /**
//             * 设置刻度在轴线中间
//             * */
//            chart.getDataAxis().setHorizontalTickAlign(Paint.Align.CENTER);
//            chart.getDataAxis().getTickLabelPaint().setTextAlign(Paint.Align.CENTER);

//            /**
//             * 定义交叉点标签显示格式,特别备注,因曲线图的特殊性，所以返回格式为:  x值,y值
//             * */
//            chart.setDotLabelFormatter(new IFormatterTextCallBack() {
//
//                @Override
//                public String textFormatter(String value) {
//                    String label = "(" + value + ")";
//                    return (label);
//                }
//
//            });

//            /**
//             * 标题
//             * */
//            chart.setTitle("XCL-Chart开源库");
//            chart.addSubtitle("折线图精简版");

//            /**
//             * 激活数据散点的点击监听
//             * 为了让触发更灵敏，可以扩大5px的点击监听范围
//             * */
//            chart.ActiveListenItemClick();
//            chart.extPointClickRange(5);
//            chart.showClikedFocus();

//            /**
//             * 显示十字交叉线
//             * */
//            chart.showDyLine();
//            chart.getDyLine().setDyLineStyle(XEnum.DyLineStyle.Cross);

//            /**
//             * 封闭轴
//             * */
//            chart.setAxesClosed(true);

            /**
             * 将线显示为直线，而不是平滑的
             * */
            chart.setCrurveLineStyle(XEnum.CrurveLineStyle.BEELINE);
//            chart.setCrurveLineStyle(XEnum.CrurveLineStyle.BEZIERCURVE);//平滑曲线

            /**
             * 不使用精确计算，忽略Java计算误差,提高性能
             * */
            chart.disableHighPrecision();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    /**
     * 设置Y轴最大值
     */
    private double getYMax() {
        //TODO 获取最大值
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

    private void chartDataSet() {
        //TODO 需要根据项目需求改为自动添加数据，此处为测试数据。需要从数据库里面读取数据
        List<PointD> linePoint = new ArrayList<>();
        String jsonFromAssets = getJsonFromAssets().toString();
        /**转换为实体类**/
        Gson gson = new Gson();
        TestBean testBean = gson.fromJson(jsonFromAssets, TestBean.class);
        List<TestBean.ResultBean> result = testBean.getResult();
        /****/
        for (int i = 0; i < result.size(); i++) {
            double date = Double.parseDouble(result.get(i).getDate());
            double amount = Double.parseDouble(result.get(i).getAmount());
            for (int j = 1; j <= getDayOfMonth(); j++) {
                linePoint.add(new PointD(date, amount));
            }
        }
        SplineData dataSeries = new SplineData("价格", linePoint,
                Color.rgb(62, 161, 69));
        /**
         * 把线弄细点
         * */
        dataSeries.getLinePaint().setStrokeWidth(3);
        /**
         * 隐藏线上面的小点
         * */
        dataSeries.setDotStyle(XEnum.DotStyle.HIDE);
        chartData.add(dataSeries);
    }

    /**
     * 从assets目录下读取数据，模拟读取数据库
     */
    public String getJsonFromAssets() {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = getContext().getAssets();
            //通过管理器打开文件并读取
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    assetManager.open("test.json")));
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 获取当前月的天数
     */
    public int getDayOfMonth() {
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        int day = aCalendar.getActualMaximum(Calendar.DATE);
        return day;
    }

    /**
     * 读取每月天数添加到横坐标作为刻度
     */
    private void chartLabels() {
        for (int i = 1; i <= getDayOfMonth(); i++) {
            if (i % 3 == 1) {
                labels.add(i + "");
            } else {
                labels.add("");
            }
        }
    }

    @Override
    public void render(Canvas canvas) {
        try {
            //绘图
            chart.render(canvas);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        super.onTouchEvent(event);
//
//        if (event.getAction() == MotionEvent.ACTION_UP) {
//            triggerClick(event.getX(), event.getY());
//        }
//        return true;
//    }
//
//
//    //触发交叉点监听事件
//    private void triggerClick(float x, float y) {
//        //交叉线
//        if (chart.getDyLineVisible()) {
//            chart.getDyLine().setCurrentXY(x, y);
//        }
//        if (!chart.getListenItemClickStatus()) {
//            if (chart.getDyLineVisible() && chart.getDyLine().isInvalidate()) {
//                this.invalidate();
//            }
//        } else {
//            PointPosition record = chart.getPositionRecord(x, y);
//            if (null == record) {
//                return;
//            }
//            if (record.getDataID() >= chartData.size()) {
//                return;
//            }
//            SplineData lData = chartData.get(record.getDataID());
//            List<PointD> linePoint = lData.getLineDataSet();
//            int pos = record.getDataChildID();
//            int i = 0;
//            Iterator it = linePoint.iterator();
//            while (it.hasNext()) {
//                PointD entry = (PointD) it.next();
//
//                if (pos == i) {
//                    Double xValue = entry.x;
//                    Double yValue = entry.y;
//                    /**
//                     * 在点击处显示圆圈
//                     * */
//                    float r = record.getRadius();
//                    chart.showFocusPointF(record.getPosition(), r * 2);
//                    chart.getFocusPaint().setStyle(Paint.Style.STROKE);
//                    chart.getFocusPaint().setStrokeWidth(1);
//                    chart.getFocusPaint().setColor(Color.RED);
//
//                    /**
//                     * 在点击处显示tooltip
//                     * */
//                    pToolTip.setColor(Color.RED);
//                    chart.getToolTip().setCurrentXY(x, y);
//                    chart.getToolTip().addToolTip(" Key:" + lData.getLineKey(), pToolTip);
//                    chart.getToolTip().addToolTip(" Label:" + lData.getLabel(), pToolTip);
//                    chart.getToolTip().addToolTip(" Current Value:" + Double.toString(xValue) + "," + Double.toString(yValue), pToolTip);
//                    chart.getToolTip().getBackgroundPaint().setAlpha(100);
//                    this.invalidate();
//
//                    break;
//                }
//                i++;
//            }//end while
//        }
//    }
}
