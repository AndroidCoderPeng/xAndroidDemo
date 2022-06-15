package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.mutidemo.databinding.ActivityAudioBinding;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.utils.FileUtil;
import com.pengxh.androidx.lite.utils.TimeOrDateUtil;
import com.pengxh.androidx.lite.widget.audio.AudioPopupWindow;
import com.pengxh.androidx.lite.widget.audio.AudioRecodeHelper;

public class RecodeAudioActivity extends AndroidxBaseActivity<ActivityAudioBinding> {

    private AudioRecodeHelper audioRecodeHelper;

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    public void initData() {
        AudioPopupWindow.create(this, new AudioPopupWindow.IWindowListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onViewCreated(PopupWindow window, ImageView imageView, TextView textView) {
                viewBinding.recodeAudioButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                viewBinding.recodeAudioButton.animate().scaleX(0.75f).scaleY(0.75f).setDuration(100).start();
                                window.showAtLocation(viewBinding.parentLayout, Gravity.CENTER, 0, 0);
                                audioRecodeHelper.startRecordAudio(FileUtil.createAudioFile(RecodeAudioActivity.this).toString());
                                break;
                            case MotionEvent.ACTION_UP:
                                audioRecodeHelper.stopRecordAudio();//结束录音（保存录音文件）
                                window.dismiss();
                                viewBinding.recodeAudioButton.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                                break;
                        }
                        return true;
                    }
                });

                audioRecodeHelper = new AudioRecodeHelper();
                audioRecodeHelper.setOnAudioStatusUpdateListener(new AudioRecodeHelper.OnAudioStatusUpdateListener() {
                    @Override
                    public void onUpdate(double db, long time) {
                        imageView.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
                        textView.setText(TimeOrDateUtil.millsToTime(time));
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onStop(String filePath) {
                        viewBinding.audioFilePathView.setText("录音文件路径：\r\n" + filePath);
                        if (!TextUtils.isEmpty(filePath)) {
                            viewBinding.audioPlayView.setAudioUrl(filePath);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void onDestroy() {
        viewBinding.audioPlayView.release();
        super.onDestroy();
    }
}
