package com.example.mutidemo.ui;

import com.example.mutidemo.R;
import com.pengxh.app.multilib.base.BaseNormalActivity;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/26 12:47
 */

public class PictureViewActivity extends BaseNormalActivity {

    private String childUrl;

    @Override
    public void initView() {
        setContentView(R.layout.activity_picture);
    }

    @Override
    public void initData() {
        childUrl = getIntent().getStringExtra("childUrl");
    }

    @Override
    public void initEvent() {

    }
}
