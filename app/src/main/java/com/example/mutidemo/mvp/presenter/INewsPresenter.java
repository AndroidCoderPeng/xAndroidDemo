package com.example.mutidemo.mvp.presenter;

public interface INewsPresenter {
    void onReadyRetrofitRequest(int page, long timestamp);

    void disposeRetrofitRequest();
}
