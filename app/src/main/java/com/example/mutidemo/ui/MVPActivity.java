package com.example.mutidemo.ui;

import android.app.ProgressDialog;

import com.example.mutidemo.bean.WeatherBean;
import com.example.mutidemo.mvp.presenter.WeatherPresenterImpl;
import com.example.mutidemo.mvp.view.IWeatherView;
import com.google.gson.Gson;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.widget.EasyToast;

import java.util.List;

public class MVPActivity extends BaseNormalActivity implements IWeatherView {

    private WeatherPresenterImpl weatherPresenter;
    private ProgressDialog progressDialog;

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        weatherPresenter = new WeatherPresenterImpl(this);
    }

    @Override
    public void initEvent() {
        weatherPresenter.onReadyRetrofitRequest("北京", 1, 101010100);
    }

    @Override
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载......");
            progressDialog.setCanceledOnTouchOutside(false);
        }
    }

    @Override
    public void hideProgress() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showNetWorkData(WeatherBean weatherBean) {
        if (weatherBean != null) {
            List<WeatherBean.ResultBeanX.ResultBean.DailyBean> daily = weatherBean.getResult().getResult().getDaily();
            Gson gson = new Gson();
            String json = gson.toJson(daily);
            EasyToast.showToast(json, EasyToast.SUCCESS);
        }
    }
}