package com.example.mutidemo.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mutidemo.R;
import com.example.mutidemo.util.FileUtils;
import com.example.mutidemo.util.GlideLoadEngine;
import com.example.mutidemo.util.ImageUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class WaterMarkerActivity extends BaseNormalActivity implements View.OnClickListener {

    @BindView(R.id.originalImageView)
    ImageView originalImageView;
    @BindView(R.id.markerImageView)
    ImageView markerImageView;
    private String mediaCompressPath;

    @Override
    public int initLayoutView() {
        return R.layout.activity_water_marker;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }

    @OnClick({R.id.selectImageButton, R.id.addMarkerButton})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectImageButton:
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofImage())
                        .imageEngine(GlideLoadEngine.createGlideEngine())
                        .maxSelectNum(2)
                        .isCompress(true)
                        .compressQuality(80)
                        .compressSavePath(FileUtils.getImageCompressPath())
                        .forResult(PictureConfig.CHOOSE_REQUEST);
                break;
            case R.id.addMarkerButton:
                if (!TextUtils.isEmpty(mediaCompressPath)) {
                    Log.d("TAG", "mediaCompressPath: " + mediaCompressPath);
                    Bitmap bitmap = BitmapFactory.decodeFile(mediaCompressPath);
                    String path = ImageUtil.drawTextToRightBottom(this, bitmap, "cgjd01", "20210525", "17:55:55");
                    Log.d("TAG", "path: " + path);
                    Glide.with(this).load(path)
                            .apply(new RequestOptions().error(R.drawable.ic_load_error))
                            .into(markerImageView);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                for (LocalMedia media : selectList) {
                    mediaCompressPath = media.getCompressPath();
                    Glide.with(this)
                            .load(mediaCompressPath)
                            .apply(new RequestOptions().error(R.drawable.ic_load_error))
                            .into(originalImageView);
                }
            }
        }
    }
}
