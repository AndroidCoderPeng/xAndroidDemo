package com.example.mutidemo.mvvm.vm;

import androidx.lifecycle.MutableLiveData;

import com.example.mutidemo.mvvm.BaseViewModel;
import com.example.mutidemo.mvvm.LoadState;
import com.example.mutidemo.mvvm.model.WeatherModel;
import com.example.mutidemo.util.StringHelper;
import com.example.mutidemo.util.retrofit.RetrofitServiceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class WeatherDataViewModel extends BaseViewModel {
    private final Gson gson = new Gson();
    public MutableLiveData<WeatherModel> resultModel = new MutableLiveData<>();

    public void obtainWeatherData(String city, int cityId, int cityCode) {
        loadState.setValue(LoadState.Loading);
        Observable<ResponseBody> weatherDataObservable = RetrofitServiceManager.obtainWeatherDetail(city, cityId, cityCode);
        weatherDataObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseBody>() {
            @Override
            public void onCompleted() {
                loadState.setValue(LoadState.Success);
            }

            @Override
            public void onError(Throwable e) {
                loadState.setValue(LoadState.Fail);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String response = responseBody.string();
                    int responseCode = StringHelper.separateResponseCode(response);
                    if (responseCode == 10000) {
                        WeatherModel weatherModel = gson.fromJson(response, new TypeToken<WeatherModel>() {
                        }.getType());
                        resultModel.setValue(weatherModel);
                    } else {
                        loadState.setValue(LoadState.Fail);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}