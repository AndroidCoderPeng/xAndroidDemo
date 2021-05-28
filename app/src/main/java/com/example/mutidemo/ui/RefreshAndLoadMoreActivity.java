package com.example.mutidemo.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.example.mutidemo.R;
import com.example.mutidemo.adapter.NewsAdapter;
import com.example.mutidemo.bean.NewsBean;
import com.example.mutidemo.mvp.presenter.NewsPresenterImpl;
import com.example.mutidemo.mvp.view.INewsView;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.widget.EasyToast;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.ResponseBody;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/21 19:16
 */
public class RefreshAndLoadMoreActivity extends BaseNormalActivity implements INewsView {

    @BindView(R.id.newsRecyclerView)
    RecyclerView newsRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    /**
     * 设置一个集合，用来存储网络请求到的数据
     */
    private List<NewsBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> dataBeans = new ArrayList<>();
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
    private QMUITipDialog loadingDialog;
    private static WeakReferenceHandler weakReferenceHandler;

    @Override
    public int initLayoutView() {
        return R.layout.activity_refresh;
    }

    @Override
    public void initData() {
        loadingDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载数据中，请稍后...")
                .create();
        weakReferenceHandler = new WeakReferenceHandler(this);
        newsPresenter = new NewsPresenterImpl(this);
        newsPresenter.onReadyRetrofitRequest(defaultPage, System.currentTimeMillis());
    }

    @Override
    public void initEvent() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isRefresh = true;
                //刷新之后页码重置
                defaultPage = 1;
                newsPresenter.onReadyRetrofitRequest(defaultPage, System.currentTimeMillis());
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isLoadMore = true;
                defaultPage++;
                newsPresenter.onReadyRetrofitRequest(defaultPage, System.currentTimeMillis());
            }
        });
    }

    private static class WeakReferenceHandler extends Handler {
        private WeakReference<RefreshAndLoadMoreActivity> reference;

        private WeakReferenceHandler(RefreshAndLoadMoreActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RefreshAndLoadMoreActivity activity = reference.get();
            switch (msg.what) {
                case 10000:
                    if (activity.isRefresh || activity.isLoadMore) {
                        activity.newsAdapter.notifyDataSetChanged();
                    } else {
                        //首次加载数据
                        activity.newsAdapter = new NewsAdapter(activity, activity.dataBeans);
                        activity.newsRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
                        activity.newsRecyclerView.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
                        activity.newsRecyclerView.setAdapter(activity.newsAdapter);
                        activity.newsAdapter.setOnNewsItemClickListener(new NewsAdapter.OnNewsItemClickListener() {
                            @Override
                            public void onClick(int position) {
                                NewsBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean bean = activity.dataBeans.get(position);
                                Intent intent = new Intent(activity, NewsDetailsActivity.class);
                                intent.putExtra("title", bean.getTitle());
                                intent.putExtra("src", bean.getSource());
                                intent.putExtra("time", bean.getPubDate());
                                intent.putExtra("content", bean.getHtml());
                                activity.startActivity(intent);
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
    }

    @Override
    public void showProgress() {
        loadingDialog.show();
    }

    @Override
    public void hideProgress() {
        loadingDialog.dismiss();
    }

    @Override
    public void showNetWorkData(ResponseBody response) {
        if (response != null) {
            try {
                String json = response.string();
                NewsBean newsBean = JSONObject.parseObject(json, NewsBean.class);
                List<NewsBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> listBeans = newsBean.getShowapi_res_body().getPagebean().getContentlist();
                if (isRefresh) {
                    dataBeans.clear();//下拉刷新必须先清空之前的List，不然会出现数据重复的问题
                    dataBeans = listBeans;
                    refreshLayout.finishRefresh();
                    isRefresh = false;
                } else if (isLoadMore) {
                    dataBeans.addAll(listBeans);
                    refreshLayout.finishLoadMore();
                    isLoadMore = false;
                } else {
                    dataBeans = newsBean.getShowapi_res_body().getPagebean().getContentlist();
                }
                weakReferenceHandler.sendEmptyMessage(10000);
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