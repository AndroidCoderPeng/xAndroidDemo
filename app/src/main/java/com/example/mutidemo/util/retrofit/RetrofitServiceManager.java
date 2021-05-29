package com.example.mutidemo.util.retrofit;

import android.util.Log;

import com.example.mutidemo.bean.NewsBean;
import com.example.mutidemo.bean.WeatherBean;
import com.example.mutidemo.util.Constant;

import org.jetbrains.annotations.NotNull;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class RetrofitServiceManager {

    private static final String TAG = "RetrofitServiceManager";

    private static Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())//Gson转换器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(createHttpClient())//log拦截器
                .build();
    }

    private static OkHttpClient createHttpClient() {
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

    public static Observable<WeatherBean> obtainWeatherData(String city, int cityId, int cityCode) {
        Retrofit retrofit = createRetrofit();
        RetrofitService service = retrofit.create(RetrofitService.class);
        return service.getWeather(Constant.APP_KEY, city, cityId, cityCode);
    }

    public static Observable<NewsBean> obtainNewsData(String channel, int start) {
        Retrofit retrofit = createRetrofit();
        RetrofitService service = retrofit.create(RetrofitService.class);
        return service.getNews(Constant.APP_KEY, channel, 15, start);
    }
}
