package com.example.mutidemo.util;

import android.util.Log;

import com.example.mutidemo.util.callback.HtmlParserCallBackListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

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
