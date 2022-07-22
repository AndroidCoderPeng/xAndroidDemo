package com.example.mutidemo.mvvm.retrofit;


import com.example.mutidemo.util.DemoConstant;

import okhttp3.ResponseBody;
import rx.Observable;


public class RetrofitServiceManager {

    private static final RetrofitService api = RetrofitFactory.createRetrofit(DemoConstant.BASE_URL, RetrofitService.class);

    public static Observable<ResponseBody> obtainWeatherData(String city, int cityId, int cityCode) {
        return api.obtainWeatherData(DemoConstant.APP_KEY, city, cityId, cityCode);
    }
}