package com.example.mutidemo.mvp.presenter;

import com.example.mutidemo.bean.WeatherBean;
import com.example.mutidemo.mvp.BasePresenter;
import com.example.mutidemo.mvp.model.WeatherModelImpl;
import com.example.mutidemo.mvp.view.IWeatherView;

public class WeatherPresenterImpl extends BasePresenter implements IWeatherPresenter, WeatherModelImpl.OnWeatherListener {

    private IWeatherView iWeatherView;
    private WeatherModelImpl weatherModel;

    public WeatherPresenterImpl(IWeatherView view) {
        this.iWeatherView = view;
        weatherModel = new WeatherModelImpl(this);
    }

    /**
     * 唤醒订阅
     */
    @Override
    public void onReadyRetrofitRequest(String city, int cityid, int citycode) {
        iWeatherView.showProgress();
        addSubscription(weatherModel.sendRetrofitRequest(city, cityid, citycode));
    }

    @Override
    public void onSuccess(WeatherBean weatherBean) {
        iWeatherView.hideProgress();
        /**
         * 将返回的数据传递给View并显示在Activity/Fragment上面
         */
        iWeatherView.showNetWorkData(weatherBean);
    }

    @Override
    public void onFailure(Throwable throwable) {
        iWeatherView.hideProgress();
    }
}
