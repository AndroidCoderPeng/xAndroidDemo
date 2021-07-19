package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.WeatherAdapter;
import com.example.mutidemo.bean.WeatherBean;
import com.example.mutidemo.mvp.presenter.WeatherPresenterImpl;
import com.example.mutidemo.mvp.view.IWeatherView;
import com.example.mutidemo.util.OtherUtils;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import butterknife.BindView;

public class MVPActivity extends BaseNormalActivity implements IWeatherView {

    @BindView(R.id.tempView)
    TextView tempView;
    @BindView(R.id.weatherView)
    TextView weatherView;
    @BindView(R.id.tempFieldView)
    TextView tempFieldView;
    @BindView(R.id.windView)
    TextView windView;
    @BindView(R.id.locationView)
    TextView locationView;
    @BindView(R.id.weatherRecyclerView)
    RecyclerView weatherRecyclerView;

    private WeatherPresenterImpl weatherPresenter;

    @Override
    public int initLayoutView() {
        return R.layout.activity_mvp;
    }

    @Override
    public void initData() {
        weatherPresenter = new WeatherPresenterImpl(this);
    }

    @Override
    public void initEvent() {
        weatherPresenter.onReadyRetrofitRequest("北京", 1, 101010100);
    }

    @Override
    public void showProgress() {
        OtherUtils.showLoadingDialog(this, "加载数据中，请稍后...");
    }

    @Override
    public void hideProgress() {
        OtherUtils.dismissLoadingDialog();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void showNetWorkData(WeatherBean weatherBean) {
        if (weatherBean != null) {
            WeatherBean.ResultBeanX.ResultBean result = weatherBean.getResult().getResult();
            tempView.setText(result.getTemp() + "°");
            weatherView.setText(result.getWeather());
            tempFieldView.setText(result.getTemplow() + "°~" + result.getTemphigh() + "°");
            windView.setText(result.getWinddirect() + result.getWindpower());
            locationView.setText(result.getCity());

            //获取接下来一周的天气
            WeatherAdapter weatherAdapter = new WeatherAdapter(this, result.getDaily());
            weatherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            weatherRecyclerView.setAdapter(weatherAdapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (weatherPresenter != null) {
            weatherPresenter.disposeRetrofitRequest();
        }
    }
}