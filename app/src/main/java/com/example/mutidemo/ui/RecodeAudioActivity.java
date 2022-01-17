package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.mutidemo.R;
import com.example.mutidemo.base.AndroidxBaseActivity;
import com.example.mutidemo.databinding.ActivityAudioBinding;
import com.example.mutidemo.util.AudioRecodeHelper;
import com.example.mutidemo.util.TimeOrDateUtil;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

public class RecodeAudioActivity extends AndroidxBaseActivity<ActivityAudioBinding> implements View.OnTouchListener {

    private AudioRecodeHelper audioRecodeHelper;
    private PopupWindow popWindow;

    @Override
    public void initData() {
        View view = View.inflate(this, R.layout.popu_microphone, null);
        int popWidth = (int) (QMUIDisplayHelper.getScreenWidth(this) * 0.35);
        int popHeight = (int) (QMUIDisplayHelper.getScreenWidth(this) * 0.30);
        popWindow = new PopupWindow(view, popWidth, popHeight, true);
        popWindow.setAnimationStyle(R.style.PopupAnimation);
        ImageView recodeImageView = view.findViewById(R.id.recodeImageView);
        TextView recodeTextView = view.findViewById(R.id.recodeTextView);
        audioRecodeHelper = new AudioRecodeHelper();
        audioRecodeHelper.setOnAudioStatusUpdateListener(new AudioRecodeHelper.OnAudioStatusUpdateListener() {
            @Override
            public void onUpdate(double db, long time) {
                recodeImageView.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
                recodeTextView.setText(TimeOrDateUtil.millsToTime(time));
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initEvent() {
        viewBinding.recodeAudioButton.setOnTouchListener(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                viewBinding.recodeAudioButton.animate().scaleX(0.75f).scaleY(0.75f).setDuration(100).start();
                popWindow.showAtLocation(viewBinding.parentLayout, Gravity.CENTER, 0, 0);
                audioRecodeHelper.startRecordAudio();
                break;
            case MotionEvent.ACTION_UP:
                audioRecodeHelper.stopRecordAudio();//结束录音（保存录音文件）
                popWindow.dismiss();
                viewBinding.recodeAudioButton.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                break;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        viewBinding.audioPlayView.release();
        super.onDestroy();
    }
}
