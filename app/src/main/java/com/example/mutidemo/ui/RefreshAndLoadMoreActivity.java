package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.example.mutidemo.R;
import com.example.mutidemo.adapter.NewsAdapter;
import com.example.mutidemo.bean.NewsBean;
import com.example.mutidemo.util.HttpHelper;
import com.example.mutidemo.util.OtherUtils;
import com.example.mutidemo.util.callback.HttpCallBackListener;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.widget.EasyToast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Response;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/21 19:16
 */
public class RefreshAndLoadMoreActivity extends BaseNormalActivity {

    private static final String TAG = "RefreshAndLoadMore";
    private Context mContext = RefreshAndLoadMoreActivity.this;
    @BindView(R.id.newsRecyclerView)
    RecyclerView newsRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    /**
     * 设置一个集合，用来存储网络请求到的数据
     */
    private List<NewsBean.ResultBeanX.ResultBean.ListBean> datas = new ArrayList<>();
    /**
     * 自定义刷新和加载的标识，默认为false
     */
    private boolean isRefresh, isLoadMore = false;
    /**
     * 起始页
     */
    private int defaultPage = 0;
    private NewsAdapter newsAdapter;

    @Override
    public void initView() {
        setContentView(R.layout.activity_refresh);
    }

    @Override
    public void initData() {
        OtherUtils.showProgressDialog(this, "数据加载中...");
        //第一次加载数据
        HttpHelper.doHttpRequest(defaultPage, new HttpCallBackListener() {
            @Override
            public void onSuccess(Response response) throws IOException {
                Log.d(TAG, "onSuccess: 首次加载数据");
                JsonToBean(response.body().string());
            }

            @Override
            public void onFailure(Throwable throwable) {
                handler.sendEmptyMessageDelayed(10001, 3000);
            }
        });
    }

    @Override
    public void initEvent() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isRefresh = true;
                //刷新之后页码重置
                defaultPage = 0;
                HttpHelper.doHttpRequest(0, new HttpCallBackListener() {
                    @Override
                    public void onSuccess(Response response) throws IOException {
                        Log.d(TAG, "onRefresh: 下拉刷新");
                        JsonToBean(response.body().string());
                        refreshLayout.finishRefresh();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        refreshLayout.finishRefresh(3000);
                        handler.sendEmptyMessageDelayed(10002, 3000);
                    }
                });
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isLoadMore = true;
                defaultPage++;
                HttpHelper.doHttpRequest(defaultPage, new HttpCallBackListener() {
                    @Override
                    public void onSuccess(Response response) throws IOException {
                        Log.d(TAG, "onLoadMore: 上拉加载");
                        JsonToBean(response.body().string());
                        refreshLayout.finishLoadMore();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        refreshLayout.finishLoadMore(3000);
                        handler.sendEmptyMessageDelayed(10002, 3000);
                    }
                });
            }
        });
    }

    private void JsonToBean(String result) {
        Log.d(TAG, "JsonToBean: " + result);
        NewsBean newsBean = JSONObject.parseObject(result, NewsBean.class);
        List<NewsBean.ResultBeanX.ResultBean.ListBean> listBeans = newsBean.getResult().getResult().getList();
        if (!newsBean.getCode().equals("10000")) {
            EasyToast.showToast("获取数据失败，请稍后重试", EasyToast.ERROR);
        } else {
            if (isRefresh) {
                datas.clear();//下拉刷新必须先清空之前的List，不然会出现数据重复的问题
                for (int i = 0; i < listBeans.size(); i++) {
                    datas.add(0, listBeans.get(i));
                }
                isRefresh = false;
            } else if (isLoadMore) {
                datas.addAll(listBeans);
                isLoadMore = false;
            } else {
                datas = newsBean.getResult().getResult().getList();
            }
            //更新List
            handler.sendEmptyMessage(Integer.parseInt(newsBean.getCode()));
        }
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
                                NewsBean.ResultBeanX.ResultBean.ListBean bean = datas.get(position);
                                Intent intent = new Intent(mContext, NewsDetailsActivity.class);
                                intent.putExtra("title", bean.getTitle());
                                intent.putExtra("src", bean.getSrc());
                                intent.putExtra("time", bean.getTime());
                                intent.putExtra("content", bean.getContent());
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
            OtherUtils.hideProgressDialog();
        }
    };
}