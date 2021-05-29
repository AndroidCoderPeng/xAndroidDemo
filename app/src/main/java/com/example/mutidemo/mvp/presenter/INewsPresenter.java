package com.example.mutidemo.mvp.presenter;

public interface INewsPresenter {
    void onReadyRetrofitRequest(String channel, int start);

    void disposeRetrofitRequest();
}
