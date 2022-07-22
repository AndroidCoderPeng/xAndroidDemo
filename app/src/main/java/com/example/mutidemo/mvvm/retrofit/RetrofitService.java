package com.example.mutidemo.mvvm.retrofit;


import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

interface RetrofitService {
    /**
     * 获取天气数据
     */
    @GET("/jisuapi/weather")
    Observable<ResponseBody> obtainWeatherData(
            @Query("appkey") String appkey,
            @Query("city") String city,
            @Query("cityid") int cityid,
            @Query("citycode") int citycode
    );
}