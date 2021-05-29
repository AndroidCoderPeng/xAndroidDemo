package com.example.mutidemo.mvp.model;

import android.util.Log;

import com.example.mutidemo.bean.NewsBean;
import com.example.mutidemo.util.retrofit.RetrofitServiceManager;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class NewsModelImpl implements INewsModel {

    private static final String TAG = "NewsModelImpl";
    private OnNewsListener newsListener;

    public NewsModelImpl(OnNewsListener listener) {
        this.newsListener = listener;
    }

    public interface OnNewsListener {
        void onSuccess(NewsBean response);

        void onFailure(Throwable throwable);
    }

    @Override
    public Subscription sendRetrofitRequest(String channel, int start) {
        Observable<NewsBean> observable = RetrofitServiceManager.obtainNewsData(channel, start);
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsBean>() {
                    @Override
                    public void onError(Throwable e) {
                        if (newsListener != null) {
                            newsListener.onFailure(e);
                        }
                    }

                    @Override
                    public void onNext(NewsBean response) {
                        if (newsListener != null) {
                            newsListener.onSuccess(response);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted ===============> 数据请求完毕");
                    }
                });
    }
}
