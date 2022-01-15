package com.example.mutidemo.ui;

import android.app.ProgressDialog;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mutidemo.R;
import com.example.mutidemo.util.FileUtils;
import com.example.mutidemo.util.GlideLoadEngine;
import com.example.mutidemo.util.JZMediaExo;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.widget.EasyToast;
import com.zolad.videoslimmer.VideoSlimmer;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jzvd.JzvdStd;

public class VideoCompressActivity extends BaseNormalActivity implements View.OnClickListener {

    private static final String TAG = "VideoCompressActivity";
    private static final int BITRATE = 200 * 360 * 30;

    @BindView(R.id.originalVideoView)
    JzvdStd originalVideoView;
    @BindView(R.id.compressedVideoView)
    JzvdStd compressedVideoView;

    private int defaultWidth = 720;
    private int defaultHeight = 1280;
    private String defaultRotation = "90";//视频为竖屏，0为横屏
    private String mediaOriginalPath;
    private ProgressDialog progressDialog;

    @Override
    public int initLayoutView() {
        return R.layout.activity_video_compress;
    }

    @Override
    public void initData() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("视频压缩中...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.bg_progress));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void initEvent() {
        testMediaInterface();
    }

    @OnClick({R.id.selectVideoButton, R.id.compressVideoButton})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectVideoButton:
                PictureSelector.create(VideoCompressActivity.this)
                        .openGallery(SelectMimeType.ofVideo())
                        .setImageEngine(GlideLoadEngine.createGlideEngine())
                        .setMaxSelectNum(1)
                        .setRecordVideoMaxSecond(20)
                        .isPreviewVideo(true)
                        .forResult(new OnResultCallbackListener<LocalMedia>() {

                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {
                                onSelectResult(result);
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                break;
            case R.id.compressVideoButton:
                if (!TextUtils.isEmpty(mediaOriginalPath)) {
                    String outputVideoFile = FileUtils.getVideoFilePath();
                    int width, height;
                    if (defaultRotation.equals("90")) {
                        width = defaultHeight / 2;
                        height = defaultWidth / 2;
                    } else {
                        width = defaultWidth / 2;
                        height = defaultHeight / 2;
                    }
                    VideoSlimmer.convertVideo(mediaOriginalPath, outputVideoFile, width, height, BITRATE, new VideoSlimmer.ProgressListener() {
                        @Override
                        public void onStart() {
                            progressDialog.show();
                        }

                        @Override
                        public void onProgress(float percent) {
                            progressDialog.setProgress((int) percent);
                        }

                        @Override
                        public void onFinish(boolean result) {
                            //convert finish,result(true is success,false is fail)
                            if (result) {
                                compressedVideoView.setUp(outputVideoFile, "", JzvdStd.SCREEN_NORMAL, JZMediaExo.class);
                                Glide.with(VideoCompressActivity.this)
                                        .setDefaultRequestOptions(new RequestOptions().frame(4000000))
                                        .load(outputVideoFile)
                                        .into(compressedVideoView.posterImageView);
                            } else {
                                EasyToast.showToast("压缩失败", EasyToast.ERROR);
                            }
                            progressDialog.dismiss();
                        }

                    });
                }
                break;
        }
    }

    private void testMediaInterface() {
        String url = "http://111.198.10.15:11409/static/2021-05/b9d0e7bf520f4f50a0dedb76bf4b70aa.mp4";
//        compressedVideoView.setUp(url, "", JzvdStd.SCREEN_NORMAL);
        compressedVideoView.setUp(url, "", JzvdStd.SCREEN_NORMAL, JZMediaExo.class);
        Glide.with(VideoCompressActivity.this)
                .setDefaultRequestOptions(new RequestOptions().frame(4000000))
                .load(url)
                .into(compressedVideoView.posterImageView);
    }

    private void onSelectResult(ArrayList<LocalMedia> result) {
        for (LocalMedia media : result) {
            this.defaultWidth = media.getWidth();
            this.defaultHeight = media.getHeight();
            this.mediaOriginalPath = media.getRealPath();

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(mediaOriginalPath);
            this.defaultRotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            Log.d(TAG, "defaultRotation: " + defaultRotation);

            originalVideoView.setUp(mediaOriginalPath, media.getFileName());
            //设置第一帧为封面
            Glide.with(this)
                    .setDefaultRequestOptions(new RequestOptions().frame(4000000))
                    .load(mediaOriginalPath)
                    .into(originalVideoView.posterImageView);
        }
    }
}
