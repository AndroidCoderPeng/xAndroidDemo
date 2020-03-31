package com.example.mutidemo.mvp.model;

import rx.Subscription;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/3/30 15:11
 */
public interface INewsModel {
    Subscription sendRetrofitRequest(int page, long timestamp);
}
