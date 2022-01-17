package com.example.mutidemo.mvp.presenter;

import com.example.mutidemo.bean.NewsBean;
import com.example.mutidemo.mvp.BasePresenter;
import com.example.mutidemo.mvp.model.NewsModelImpl;
import com.example.mutidemo.mvp.view.INewsView;

public class NewsPresenterImpl extends BasePresenter implements INewsPresenter, NewsModelImpl.OnNewsListener {

    private final INewsView view;
    private final NewsModelImpl actionModel;

    public NewsPresenterImpl(INewsView newsView) {
        this.view = newsView;
        actionModel = new NewsModelImpl(this);
    }

    /**
     * 唤醒订阅
     */
    @Override
    public void onReadyRetrofitRequest(String channel, int start) {
        view.showProgress();
        addSubscription(actionModel.sendRetrofitRequest(channel, start));
    }

    /**
     * 取消订阅
     */
    @Override
    public void disposeRetrofitRequest() {
        unSubscription();
    }

    @Override
    public void onSuccess(NewsBean resultBean) {
        view.hideProgress();
        view.showNetWorkData(resultBean);
    }

    @Override
    public void onFailure(Throwable throwable) {
        view.hideProgress();
    }
}
