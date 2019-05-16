package com.example.mutidemo.ui;

import android.widget.ListView;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.MultiListViewAdapter;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/7/20.
 */

public class MultiListViewActivity extends BaseNormalActivity {


    @BindView(R.id.mVerticListView)
    ListView mVerticListView;

    private List<String> mVerticItemList = Arrays.asList("认真的雪", "怪咖", "演员", "你还要我怎样", "丑八怪", "刚刚好", "初学者", "我终于成了别人的女人", "方圆几里", "绅士", "初学者", "哑巴");
    private List<String> mHorizItemList = Arrays.asList("http://simg.s.weibo.com/gs1_3536924658590100566.jpg",
            "http://simg.s.weibo.com/gs1_3536924658590100566.jpg",
            "http://simg.s.weibo.com/gs1_1854835374095387516.jpg",
            "http://simg.s.weibo.com/gs1_11211502726571496143.jpg",
            "http://simg.s.weibo.com/gs1_3536924658590100566.jpg",
            "http://simg.s.weibo.com/gs1_11211502726571496143.jpg",
            "http://simg.s.weibo.com/gs1_1854835374095387516.jpg",
            "http://simg.s.weibo.com/gs1_11211502726571496143.jpg");

    @Override
    public void initView() {
        setContentView(R.layout.activity_mutillistview);
    }

    @Override
    public void init() {

    }

    @Override
    public void initEvent() {
        MultiListViewAdapter adapter = new MultiListViewAdapter(MultiListViewActivity.this, mVerticItemList, mHorizItemList);
        mVerticListView.setAdapter(adapter);
    }
}
