package com.example.mutidemo.mvp.model;

import android.util.Log;

import com.example.mutidemo.mvp.retrofit.RetrofitServiceManager;
import com.example.mutidemo.util.Constant;

import okhttp3.ResponseBody;
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
        void onSuccess(ResponseBody response);

        void onFailure(Throwable throwable);
    }

    @Override
    public Subscription sendRetrofitRequest(int page, long timestamp) {
        Observable<ResponseBody> observable = RetrofitServiceManager.getNewsData(Constant.BASE_NEWS_URL, page, timestamp);
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onError(Throwable e) {
                        if (newsListener != null) {
                            newsListener.onFailure(e);
                            Log.e(TAG, "onError: ", e);
                        }
                    }

                    @Override
                    public void onNext(ResponseBody response) {
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
