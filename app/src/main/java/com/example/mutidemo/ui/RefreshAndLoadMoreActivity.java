package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.NewsAdapter;
import com.example.mutidemo.bean.NewsBean;
import com.example.mutidemo.mvp.presenter.NewsPresenterImpl;
import com.example.mutidemo.mvp.view.INewsView;
import com.example.mutidemo.util.OtherUtils;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

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
    private List<NewsBean.ResultBeanX.ResultBean.ListBean> dataBeans = new ArrayList<>();
    /**
     * 自定义刷新和加载的标识，默认为false
     */
    private boolean isRefresh, isLoadMore = false;
    /**
     * 起始页
     */
    private int defaultPage = 0;
    private NewsAdapter newsAdapter;
    private NewsPresenterImpl newsPresenter;
    private static WeakReferenceHandler weakReferenceHandler;

    @Override
    public int initLayoutView() {
        return R.layout.activity_refresh;
    }

    @Override
    public void initData() {
        weakReferenceHandler = new WeakReferenceHandler(this);
        newsPresenter = new NewsPresenterImpl(this);
        newsPresenter.onReadyRetrofitRequest("娱乐", defaultPage);
    }

    @Override
    public void initEvent() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isRefresh = true;
                //刷新之后页码重置
                defaultPage = 0;
                newsPresenter.onReadyRetrofitRequest("娱乐", defaultPage);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isLoadMore = true;
                defaultPage++;
                newsPresenter.onReadyRetrofitRequest("娱乐", defaultPage);
            }
        });
    }

    private static class WeakReferenceHandler extends Handler {
        private final WeakReference<RefreshAndLoadMoreActivity> reference;

        private WeakReferenceHandler(RefreshAndLoadMoreActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message msg) {
            RefreshAndLoadMoreActivity activity = reference.get();
            if (msg.what == 10000) {
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
                            NewsBean.ResultBeanX.ResultBean.ListBean bean = activity.dataBeans.get(position);
                            Intent intent = new Intent(activity, NewsDetailsActivity.class);
                            intent.putExtra("title", bean.getTitle());
                            intent.putExtra("src", bean.getSrc());
                            intent.putExtra("time", bean.getTime());
                            intent.putExtra("content", bean.getContent());
                            activity.startActivity(intent);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void showProgress() {
        OtherUtils.showLoadingDialog(this, "加载数据中，请稍后...");
    }

    @Override
    public void hideProgress() {
        OtherUtils.dismissLoadingDialog();
    }

    @Override
    public void showNetWorkData(NewsBean response) {
        if (response.getCode().equals("10000")) {
            List<NewsBean.ResultBeanX.ResultBean.ListBean> listBeans = response.getResult().getResult().getList();
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
                dataBeans = listBeans;
            }
            weakReferenceHandler.sendEmptyMessage(10000);
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