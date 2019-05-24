package com.example.mutidemo.mvp.retrofit;

import com.example.mutidemo.bean.WeatherBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface RetrofitService {
    /**
     * 天气详情
     * https://way.jd.com/jisuapi/weather?city=北京&cityid=1&citycode=101010100&appkey=e957ed7ad90436a57e604127d9d8fa32
     */
    @GET("jisuapi/weather?appkey=e957ed7ad90436a57e604127d9d8fa32")
    Observable<WeatherBean> getData(@Query("city") String city,
                                           @Query("cityid") int cityid,
                                           @Query("citycode") int citycode);
}
