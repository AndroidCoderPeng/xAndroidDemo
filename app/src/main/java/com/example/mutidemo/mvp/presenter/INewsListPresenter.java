package com.example.mutidemo.mvp.presenter;

public interface INewsListPresenter {
    void onReadyRetrofitRequest(String channel, int start);

    void disposeRetrofitRequest();
}
