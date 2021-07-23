package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mutidemo.R;
import com.example.mutidemo.util.AudioRecodeHelper;
import com.example.mutidemo.util.TimeOrDateUtil;
import com.example.mutidemo.widget.AudioPlayerView;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import butterknife.BindView;

public class RecodeAudioActivity extends BaseNormalActivity implements View.OnTouchListener {

    private static final String TAG = "RecodeAudioActivity";
    @BindView(R.id.parentLayout)
    RelativeLayout parentLayout;
    @BindView(R.id.recodeAudioButton)
    ImageButton recodeAudioButton;
    @BindView(R.id.audioFilePathView)
    TextView audioFilePathView;
    @BindView(R.id.audioPlayView)
    AudioPlayerView audioPlayView;

    private AudioRecodeHelper audioRecodeHelper;
    private PopupWindow popWindow;

    @Override
    public int initLayoutView() {
        return R.layout.activity_audio;
    }

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
                audioFilePathView.setText("录音文件路径：\r\n" + filePath);
                if (!TextUtils.isEmpty(filePath)) {
                    audioPlayView.setAudioUrl(filePath);
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initEvent() {
        recodeAudioButton.setOnTouchListener(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                recodeAudioButton.animate().scaleX(0.75f).scaleY(0.75f).setDuration(100).start();
                popWindow.showAtLocation(parentLayout, Gravity.CENTER, 0, 0);
                audioRecodeHelper.startRecordAudio();
                break;
            case MotionEvent.ACTION_UP:
                audioRecodeHelper.stopRecordAudio();//结束录音（保存录音文件）
                popWindow.dismiss();
                recodeAudioButton.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioPlayView != null) {
            audioPlayView.release();
        }
    }
}
