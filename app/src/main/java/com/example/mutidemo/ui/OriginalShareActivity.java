package com.example.mutidemo.ui;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.mutidemo.R;
import com.example.mutidemo.util.GlideLoadEngine;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;

public class OriginalShareActivity extends BaseNormalActivity {

    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.imagePathView)
    TextView imagePathView;
    @BindView(R.id.selectImageButton)
    Button selectImageButton;
    @BindView(R.id.shareImageButton)
    Button shareImageButton;

    private String realPath;

    @Override
    public int initLayoutView() {
        return R.layout.activity_original;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyPhotos.createAlbum(OriginalShareActivity.this, true, false, GlideLoadEngine.getInstance())
                        .setFileProviderAuthority("com.example.mutidemo.fileProvider")
                        .setCount(1)
                        .setMinFileSize(1024 * 10)
                        .start(new SelectCallback() {
                            @Override
                            public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                                Photo resultMedia = photos.get(0);
                                realPath = resultMedia.path;
                                imagePathView.setText(realPath);
                                Glide.with(OriginalShareActivity.this).load(realPath).into(imageView);
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            }
        });
        shareImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
    }
}
