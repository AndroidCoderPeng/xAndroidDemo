package com.example.mutidemo.util.retrofit;

import com.example.mutidemo.util.DemoConstant;

import okhttp3.ResponseBody;
import rx.Observable;

public class RetrofitServiceManager {

    private static final String TAG = "RetrofitServiceManager";

    private static final RetrofitService api = RetrofitFactory.createRetrofit(DemoConstant.BASE_URL, RetrofitService.class);

    public static Observable<ResponseBody> obtainWeatherDetail(String city, int cityId, int cityCode) {
        return api.obtainWeatherDetail(DemoConstant.APP_KEY, city, cityId, cityCode);
    }

    public static Observable<ResponseBody> obtainNewsList(String channel, int start) {
        return api.obtainNewsList(DemoConstant.APP_KEY, channel, 15, start);
    }
}
