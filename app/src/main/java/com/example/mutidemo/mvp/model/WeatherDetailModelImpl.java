package com.example.mutidemo.mvp.model;

import android.util.Log;

import com.example.mutidemo.bean.WeatherDetailBean;
import com.example.mutidemo.util.StringHelper;
import com.example.mutidemo.util.retrofit.RetrofitServiceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class WeatherDetailModelImpl implements IWeatherDetailModel {

    private static final String TAG = "WeatherModelImpl";
    private final Gson gson = new Gson();
    private final OnWeatherListener weatherListener;

    public WeatherDetailModelImpl(OnWeatherListener listener) {
        this.weatherListener = listener;
    }

    /**
     * 数据回调接口
     */
    public interface OnWeatherListener {
        void onSuccess(WeatherDetailBean weatherBean);

        void onFailure(Throwable throwable);
    }

    @Override
    public Subscription sendRetrofitRequest(String city, int cityid, int citycode) {
        Observable<ResponseBody> observable = RetrofitServiceManager.obtainWeatherDetail(city, cityid, citycode);
        return observable
                .subscribeOn(Schedulers.io())//在io线程获取数据
                .observeOn(AndroidSchedulers.mainThread())//回调给主线程，异步;
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onError(Throwable e) {
                        if (weatherListener != null) {
                            weatherListener.onFailure(e);
                        }
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String response = responseBody.string();
                            int responseCode = StringHelper.separateResponseCode(response);
                            if (responseCode == 10000) {
                                WeatherDetailBean weatherBean = gson.fromJson(response, new TypeToken<WeatherDetailBean>() {
                                }.getType());
                                weatherListener.onSuccess(weatherBean);
                            } else {
                                weatherListener.onFailure(new Exception("responseCode = " + responseCode));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted ===============> 数据请求完毕");
                    }
                });
    }
}
