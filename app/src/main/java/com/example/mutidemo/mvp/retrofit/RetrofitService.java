package com.example.mutidemo.mvp.retrofit;

import com.example.mutidemo.bean.WeatherBean;
import com.example.mutidemo.util.Constant;

import okhttp3.ResponseBody;
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


    /**
     * Gson转换为实体类失败，强制不转换，用默认的OkHttp3的实体接收
     * <p>
     * https://route.showapi.com/109-35?channelId=57463656a44a13cf&channelName=旅游最新&maxResult=20&needAllList=0&needHtml=1&showapi_appid=166496&showapi_sign=0db25ea1889a4b7a9e12956478769f78&page=1&showapi_timestamp=20200330153207
     */
    @GET("109-35?channelId=57463656a44a13cf&channelName=旅游最新&maxResult=20&needAllList=1&needHtml=1&showapi_appid=" + Constant.API_ID + "&showapi_sign=" + Constant.API_SIGN)
    Observable<ResponseBody> getData(@Query("page") int page,
                                     @Query("showapi_timestamp") long timestamp);
}
