package com.example.mutidemo.mvp.presenter;

import com.example.mutidemo.bean.WeatherBean;
import com.example.mutidemo.mvp.BasePresenter;
import com.example.mutidemo.mvp.model.WeatherModelImpl;
import com.example.mutidemo.mvp.view.IWeatherView;

public class WeatherPresenterImpl extends BasePresenter implements IWeatherPresenter, WeatherModelImpl.OnWeatherListener {

    private IWeatherView view;
    private WeatherModelImpl weatherModel;

    public WeatherPresenterImpl(IWeatherView weatherView) {
        this.view = weatherView;
        weatherModel = new WeatherModelImpl(this);
    }

    /**
     * 唤醒订阅
     */
    @Override
    public void onReadyRetrofitRequest(String city, int cityid, int citycode) {
        view.showProgress();
        addSubscription(weatherModel.sendRetrofitRequest(city, cityid, citycode));
    }

    @Override
    public void disposeRetrofitRequest() {
        unSubscription();
    }

    @Override
    public void onSuccess(WeatherBean weatherBean) {
        view.hideProgress();
        view.showNetWorkData(weatherBean);
    }

    @Override
    public void onFailure(Throwable throwable) {
        view.hideProgress();
    }
}
