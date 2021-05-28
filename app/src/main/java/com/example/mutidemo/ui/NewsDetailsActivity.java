package com.example.mutidemo.ui;

import android.os.Build;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.mutidemo.R;
import com.example.mutidemo.util.ImageUtil;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import butterknife.BindView;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/3/5 20:18
 */
public class NewsDetailsActivity extends BaseNormalActivity {

    @BindView(R.id.newsTitle)
    TextView newsTitle;
    @BindView(R.id.newsSrc)
    TextView newsSrc;
    @BindView(R.id.newsTime)
    TextView newsTime;
    @BindView(R.id.newsContent)
    TextView newsContent;

    @Override
    public int initLayoutView() {
        return R.layout.activity_news_details;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void initData() {
        String title = getIntent().getStringExtra("title");
        String src = getIntent().getStringExtra("src");
        String time = getIntent().getStringExtra("time");
        String content = getIntent().getStringExtra("content");

        newsTitle.setText(title);
        newsSrc.setText(src);
        newsTime.setText(time);

        ImageUtil.setTextFromHtml(this, newsContent, content,
                QMUIDisplayHelper.getScreenWidth(this), 10);
    }

    @Override
    public void initEvent() {

    }
}
