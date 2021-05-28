package com.example.mutidemo.mvp.retrofit;

import android.util.Log;

import com.example.mutidemo.bean.WeatherBean;

import org.jetbrains.annotations.NotNull;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class RetrofitServiceManager {

    private static final String TAG = "RetrofitServiceManager";

    private static Retrofit createRetrofit(String baseUrl) {
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
            public void log(@NotNull String message) {
                Log.d(TAG, "OkHttp返回值: " + message);
            }
        });
        interceptor.setLevel(level);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor);
        return builder.build();
    }

    public static Observable<WeatherBean> getWeatherData(String baseUrl, String city, int cityId, int cityCode) {
        Retrofit retrofit = createRetrofit(baseUrl);
        RetrofitService service = retrofit.create(RetrofitService.class);
        return service.getData(city, cityId, cityCode);
    }

    public static Observable<ResponseBody> getNewsData(String baseUrl, int page, long timestamp) {
        Retrofit retrofit = createRetrofit(baseUrl);
        RetrofitService service = retrofit.create(RetrofitService.class);
        return service.getData(page, timestamp);
    }
}
