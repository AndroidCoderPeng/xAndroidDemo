package com.example.mutidemo.mvp.model;

import android.util.Log;

import com.example.mutidemo.bean.NewsListBean;
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


public class NewsListModelImpl implements INewsListModel {

    private static final String TAG = "NewsModelImpl";
    private final Gson gson = new Gson();
    private final OnNewsListener newsListener;

    public NewsListModelImpl(OnNewsListener listener) {
        this.newsListener = listener;
    }

    public interface OnNewsListener {
        void onSuccess(NewsListBean dataRows);

        void onFailure(Throwable throwable);
    }

    @Override
    public Subscription sendRetrofitRequest(String channel, int start) {
        Observable<ResponseBody> observable = RetrofitServiceManager.obtainNewsList(channel, start);
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onError(Throwable e) {
                        if (newsListener != null) {
                            newsListener.onFailure(e);
                        }
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String response = responseBody.string();
                            int responseCode = StringHelper.separateResponseCode(response);
                            if (responseCode == 10000) {
                                NewsListBean dataRows = gson.fromJson(response, new TypeToken<NewsListBean>() {
                                }.getType());
                                newsListener.onSuccess(dataRows);
                            } else {
                                newsListener.onFailure(new Exception("responseCode = " + responseCode));
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
