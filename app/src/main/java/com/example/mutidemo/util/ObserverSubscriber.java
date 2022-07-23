package com.example.mutidemo.util;

import com.example.mutidemo.util.callback.OnObserverCallback;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ObserverSubscriber {
    public static void addSubscribe(Observable<ResponseBody> observable, OnObserverCallback observerCallback) {
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseBody>() {
            @Override
            public void onCompleted() {
                observerCallback.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                observerCallback.onError(e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                observerCallback.onNext(responseBody);
            }
        });
    }
}
