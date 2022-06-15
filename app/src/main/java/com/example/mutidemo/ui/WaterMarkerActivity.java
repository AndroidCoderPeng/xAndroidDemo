package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mutidemo.R;
import com.example.mutidemo.databinding.ActivityWaterMarkerBinding;
import com.example.mutidemo.util.FileUtils;
import com.example.mutidemo.util.GlideLoadEngine;
import com.example.mutidemo.util.ImageHelper;
import com.example.mutidemo.util.OtherUtils;
import com.example.mutidemo.util.callback.ICompressListener;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.utils.FileUtil;
import com.pengxh.androidx.lite.utils.ImageUtil;
import com.pengxh.androidx.lite.utils.TimeOrDateUtil;
import com.pengxh.androidx.lite.widget.EasyToast;

import java.io.File;
import java.util.ArrayList;

@SuppressLint("SetTextI18n")
public class WaterMarkerActivity extends AndroidxBaseActivity<ActivityWaterMarkerBinding> {

    private final Context context = this;
    private String mediaRealPath;

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        viewBinding.selectImageButton.setOnClickListener(view ->
                EasyPhotos.createAlbum(WaterMarkerActivity.this, true, false, GlideLoadEngine.getInstance())
                        .setFileProviderAuthority("com.example.mutidemo.fileProvider")
                        .start(101));

        viewBinding.addMarkerButton.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(mediaRealPath)) {
                Bitmap bitmap = BitmapFactory.decodeFile(mediaRealPath);

                OtherUtils.showLoadingDialog(context, "水印添加中，请稍后...");
                ImageHelper.drawTextToRightBottom(context, bitmap, TimeOrDateUtil.timestampToCompleteDate(System.currentTimeMillis()), file -> {
                    OtherUtils.dismissLoadingDialog();
                    ImageHelper.compressImage(file.getPath(), FileUtils.getImageCompressPath(), new ICompressListener() {
                        @Override
                        public void onSuccess(File file) {
                            Glide.with(context)
                                    .load(file)
                                    .apply(new RequestOptions().error(R.drawable.ic_load_error))
                                    .into(viewBinding.markerImageView);
                            viewBinding.markerImageSizeView.setText("压缩后：" + FileUtil.formatFileSize(file.length()));
                            viewBinding.markerImageView.setOnClickListener(v -> {
                                ArrayList<String> urls = new ArrayList<>();
                                urls.add(file.getPath());
                                ImageUtil.showBigImage(context, 0, urls);
                            });
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
        if (resultPhotos == null) {
            EasyToast.show(this, "选择图片失败");
            return;
        }
        if (resultPhotos.size() >= 1) {
            Photo photo = resultPhotos.get(0);
            mediaRealPath = photo.path;
            Glide.with(this)
                    .load(photo.uri)
                    .apply(new RequestOptions().error(R.drawable.ic_load_error))
                    .into(viewBinding.originalImageView);
            viewBinding.originalImageSizeView.setText("压缩前：" + FileUtil.formatFileSize(photo.size));
            viewBinding.originalImageView.setOnClickListener(new View.OnClickListener() {
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
