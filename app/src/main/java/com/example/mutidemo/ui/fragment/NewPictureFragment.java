package com.example.mutidemo.ui.fragment;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.PictureAdapter;
import com.example.mutidemo.base.BaseFragment;
import com.example.mutidemo.bean.ResultBean;
import com.example.mutidemo.ui.PictureViewActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/25 19:50
 */
public class NewPictureFragment extends BaseFragment {

    private static final String TAG = "NewPictureFragment";

    @BindView(R.id.newPictureView)
    RecyclerView newPictureView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    /**
     * 设置一个集合，用来存储网络请求到的数据
     */
    private List<ResultBean.CategoryBean.ListBean> listBeans = new ArrayList<>();
    /**
     * 自定义刷新和加载的标识，默认为false
     */
    private boolean isRefresh, isLoadMore = false;
    /**
     * 起始页
     */
    private int defaultPage = 0;
    private ResultBean.CategoryBean categoryBean;
    private String categoryUrl;

    public void setData(ResultBean.CategoryBean data, String url) {
        this.categoryBean = data;
        this.categoryUrl = url;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_picture;
    }

    @Override
    protected void initData() {
        PictureAdapter adapter = new PictureAdapter(getActivity(), categoryBean.getList());
        newPictureView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        newPictureView.setAdapter(adapter);
        adapter.setOnNewsItemClickListener(new PictureAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                String childUrl = categoryBean.getList().get(position).getChildUrl();
                Intent intent = new Intent(getActivity(), PictureViewActivity.class);
                intent.putExtra("childUrl", childUrl);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void loadData() {

    }
}
