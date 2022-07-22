package com.example.mutidemo.util.retrofit;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface RetrofitService {
    /**
     * 天气详情
     * https://way.jd.com/jisuapi/weather?city=北京&cityid=1&citycode=101010100&appkey=e957ed7ad90436a57e604127d9d8fa32
     */
    @GET("/jisuapi/weather")
    Observable<ResponseBody> obtainWeatherDetail(
            @Query("appkey") String appkey,
            @Query("city") String city,
            @Query("cityid") int cityid,
            @Query("citycode") int citycode
    );

    /**
     * https://way.jd.com/jisuapi/get?channel=头条&num=10&start=0&appkey=e957ed7ad90436a57e604127d9d8fa32
     */
    @GET("/jisuapi/get")
    Observable<ResponseBody> obtainNewsList(
            @Query("appkey") String appkey,
            @Query("channel") String channel,
            @Query("num") int num,
            @Query("start") int start
    );
}
