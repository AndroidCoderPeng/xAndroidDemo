package com.example.mutidemo.mvp.model;

import rx.Subscription;

public interface IWeatherModel {
    /**
     * 将WeatherPresenterImpl发起的请求通过订阅的方式，用Okhttp+retrofit请求数据，真正发起请求
     */
    Subscription sendRetrofitRequest(String city, int cityid, int citycode);
}
