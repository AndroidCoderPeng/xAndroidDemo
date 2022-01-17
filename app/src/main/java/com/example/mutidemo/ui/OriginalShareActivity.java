package com.example.mutidemo.ui;

import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.mutidemo.base.AndroidxBaseActivity;
import com.example.mutidemo.databinding.ActivityOriginalBinding;
import com.example.mutidemo.util.GlideLoadEngine;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;

import java.io.File;
import java.util.ArrayList;

public class OriginalShareActivity extends AndroidxBaseActivity<ActivityOriginalBinding> {

    private String realPath;

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        viewBinding.selectImageButton.setOnClickListener(v -> EasyPhotos.createAlbum(OriginalShareActivity.this, true, false, GlideLoadEngine.getInstance())
                .setFileProviderAuthority("com.example.mutidemo.fileProvider")
                .setCount(1)
                .setMinFileSize(1024 * 10)
                .start(new SelectCallback() {
                    @Override
                    public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                        Photo resultMedia = photos.get(0);
                        realPath = resultMedia.path;
                        viewBinding.imagePathView.setText(realPath);
                        Glide.with(OriginalShareActivity.this).load(realPath).into(viewBinding.imageView);
                    }

                    @Override
                    public void onCancel() {

                    }
                }));
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
