package com.example.mutidemo.ui;

import android.annotation.SuppressLint;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mutidemo.adapter.WeatherAdapter;
import com.example.mutidemo.bean.WeatherDetailBean;
import com.example.mutidemo.databinding.ActivityMvpBinding;
import com.example.mutidemo.mvp.presenter.WeatherDetailPresenterImpl;
import com.example.mutidemo.mvp.view.IWeatherDetailView;
import com.example.mutidemo.util.OtherUtils;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;

public class MVPActivity extends AndroidxBaseActivity<ActivityMvpBinding> implements IWeatherDetailView {

    private WeatherDetailPresenterImpl weatherPresenter;

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    public void initData() {
        weatherPresenter = new WeatherDetailPresenterImpl(this);
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
    public void showNetWorkData(WeatherDetailBean weatherBean) {
        if (weatherBean != null) {
            WeatherDetailBean.ResultBeanX.ResultBean result = weatherBean.getResult().getResult();
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