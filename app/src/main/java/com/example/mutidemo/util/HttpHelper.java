package com.example.mutidemo.util;

import android.util.Log;

import com.example.mutidemo.util.callback.HtmlParserCallBackListener;
import com.example.mutidemo.util.callback.HttpCallBackListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/21 20:25
 */
public class HttpHelper {
    private static final String TAG = "HttpHelper";

    private static String replacePageNumber(int page) {
        Log.d(TAG, "replacePageNumber: 第" + page + "页");
        return Constant.NEWS_URL.replace("pageNum", String.valueOf(page));
    }

    public static void doHttpRequest(final int pageNumber, final HttpCallBackListener listener) {
        Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> subscriber) {
                Call call = new OkHttpClient().newCall(new Request.Builder().url(replacePageNumber(pageNumber)).get().build());
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        subscriber.onNext(response);
                    }
                });
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted: 加载数据完毕");
            }

            @Override
            public void onError(Throwable e) {
                listener.onFailure(e);
            }

            @Override
            public void onNext(Response response) {
                try {
                    listener.onSuccess(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void captureHtmlData(final String url, final HtmlParserCallBackListener listener) {
        Log.d(TAG, "数据抓取地址: " + url);
        Observable.create(new Observable.OnSubscribe<Document>() {
            @Override
            public void call(Subscriber<? super Document> subscriber) {
                try {
                    subscriber.onNext(Jsoup.connect(url).timeout(30 * 1000).get());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Document>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Document document) {
                try {
                    listener.onParserDone(document);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
