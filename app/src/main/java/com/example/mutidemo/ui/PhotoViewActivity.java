package com.example.mutidemo.ui;

import com.bumptech.glide.Glide;
import com.example.mutidemo.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import butterknife.BindView;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/3/6 12:49
 */
public class PhotoViewActivity extends BaseNormalActivity {

    @BindView(R.id.photoView)
    PhotoView photoView;

    @Override
    public void initView() {
        setContentView(R.layout.activity_big_picture);
    }

    @Override
    public void initData() {
        String imageURL = getIntent().getStringExtra("imageURL");
        Glide.with(this).load(imageURL).into(photoView);
    }

    @Override
    public void initEvent() {

    }
}
