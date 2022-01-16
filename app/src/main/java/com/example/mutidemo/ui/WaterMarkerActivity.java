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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mutidemo.R;
import com.example.mutidemo.util.FileUtils;
import com.example.mutidemo.util.GlideLoadEngine;
import com.example.mutidemo.util.ImageUtil;
import com.example.mutidemo.util.OtherUtils;
import com.example.mutidemo.util.TimeOrDateUtil;
import com.example.mutidemo.util.callback.ICompressListener;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.FileUtil;
import com.pengxh.app.multilib.widget.EasyToast;

import java.io.File;
import java.util.ArrayList;

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
                EasyPhotos.createAlbum(this, true, false, GlideLoadEngine.getInstance())
                        .setFileProviderAuthority("com.example.mutidemo.fileProvider")
                        .start(101);//也可以选择链式调用写法
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
        if (resultPhotos == null) {
            EasyToast.showToast("选择图片失败", EasyToast.ERROR);
            return;
        }
        if (resultPhotos.size() >= 1) {
            Photo photo = resultPhotos.get(0);
            mediaRealPath = photo.path;
            Glide.with(this)
                    .load(photo.uri)
                    .apply(new RequestOptions().error(R.drawable.ic_load_error))
                    .into(originalImageView);
            originalImageSizeView.setText("压缩前：" + FileUtil.formatFileSize(photo.size));
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
