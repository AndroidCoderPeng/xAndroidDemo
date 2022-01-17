package com.example.mutidemo.ui;

import android.annotation.SuppressLint;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mutidemo.adapter.WeatherAdapter;
import com.example.mutidemo.base.AndroidxBaseActivity;
import com.example.mutidemo.bean.WeatherBean;
import com.example.mutidemo.databinding.ActivityMvpBinding;
import com.example.mutidemo.mvp.presenter.WeatherPresenterImpl;
import com.example.mutidemo.mvp.view.IWeatherView;
import com.example.mutidemo.util.OtherUtils;

public class MVPActivity extends AndroidxBaseActivity<ActivityMvpBinding> implements IWeatherView {

    private WeatherPresenterImpl weatherPresenter;

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
            viewBinding.tempView.setText(result.getTemp() + "°");
            viewBinding.weatherView.setText(result.getWeather());
            viewBinding.tempFieldView.setText(result.getTemplow() + "°~" + result.getTemphigh() + "°");
            viewBinding.windView.setText(result.getWinddirect() + result.getWindpower());
            viewBinding.locationView.setText(result.getCity());

            //获取接下来一周的天气
            WeatherAdapter weatherAdapter = new WeatherAdapter(this, result.getDaily());
            viewBinding.weatherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            viewBinding.weatherRecyclerView.setAdapter(weatherAdapter);
        }
    }

    @Override
    public void onDestroy() {
        if (weatherPresenter != null) {
            weatherPresenter.disposeRetrofitRequest();
        }
        super.onDestroy();
    }
}