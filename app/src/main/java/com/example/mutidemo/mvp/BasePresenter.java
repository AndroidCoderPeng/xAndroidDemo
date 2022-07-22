package com.example.mutidemo.mvp;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class BasePresenter {
    private CompositeSubscription mCompositeSubscription;

    //Rxjava注册
    protected void addSubscription(Subscription subscriber) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscriber);
    }

    //Rxjava取消注册，以避免内存泄露
    protected void unSubscription() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
    }
}