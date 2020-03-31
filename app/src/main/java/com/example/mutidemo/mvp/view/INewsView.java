package com.example.mutidemo.mvp.view;

import okhttp3.ResponseBody;

public interface INewsView {

    void showProgress();

    void hideProgress();

    /**
     * 显示从后台服务器获取到的数据
     */
    void showNetWorkData(ResponseBody response);
}
