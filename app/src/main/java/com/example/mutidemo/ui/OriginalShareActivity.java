package com.example.mutidemo.ui;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.mutidemo.databinding.ActivityOriginalBinding;
import com.example.mutidemo.util.GlideLoadEngine;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;

import java.io.File;
import java.util.ArrayList;

public class OriginalShareActivity extends AndroidxBaseActivity<ActivityOriginalBinding> {

    private String realPath;

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        viewBinding.selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(OriginalShareActivity.this)
                        .openGallery(SelectMimeType.ofImage())
                        .isGif(false)
                        .isMaxSelectEnabledMask(true)
                        .setFilterMinFileSize(100)
                        .setMaxSelectNum(1)
                        .isDisplayCamera(false)
                        .setImageEngine(GlideLoadEngine.getInstance())
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {
                                LocalMedia resultMedia = result.get(0);
                                realPath = resultMedia.getRealPath();
                                viewBinding.imagePathView.setText(realPath);
                                Glide.with(OriginalShareActivity.this).load(realPath).into(viewBinding.imageView);
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            }
        });

        viewBinding.shareImageButton.setOnClickListener(v -> {
            if (realPath == null) {
                return;
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, "文件分享");
            // 兼容android 7.0+
            Uri uri = FileProvider.getUriForFile(
                    OriginalShareActivity.this,
                    "com.example.mutidemo.fileProvider",
                    new File(realPath));
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setType("image/*");
            startActivity(intent);
        });
    }
}
