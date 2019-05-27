package com.example.mutidemo.ui;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.GridViewAdapter;
import com.example.mutidemo.bean.WeatherBean;
import com.example.mutidemo.mvp.presenter.WeatherPresenterImpl;
import com.example.mutidemo.mvp.view.IWeatherView;
import com.example.mutidemo.util.OtherUtil;
import com.example.mutidemo.widget.FramedGridView;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MVPActivity extends BaseNormalActivity implements IWeatherView, View.OnClickListener {

    @BindView(R.id.mFramedGridView)
    FramedGridView mFramedGridView;
    @BindView(R.id.mMVP_TextView_cityName)
    TextView mMVPTextViewCityName;
    @BindView(R.id.mMVP_ImageView_getLocation)
    ImageView mMVPImageViewGetLocation;
    @BindView(R.id.mMVP_TextView_temp)
    TextView mMVPTextViewTemp;
    @BindView(R.id.mMVP_TextView_weather_1)
    TextView mMVPTextViewWeather1;
    @BindView(R.id.mMVP_TextView_winddirect)
    TextView mMVPTextViewWinddirect;
    @BindView(R.id.mMVP_TextView_windpower)
    TextView mMVPTextViewWindpower;
    @BindView(R.id.mMVP_TextView_humidity)
    TextView mMVPTextViewHumidity;
    @BindView(R.id.mMVP_TextView_weather_2)
    TextView mMVPTextViewWeather2;
    @BindView(R.id.mMVP_TextView_templow)
    TextView mMVPTextViewTemplow;
    @BindView(R.id.mMVP_TextView_temphigh)
    TextView mMVPTextViewTemphigh;
    @BindView(R.id.mMVP_TextView_pressure)
    TextView mMVPTextViewPressure;
    @BindView(R.id.mMVP_TextView_date)
    TextView mMVPTextViewDate;
    @BindView(R.id.mMVP_TextView_week)
    TextView mMVPTextViewWeek;

    private WeatherPresenterImpl weatherPresenter;
    private ProgressDialog progressDialog;

    @Override
    public void initView() {
        setContentView(R.layout.activity_mvp);
    }

    @Override
    public void init() {
        weatherPresenter = new WeatherPresenterImpl(this);
    }

    @Override
    public void initEvent() {
        weatherPresenter.onReadyRetrofitRequest("北京", 1, 101010100);
    }

    @Override
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载数据...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    @Override
    public void hideProgress() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showNetWorkData(WeatherBean weatherBean) {
        if (weatherBean != null) {
            //TODO 显示当天的详细天气情况
            WeatherBean.ResultBeanX.ResultBean resultBean = weatherBean.getResult().getResult();
            bindResultData(resultBean);

            //TODO 显示当天24h的天气情况
            List<WeatherBean.ResultBeanX.ResultBean.HourlyBean> hourlyBeanList = weatherBean.getResult().getResult().getHourly();
            bindHourlyData(hourlyBeanList);

            //TODO 将一周内的天气情况作为画廊形式展示
            List<WeatherBean.ResultBeanX.ResultBean.DailyBean> dailyBeanList = weatherBean.getResult().getResult().getDaily();
            bindDailyData(dailyBeanList);

            //TODO 绑定GridView
            List<WeatherBean.ResultBeanX.ResultBean.IndexBean> indexBeanList = weatherBean.getResult().getResult().getIndex();
            bindIndexData(indexBeanList);
        } else {
            ToastUtil.showBeautifulToast("获取数据失败，请重试", 5);
        }
    }

    private void bindResultData(WeatherBean.ResultBeanX.ResultBean resultBean) {
        mMVPTextViewCityName.setText(resultBean.getCity());
        mMVPTextViewTemp.setText(resultBean.getTemp() + "°");
        mMVPTextViewWeather1.setText(resultBean.getWeather());
        mMVPTextViewWinddirect.setText(resultBean.getWinddirect());
        mMVPTextViewWindpower.setText(resultBean.getWindpower());
        mMVPTextViewHumidity.setText("湿度\r\r" + resultBean.getHumidity() + "%");
        mMVPTextViewPressure.setText("气压\r\r" + resultBean.getPressure() + "Pa");
        mMVPTextViewDate.setText("\r\r" + resultBean.getDate() + "\r\r");
        mMVPTextViewWeek.setText(resultBean.getWeek());
        mMVPTextViewTemplow.setText(resultBean.getTemplow() + "~");
        mMVPTextViewTemphigh.setText(resultBean.getTemphigh() + "°");
        mMVPTextViewWeather2.setText(resultBean.getWeather());
        ToastUtil.showBeautifulToast("更新时间：" + resultBean.getUpdatetime(), 3);
    }

    private void bindHourlyData(List<WeatherBean.ResultBeanX.ResultBean.HourlyBean> hourlyBeanList) {

    }

    private void bindDailyData(List<WeatherBean.ResultBeanX.ResultBean.DailyBean> dailyBeanList) {

    }

    private void bindIndexData(List<WeatherBean.ResultBeanX.ResultBean.IndexBean> indexBeanList) {
        GridViewAdapter mGridViewAdapter = new GridViewAdapter(this, indexBeanList);
        mFramedGridView.setAdapter(mGridViewAdapter);
        OtherUtil.measureViewHeight(this, mFramedGridView);//计算GridView的实际高度
        mFramedGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String detail = indexBeanList.get(position).getDetail();
                ToastUtil.showBeautifulToast(detail, 3);
            }
        });
    }

    @OnClick(R.id.mMVP_ImageView_getLocation)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mMVP_ImageView_getLocation:
                ToastUtil.showBeautifulToast("mMVP_ImageView_getLocation", 3);
                break;

            default:
                break;
        }
    }
}