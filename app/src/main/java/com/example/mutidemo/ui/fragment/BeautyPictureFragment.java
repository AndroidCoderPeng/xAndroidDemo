package com.example.mutidemo.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.example.mutidemo.R;
import com.example.mutidemo.adapter.PictureAdapter;
import com.example.mutidemo.base.BaseFragment;
import com.example.mutidemo.bean.ResultBean;
import com.example.mutidemo.ui.PictureViewActivity;
import com.example.mutidemo.util.HtmlParserHelper;
import com.example.mutidemo.util.HttpHelper;
import com.example.mutidemo.util.OtherUtils;
import com.example.mutidemo.util.callback.HtmlParserCallBackListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/25 19:50
 */
public class BeautyPictureFragment extends BaseFragment {

    private static final String TAG = "BeautyPictureFragment";

    @BindView(R.id.newPictureView)
    RecyclerView newPictureView;
    @BindView(R.id.newPictureRefresh)
    SmartRefreshLayout newPictureRefresh;

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
    private int defaultPage = 1;
    private ResultBean.CategoryBean categoryBean;
    private String categoryUrl;//用来实现刷新的地址
    private PictureAdapter pictureAdapter;

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
        //首次加载数据
        handler.sendEmptyMessage(10005);
    }

    @Override
    protected void loadData() {
        newPictureRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isRefresh = true;
                //刷新之后页码重置
                defaultPage = 1;
                HttpHelper.captureHtmlData(categoryUrl, new HtmlParserCallBackListener() {
                    @Override
                    public void onParserDone(Document document) throws IOException {
                        Message message = handler.obtainMessage();
                        message.what = 10005;
                        message.obj = document;
                        handler.sendMessage(message);
                        refreshLayout.finishRefresh();
                    }
                });
            }
        });
        newPictureRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isLoadMore = true;
                defaultPage++;
                String url = replaceIndex(categoryUrl, defaultPage);
                if (url.equals("")) {
                    return;
                }
                HttpHelper.captureHtmlData(url, new HtmlParserCallBackListener() {

                    @Override
                    public void onParserDone(Document document) throws IOException {
                        Message message = handler.obtainMessage();
                        message.what = 10005;
                        message.obj = document;
                        handler.sendMessage(message);
                        refreshLayout.finishLoadMore();
                    }
                });
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
            if (msg.what == 10005) {
                Document document = (Document) msg.obj;
                if (document == null) {
                    JsonToBean("");
                } else {
                    JsonToBean(HtmlParserHelper.getLoadMoreList(document));
                }
            } else if (msg.what == 20000) {
                if (isRefresh || isLoadMore) {
                    pictureAdapter.notifyDataSetChanged();
                } else {
                    //首次加载数据
                    pictureAdapter = new PictureAdapter(getActivity(), listBeans);
                    newPictureView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    newPictureView.setAdapter(pictureAdapter);
                }
                pictureAdapter.setOnNewsItemClickListener(new PictureAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        String childUrl = listBeans.get(position).getChildUrl();
                        String childTitle = listBeans.get(position).getChildTitle();
                        Intent intent = new Intent(getActivity(), PictureViewActivity.class);
                        intent.putExtra("childTitle", childTitle);
                        intent.putExtra("childUrl", childUrl);
                        startActivity(intent);
                    }
                });
            }
            OtherUtils.hideProgressDialog();
        }
    };

    private void JsonToBean(String result) {
        if (result.equals("")) {
            listBeans = categoryBean.getList();
        } else {
            ResultBean resultBean = JSONObject.parseObject(result, ResultBean.class);
            List<ResultBean.CategoryBean.ListBean> beans = resultBean.getBeanList().get(0).getList();
            if (isRefresh) {
                listBeans.clear();
                for (int i = 0; i < beans.size(); i++) {
                    listBeans.add(0, beans.get(i));
                }
                isRefresh = false;
            } else if (isLoadMore) {
                listBeans.addAll(beans);
                isLoadMore = false;
            }
        }
        //更新List
        handler.sendEmptyMessage(20000);
    }

    /**
     * 替换页面索引
     */
    private String replaceIndex(String url, int index) {
        if (index > 5) {
            newPictureRefresh.finishLoadMore();
            return "";
        }
        StringBuilder builder = new StringBuilder(url);
        int startIndex = categoryUrl.lastIndexOf("_");
        int endIndex = categoryUrl.lastIndexOf(".");

        StringBuilder replace = builder.replace(startIndex + 1, endIndex, String.valueOf(index));
        String newURL = replace.toString();
        Log.d(TAG, "replaceIndex: " + newURL);
        return newURL;
    }
}
