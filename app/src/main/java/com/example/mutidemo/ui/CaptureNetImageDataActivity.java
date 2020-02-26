package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.example.mutidemo.R;
import com.example.mutidemo.bean.LocalDataBean;
import com.example.mutidemo.bean.ResultBean;
import com.example.mutidemo.ui.fragment.BeautyPictureFragment;
import com.example.mutidemo.ui.fragment.FilmPictureFragment;
import com.example.mutidemo.ui.fragment.NewPictureFragment;
import com.example.mutidemo.ui.fragment.StarPictureFragment;
import com.example.mutidemo.util.HtmlParserHelper;
import com.example.mutidemo.util.HttpCallBackListener;
import com.example.mutidemo.util.HttpHelper;
import com.example.mutidemo.util.OtherUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.StringUtil;
import com.pengxh.app.multilib.widget.EasyToast;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Response;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/25 12:56
 */
public class CaptureNetImageDataActivity extends BaseNormalActivity {

    private static final String TAG = "CaptureNetImageData";
    @BindView(R.id.pagerTabLayout)
    TabLayout pagerTabLayout;
    @BindView(R.id.pictureViewPager)
    ViewPager pictureViewPager;


    @Override
    public void initView() {
        setContentView(R.layout.activity_capture_net);
    }

    @Override
    public void initData() {
        String assetsData = StringUtil.getAssetsData(this, "resources.json");
        List<LocalDataBean> dataBeanList = new Gson().fromJson(assetsData, new TypeToken<List<LocalDataBean>>() {
        }.getType());

        OtherUtils.showProgressDialog(this, "数据加载中...");
        String url = dataBeanList.get(0).getUrl();
        Log.d(TAG, "抓取数据地址: " + url);
        HttpHelper.captureHtmlData(url, new HttpCallBackListener() {
            @Override
            public void onSuccess(Response response) throws IOException {

            }

            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onParserDone(Document document) throws IOException {
                Message message = handler.obtainMessage();
                message.what = 10000;
                message.obj = HtmlParserHelper.getCategoryList(document);
                handler.sendMessage(message);
            }
        });
    }

    private List<String> tabTitle = new ArrayList<>();
    private String categoryUrl;
    private List<ResultBean.CategoryBean> categoryBeans = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    public void initEvent() {

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
                    String s = (String) msg.obj;
                    ResultBean resultBean = JSONObject.parseObject(s, ResultBean.class);
                    List<ResultBean.CategoryBean> resultBeanList = resultBean.getBeanList();
                    for (ResultBean.CategoryBean bean : resultBeanList) {
                        categoryUrl = bean.getUrl();
                        tabTitle.add(bean.getTitle());
                        categoryBeans.add(bean);
                    }
                    //最新手机壁纸, 美女手机壁纸, 明星手机壁纸, 影视手机壁纸
//                    Log.d(TAG, "handleMessage: "+tabTitle);
                    //TODO 绑定数据
                    NewPictureFragment newPictureFragment = new NewPictureFragment();
                    newPictureFragment.setData(categoryBeans.get(0), categoryUrl);
                    fragments.add(newPictureFragment);
                    fragments.add(new BeautyPictureFragment());
                    fragments.add(new StarPictureFragment());
                    fragments.add(new FilmPictureFragment());

                    FragmentPagerAdapter adapter = new PicturePageAdapter(getSupportFragmentManager(), fragments, tabTitle);
                    pictureViewPager.setAdapter(adapter);
                    pagerTabLayout.setupWithViewPager(pictureViewPager);
                    break;
                case 10001:
                    EasyToast.showToast("获取数据失败", EasyToast.ERROR);
                    break;
                default:
                    break;
            }
            OtherUtils.hideProgressDialog();
        }
    };

    private static class PicturePageAdapter extends FragmentPagerAdapter {

        private List<Fragment> pageList;
        private List<String> titleList;

        PicturePageAdapter(FragmentManager fm, List<Fragment> pages, List<String> titles) {
            super(fm);
            this.pageList = pages;
            this.titleList = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return pageList.get(position);
        }

        @Override
        public int getCount() {
            return pageList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }
}