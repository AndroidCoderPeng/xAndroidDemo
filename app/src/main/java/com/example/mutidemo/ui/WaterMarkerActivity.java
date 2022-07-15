package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
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
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.utils.FileUtil;
import com.pengxh.androidx.lite.utils.ImageUtil;
import com.pengxh.androidx.lite.utils.TimeOrDateUtil;
import com.pengxh.androidx.lite.utils.WeakReferenceHandler;
import com.pengxh.androidx.lite.widget.EasyToast;

import java.io.File;
import java.util.ArrayList;

@SuppressLint("SetTextI18n")
public class WaterMarkerActivity extends AndroidxBaseActivity<ActivityWaterMarkerBinding> {

    private final Context context = this;
    private WeakReferenceHandler weakReferenceHandler;
    private String mediaRealPath;

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    public void initData() {
        weakReferenceHandler = new WeakReferenceHandler(callback);
    }

    @Override
    public void initEvent() {
        viewBinding.selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(WaterMarkerActivity.this)
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
                                if (result == null) {
                                    EasyToast.show(WaterMarkerActivity.this, "选择照片失败，请重试");
                                    return;
                                }
                                // 线程控制图片压缩上传过程，防止速度过快导致压缩失败
                                int sum = (result.size() * 500);
                                new CountDownTimer(sum, 500) {

                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        int i = (int) (millisUntilFinished / 500);
                                        Message message = weakReferenceHandler.obtainMessage();
                                        message.obj = result.get(i);
                                        message.what = 2022061702;
                                        weakReferenceHandler.handleMessage(message);
                                    }

                                    @Override
                                    public void onFinish() {

                                    }
                                }.start();
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            }
        });

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

    private final Handler.Callback callback = msg -> {
        if (msg.what == 2022061702) {
            LocalMedia obj = (LocalMedia) msg.obj;
            mediaRealPath = obj.getRealPath();
            Glide.with(this)
                    .load(mediaRealPath)
                    .apply(new RequestOptions().error(R.drawable.ic_load_error))
                    .into(viewBinding.originalImageView);
            viewBinding.originalImageSizeView.setText("压缩前：" + FileUtil.formatFileSize(obj.getSize()));
            viewBinding.originalImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> urls = new ArrayList<>();
                    urls.add(mediaRealPath);
                    ImageUtil.showBigImage(context, 0, urls);
                }
            });
        }
        return true;
    };
}
