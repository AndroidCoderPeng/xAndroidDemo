package com.example.mutidemo.mvp.presenter;

import com.example.mutidemo.mvp.BasePresenter;
import com.example.mutidemo.mvp.model.NewsModelImpl;
import com.example.mutidemo.mvp.view.INewsView;

import okhttp3.ResponseBody;

public class NewsPresenterImpl extends BasePresenter implements INewsPresenter, NewsModelImpl.OnNewsListener {

    private INewsView iNewsView;
    private NewsModelImpl newsModel;

    public NewsPresenterImpl(INewsView view) {
        this.iNewsView = view;
        newsModel = new NewsModelImpl(this);
    }

    /**
     * 唤醒订阅
     */
    @Override
    public void onReadyRetrofitRequest(int page, long timestamp) {
        iNewsView.showProgress();
        addSubscription(newsModel.sendRetrofitRequest(page, timestamp));
    }

    /**
     * 取消订阅
     */
    @Override
    public void disposeRetrofitRequest() {
        unSubscription();
    }

    @Override
    public void onSuccess(ResponseBody response) {
        iNewsView.hideProgress();
        /**
         * 将返回的数据传递给View并显示在Activity/Fragment上面
         */
        iNewsView.showNetWorkData(response);
    }

    @Override
    public void onFailure(Throwable throwable) {
        iNewsView.hideProgress();
    }
}
