package com.example.mutidemo.mvp.retrofit;

import android.util.Log;

import com.example.mutidemo.bean.WeatherBean;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class RetrofitServiceManager {

    private static final String TAG = "RetrofitServiceManager";

    public static Retrofit createRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())//Gson转换器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(getOkHttpClient())//log拦截器
                .build();
    }

    private static OkHttpClient getOkHttpClient() {
        //日志显示级别
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG, "OkHttp ======>" + message);
            }
        });
        interceptor.setLevel(level);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor);
        return builder.build();
    }

    /**
     * 获取天气详情信息，
     *
     * @param city
     * @param cityid
     * @param citycode
     * @return
     */
    public static Observable<WeatherBean> getWeatherData(String baseUrl, String city, int cityid, int citycode) {
        Retrofit retrofit = createRetrofit(baseUrl);
        RetrofitService service = retrofit.create(RetrofitService.class);
        return service.getData(city, cityid, citycode);
    }
}
