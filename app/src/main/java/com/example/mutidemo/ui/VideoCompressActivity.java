package com.example.mutidemo.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mutidemo.R;
import com.example.mutidemo.util.FileUtils;
import com.example.mutidemo.util.GlideLoadEngine;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.widget.EasyToast;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.zolad.videoslimmer.VideoSlimmer;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jzvd.JzvdStd;

public class VideoCompressActivity extends BaseNormalActivity implements View.OnClickListener {

    private static final String TAG = "VideoCompressActivity";

    @BindView(R.id.originalVideoView)
    JzvdStd originalVideoView;
    @BindView(R.id.compressedVideoView)
    JzvdStd compressedVideoView;

    private String mediaOriginalPath;
    private int originalWidth = 720;
    private int originalHeight = 1280;
    private QMUITipDialog loadingDialog;

    @Override
    public int initLayoutView() {
        return R.layout.activity_video_compress;
    }

    @Override
    public void initData() {
        loadingDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("视频压缩中，请稍后")
                .create();
    }

    @Override
    public void initEvent() {

    }

    @OnClick({R.id.selectVideoButton, R.id.compressVideoButton})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectVideoButton:
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofVideo())
                        .isWeChatStyle(true)
                        .imageEngine(GlideLoadEngine.createGlideEngine())
                        .maxSelectNum(1)
                        .videoMaxSecond(20)
                        .isPreviewVideo(true)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
                break;
            case R.id.compressVideoButton:
                if (!TextUtils.isEmpty(mediaOriginalPath)) {
                    String outputVideoFile = FileUtils.getOutputVideoFile();
                    //TODO 宽高要考虑是否会变形
                    VideoSlimmer.convertVideo(mediaOriginalPath, outputVideoFile, originalHeight / 2, originalWidth / 2, 200 * 360 * 30, new VideoSlimmer.ProgressListener() {
                        @Override
                        public void onStart() {
                            //convert start
                            loadingDialog.show();
                        }

                        @Override
                        public void onFinish(boolean result) {
                            //convert finish,result(true is success,false is fail)
                            if (result) {
                                loadingDialog.dismiss();
                                compressedVideoView.setUp(outputVideoFile, "");
                                Glide.with(VideoCompressActivity.this)
                                        .setDefaultRequestOptions(new RequestOptions().frame(4000000))
                                        .load(outputVideoFile)
                                        .into(compressedVideoView.posterImageView);
                            } else {
                                EasyToast.showToast("压缩失败", EasyToast.ERROR);
                            }
                        }


                        @Override
                        public void onProgress(float percent) {
                            //percent of progress
                            Log.d(TAG, "onProgress: " + percent);
                        }
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
                    Log.d(TAG, "onActivityResult: " + new Gson().toJson(media));
                    this.originalWidth = media.getWidth();
                    this.originalHeight = media.getHeight();
                    this.mediaOriginalPath = media.getRealPath();

                    originalVideoView.setUp(mediaOriginalPath, media.getFileName());
                    //设置第一帧为封面
                    Glide.with(this)
                            .setDefaultRequestOptions(new RequestOptions().frame(4000000))
                            .load(mediaOriginalPath)
                            .into(originalVideoView.posterImageView);
                }
            }
        }
    }
}
