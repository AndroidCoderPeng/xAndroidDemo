package com.example.mutidemo.mvp.presenter;

import com.example.mutidemo.bean.WeatherDetailBean;
import com.example.mutidemo.mvp.BasePresenter;
import com.example.mutidemo.mvp.model.WeatherDetailModelImpl;
import com.example.mutidemo.mvp.view.IWeatherDetailView;

public class WeatherDetailPresenterImpl extends BasePresenter implements IWeatherDetailPresenter, WeatherDetailModelImpl.OnWeatherListener {

    private final IWeatherDetailView view;
    private final WeatherDetailModelImpl weatherModel;

    public WeatherDetailPresenterImpl(IWeatherDetailView weatherView) {
        this.view = weatherView;
        weatherModel = new WeatherDetailModelImpl(this);
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
    public void onSuccess(WeatherDetailBean weatherBean) {
        view.hideProgress();
        view.showNetWorkData(weatherBean);
    }

    @Override
    public void onFailure(Throwable throwable) {
        view.hideProgress();
    }
}
