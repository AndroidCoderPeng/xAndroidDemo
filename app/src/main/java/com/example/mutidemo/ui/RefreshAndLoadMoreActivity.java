package com.example.mutidemo.ui;

import android.content.Intent;
import android.os.Handler;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mutidemo.adapter.NewsAdapter;
import com.example.mutidemo.bean.NewsListBean;
import com.example.mutidemo.databinding.ActivityRefreshBinding;
import com.example.mutidemo.mvp.presenter.NewsListPresenterImpl;
import com.example.mutidemo.mvp.view.INewsListView;
import com.example.mutidemo.util.OtherUtils;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.utils.WeakReferenceHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/21 19:16
 */
public class RefreshAndLoadMoreActivity extends AndroidxBaseActivity<ActivityRefreshBinding> implements INewsListView {

    /**
     * 设置一个集合，用来存储网络请求到的数据
     */
    private List<NewsListBean.ResultBeanX.ResultBean.ListBean> dataBeans = new ArrayList<>();
    /**
     * 自定义刷新和加载的标识，默认为false
     */
    private boolean isRefresh, isLoadMore = false;
    /**
     * 起始页
     */
    private int defaultPage = 0;
    private NewsAdapter newsAdapter;
    private NewsListPresenterImpl newsPresenter;
    private static WeakReferenceHandler weakReferenceHandler;

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    public void initData() {
        weakReferenceHandler = new WeakReferenceHandler(callback);
        newsPresenter = new NewsListPresenterImpl(this);
        newsPresenter.onReadyRetrofitRequest("娱乐", defaultPage);
    }

    @Override
    public void initEvent() {
        viewBinding.refreshLayout.setOnRefreshListener(refreshLayout -> {
            isRefresh = true;
            //刷新之后页码重置
            defaultPage = 0;
            newsPresenter.onReadyRetrofitRequest("娱乐", defaultPage);
        });
        viewBinding.refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            isLoadMore = true;
            defaultPage++;
            newsPresenter.onReadyRetrofitRequest("娱乐", defaultPage);
        });
    }

    private final Handler.Callback callback = msg -> {
        if (msg.what == 10000) {
            if (isRefresh || isLoadMore) {
                newsAdapter.notifyDataSetChanged();
            } else {
                //首次加载数据
                newsAdapter = new NewsAdapter(this, dataBeans);
                viewBinding.newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                viewBinding.newsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
                viewBinding.newsRecyclerView.setAdapter(newsAdapter);
                newsAdapter.setOnNewsItemClickListener(position -> {
                    NewsListBean.ResultBeanX.ResultBean.ListBean bean = dataBeans.get(position);
                    Intent intent = new Intent(this, NewsDetailsActivity.class);
                    intent.putExtra("title", bean.getTitle());
                    intent.putExtra("src", bean.getSrc());
                    intent.putExtra("time", bean.getTime());
                    intent.putExtra("content", bean.getContent());
                    startActivity(intent);
                });
            }
        }
        return true;
    };

    @Override
    public void showProgress() {
        if (!isRefresh && !isLoadMore) {
            OtherUtils.showLoadingDialog(this, "加载数据中，请稍后...");
        }
    }

    @Override
    public void hideProgress() {
        OtherUtils.dismissLoadingDialog();
    }

    @Override
    public void showNetWorkData(NewsListBean response) {
        if (response.getCode().equals("10000")) {
            List<NewsListBean.ResultBeanX.ResultBean.ListBean> listBeans = response.getResult().getResult().getList();
            if (isRefresh) {
                dataBeans.clear();//下拉刷新必须先清空之前的List，不然会出现数据重复的问题
                dataBeans = listBeans;
                viewBinding.refreshLayout.finishRefresh();
                isRefresh = false;
            } else if (isLoadMore) {
                dataBeans.addAll(listBeans);
                viewBinding.refreshLayout.finishLoadMore();
                isLoadMore = false;
            } else {
                dataBeans = listBeans;
            }
            weakReferenceHandler.sendEmptyMessage(10000);
        }
    }

    @Override
    public void onDestroy() {
        if (newsPresenter != null) {
            newsPresenter.disposeRetrofitRequest();
        }
        super.onDestroy();
    }
}