package com.example.mutidemo.ui;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.GridViewAdapter;
import com.example.mutidemo.bean.WeatherBean;
import com.example.mutidemo.mvp.presenter.WeatherPresenterImpl;
import com.example.mutidemo.mvp.view.IWeatherView;
import com.example.mutidemo.util.OtherUtil;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;

public class MVPActivity extends BaseNormalActivity implements IWeatherView {

    private WeatherPresenterImpl weatherPresenter;
    private ProgressDialog progressDialog;

    @BindView(R.id.mMVP_GridView)
    GridView mMVP_GridView;

    @Override
    public void initView() {
        setContentView(R.layout.activity_mvp);
    }

    @Override
    public void init() {
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
            progressDialog.setMessage("正在加载数据...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
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

            //TODO 将一周内的天气情况作为画廊形式展示
            List<WeatherBean.ResultBeanX.ResultBean.DailyBean> dailyBeanList = weatherBean.getResult().getResult().getDaily();

            //TODO 绑定GridView
            List<WeatherBean.ResultBeanX.ResultBean.IndexBean> indexBeanList = weatherBean.getResult().getResult().getIndex();
            GridViewAdapter mGridViewAdapter = new GridViewAdapter(this, indexBeanList);
            mMVP_GridView.setAdapter(mGridViewAdapter);
            OtherUtil.measureViewHeight(this, mMVP_GridView);//计算GridView的实际高度
            mMVP_GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String detail = indexBeanList.get(position).getDetail();
                    ToastUtil.showBeautifulToast(detail, 3);
                }
            });
        } else {
            ToastUtil.showBeautifulToast("获取数据失败，请重试", 5);
        }
    }
}
