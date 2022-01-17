package com.example.mutidemo.ui;

import com.example.mutidemo.base.AndroidxBaseActivity;
import com.example.mutidemo.databinding.ActivityNewsDetailsBinding;
import com.example.mutidemo.util.ImageUtil;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/3/5 20:18
 */
public class NewsDetailsActivity extends AndroidxBaseActivity<ActivityNewsDetailsBinding> {

    @Override
    public void initData() {
        String title = getIntent().getStringExtra("title");
        String src = getIntent().getStringExtra("src");
        String time = getIntent().getStringExtra("time");
        String content = getIntent().getStringExtra("content");

        viewBinding.newsTitle.setText(title);
        viewBinding.newsSrc.setText(src);
        viewBinding.newsTime.setText(time);

        ImageUtil.setTextFromHtml(this, viewBinding.newsContent, content,
                QMUIDisplayHelper.getScreenWidth(this), 10);
    }

    @Override
    public void initEvent() {

    }
}
