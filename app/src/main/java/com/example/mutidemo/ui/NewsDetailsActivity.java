package com.example.mutidemo.ui;

import android.widget.TextView;

import com.example.mutidemo.R;
import com.example.mutidemo.util.ImageUtil;
import com.example.mutidemo.util.OtherUtils;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import java.util.HashMap;

import butterknife.BindView;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/3/5 20:18
 */
public class NewsDetailsActivity extends BaseNormalActivity {

    private static final String TAG = "NewsDetailsActivity";

    @BindView(R.id.newsTitle)
    TextView newsTitle;
    @BindView(R.id.newsSrc)
    TextView newsSrc;
    @BindView(R.id.newsTime)
    TextView newsTime;
    @BindView(R.id.newsContent)
    TextView newsContent;

    @Override
    public void initView() {
        setContentView(R.layout.activity_news_details);
    }

    @Override
    public void initData() {
        String title = getIntent().getStringExtra("title");
        String src = getIntent().getStringExtra("src");
        String time = getIntent().getStringExtra("time");
        String content = getIntent().getStringExtra("content");

        newsTitle.setText(title);
        newsSrc.setText(src);
        newsTime.setText(time);

        HashMap<String, Integer> displaySize = OtherUtils.getDisplaySize(this);
        float widthPx = displaySize.get("widthPx");
        ImageUtil.setTextFromHtml(this, newsContent, content, widthPx);
    }

    @Override
    public void initEvent() {

    }
}
