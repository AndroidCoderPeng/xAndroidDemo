package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.example.mutidemo.R;
import com.example.mutidemo.adapter.NewsAdapter;
import com.example.mutidemo.bean.NewsBean;
import com.example.mutidemo.mvp.presenter.NewsPresenterImpl;
import com.example.mutidemo.mvp.view.INewsView;
import com.example.mutidemo.util.OtherUtils;
import com.example.mutidemo.util.TimeUtil;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.widget.EasyToast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import okhttp3.ResponseBody;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/21 19:16
 */
public class RefreshAndLoadMoreActivity extends BaseNormalActivity implements INewsView {

    private static final String TAG = "RefreshAndLoadMore";
    private Context mContext = RefreshAndLoadMoreActivity.this;
    @BindView(R.id.newsRecyclerView)
    RecyclerView newsRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    /**
     * 设置一个集合，用来存储网络请求到的数据
     */
    private List<NewsBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> datas = new ArrayList<>();
    /**
     * 自定义刷新和加载的标识，默认为false
     */
    private boolean isRefresh, isLoadMore = false;
    /**
     * 起始页
     */
    private int defaultPage = 1;
    private NewsAdapter newsAdapter;
    private NewsPresenterImpl newsPresenter;

    @Override
    public int initLayoutView() {
        return R.layout.activity_refresh;
    }

    @Override
    public void initData() {
        newsPresenter = new NewsPresenterImpl(this);
        newsPresenter.onReadyRetrofitRequest(defaultPage, TimeUtil.transformTime());
    }

    @Override
    public void initEvent() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isRefresh = true;
                //刷新之后页码重置
                defaultPage = 1;
                newsPresenter.onReadyRetrofitRequest(defaultPage, TimeUtil.transformTime());
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isLoadMore = true;
                defaultPage++;
                newsPresenter.onReadyRetrofitRequest(defaultPage, TimeUtil.transformTime());
            }
        });
    }

    /**
     * 使用handler请求网络数据并在handleMessage里面处理返回操作
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10000:
                    if (isRefresh || isLoadMore) {
                        newsAdapter.notifyDataSetChanged();
                    } else {
                        //首次加载数据
                        newsAdapter = new NewsAdapter(mContext, datas);
                        newsRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                        newsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                        newsRecyclerView.setAdapter(newsAdapter);
                        newsAdapter.setOnNewsItemClickListener(new NewsAdapter.OnNewsItemClickListener() {
                            @Override
                            public void onClick(int position) {
                                NewsBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean bean = datas.get(position);
                                Intent intent = new Intent(mContext, NewsDetailsActivity.class);
                                intent.putExtra("title", bean.getTitle());
                                intent.putExtra("src", bean.getSource());
                                intent.putExtra("time", bean.getPubDate());
                                intent.putExtra("content", bean.getHtml());
                                startActivity(intent);
                            }
                        });
                    }
                    break;
                case 10001:
                    EasyToast.showToast("获取数据失败", EasyToast.ERROR);
                    break;
                case 10002:
                    EasyToast.showToast("刷新数据失败", EasyToast.ERROR);
                    break;
                default:
                    break;
            }
        }
    };

    private boolean isFirstLoading = true;

    @Override
    public void showProgress() {
        if (isFirstLoading) {
            OtherUtils.showProgressDialog(this, "数据加载中...");
        }
    }

    @Override
    public void hideProgress() {
        OtherUtils.hideProgressDialog();
    }

    @Override
    public void showNetWorkData(ResponseBody response) {
        if (response != null) {
            try {
                String json = response.string();
                NewsBean newsBean = JSONObject.parseObject(json, NewsBean.class);
                List<NewsBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> listBeans = newsBean.getShowapi_res_body().getPagebean().getContentlist();
                if (isRefresh) {
                    datas.clear();//下拉刷新必须先清空之前的List，不然会出现数据重复的问题
                    for (int i = 0; i < listBeans.size(); i++) {
                        datas.add(0, listBeans.get(i));
                    }
                    refreshLayout.finishRefresh();
                    isRefresh = false;
                } else if (isLoadMore) {
                    datas.addAll(listBeans);
                    refreshLayout.finishLoadMore();
                    isLoadMore = false;
                } else {
                    Log.d(TAG, "onSuccess: 首次加载数据");
                    datas = newsBean.getShowapi_res_body().getPagebean().getContentlist();
                }
                isFirstLoading = false;
                //更新RecyclerView
                handler.sendEmptyMessage(10000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (newsPresenter != null) {
            newsPresenter.disposeRetrofitRequest();
        }
    }
}