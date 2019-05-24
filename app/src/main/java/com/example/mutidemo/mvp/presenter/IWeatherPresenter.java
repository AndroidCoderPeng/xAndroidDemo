package com.example.mutidemo.mvp.presenter;

public interface IWeatherPresenter {
    /**
     * 将用户界面收集到的信息传递给Model，发起网络请求，此时并未真正发起请求，只是传递请求的数据
     * <p>
     * https://way.jd.com/jisuapi/weather?city=北京&cityid=1&citycode=101010100&appkey=e957ed7ad90436a57e604127d9d8fa32
     */
    void onReadyRetrofitRequest(String city, int cityid, int citycode);
}
