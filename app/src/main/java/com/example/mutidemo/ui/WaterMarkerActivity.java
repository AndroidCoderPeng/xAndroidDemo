package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mutidemo.R;
import com.example.mutidemo.util.FileUtils;
import com.example.mutidemo.util.GlideLoadEngine;
import com.example.mutidemo.util.ImageUtil;
import com.example.mutidemo.util.OtherUtils;
import com.example.mutidemo.util.TimeOrDateUtil;
import com.example.mutidemo.util.callback.ICompressListener;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("SetTextI18n")
public class WaterMarkerActivity extends BaseNormalActivity implements View.OnClickListener {

    @BindView(R.id.originalImageView)
    ImageView originalImageView;
    @BindView(R.id.markerImageView)
    ImageView markerImageView;
    @BindView(R.id.originalImageSizeView)
    TextView originalImageSizeView;
    @BindView(R.id.markerImageSizeView)
    TextView markerImageSizeView;

    private Context context = this;
    private String mediaRealPath;

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
                        .isWeChatStyle(true)
                        .imageEngine(GlideLoadEngine.createGlideEngine())
                        .maxSelectNum(1)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
                break;
            case R.id.addMarkerButton:
                /**
                 * 如果出现添加水印之后压缩OOM的，可以用PictureSelector直接压缩然后添加水印，再以jpeg的编码方式，75%的像素质量保存为png图片
                 * 这样既能添加水印，也能保证图片的像素
                 * */
                if (!TextUtils.isEmpty(mediaRealPath)) {
                    Bitmap bitmap = BitmapFactory.decodeFile(mediaRealPath);

                    OtherUtils.showLoadingDialog(this, "水印添加中，请稍后...");
                    ImageUtil.drawTextToRightBottom(this, bitmap, TimeOrDateUtil.timestampToCompleteDate(System.currentTimeMillis()), file -> {
                        OtherUtils.dismissLoadingDialog();
                        ImageUtil.compressImage(file.getPath(), FileUtils.getImageCompressPath(), new ICompressListener() {
                            @Override
                            public void onSuccess(File file) {
                                Glide.with(context)
                                        .load(file)
                                        .apply(new RequestOptions().error(R.drawable.ic_load_error))
                                        .into(markerImageView);
                                markerImageSizeView.setText("压缩后：" + FileUtil.formatFileSize(file.length()));
                                markerImageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v1) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        urls.add(file.getPath());
                                        ImageUtil.showBigImage(context, 0, urls);
                                    }
                                });
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });
                    });
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
                    mediaRealPath = media.getRealPath();
                    Glide.with(this)
                            .load(mediaRealPath)
                            .apply(new RequestOptions().error(R.drawable.ic_load_error))
                            .into(originalImageView);
                    originalImageSizeView.setText("压缩前：" + FileUtil.formatFileSize(media.getSize()));
                    originalImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<String> urls = new ArrayList<>();
                            urls.add(mediaRealPath);
                            ImageUtil.showBigImage(context, 0, urls);
                        }
                    });
                }
            }
        }
    }
}
