package com.example.mutidemo.util.callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/21 19:40
 */
public interface HttpCallBackListener {
    void onSuccess(Response response) throws IOException;

    void onFailure(Throwable throwable);
}
