package com.example.mutidemo.mvp.presenter;

import com.example.mutidemo.bean.NewsListBean;
import com.example.mutidemo.mvp.BasePresenter;
import com.example.mutidemo.mvp.model.NewsListModelImpl;
import com.example.mutidemo.mvp.view.INewsListView;

public class NewsListPresenterImpl extends BasePresenter implements INewsListPresenter, NewsListModelImpl.OnNewsListener {

    private final INewsListView view;
    private final NewsListModelImpl actionModel;

    public NewsListPresenterImpl(INewsListView newsView) {
        this.view = newsView;
        actionModel = new NewsListModelImpl(this);
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
    public void onSuccess(NewsListBean resultBean) {
        view.hideProgress();
        view.showNetWorkData(resultBean);
    }

    @Override
    public void onFailure(Throwable throwable) {
        view.hideProgress();
    }
}
