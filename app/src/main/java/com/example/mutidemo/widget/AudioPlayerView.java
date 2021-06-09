package com.example.mutidemo.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.mutidemo.R;

import java.io.IOException;

public class AudioPlayerView extends AppCompatTextView {

    private static final int[] drawables = new int[]{R.drawable.ic_audio_icon1, R.drawable.ic_audio_icon2, R.drawable.ic_audio_icon3};
    private MediaPlayer mediaPlayer;
    /**
     * 在非初始化状态下调用setDataSource  会抛出IllegalStateException异常
     */
    private boolean hasPrepared = false;
    private String mUrl;
    private int index = 0;
    private Handler audioAnimationHandler;
    private Runnable animationRunnable;

    public AudioPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } catch (Exception e) {
            Log.e("mediaPlayer", " init error", e);
        }
        if (mediaPlayer != null) {
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    hasPrepared = true;
                    setText(getAudioDuration());
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mp.reset();
                    return false;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopAnimation();
                }
            });
        }
        setViewClick();
    }

    public String getAudioDuration() {
        int duration = mediaPlayer.getDuration();
        if (duration == -1) {
            return "";
        } else {
            int sec = duration / 1000;
            int m = sec / 60;
            int s = sec % 60;
            return m + ":" + s;
        }
    }

    public void setAudioUrl(String url) {
        this.mUrl = url;
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
        } catch (IOException | IllegalStateException e) {
            Log.e("mediaPlayer", " set dataSource error", e);
        }
    }

    /**
     * 用于需要设置不同的dataSource
     * 二次setDataSource的时候需要reset 将MediaPlayer恢复到Initialized状态
     *
     * @param url
     */
    public void resetUrl(String url) {
        if (TextUtils.isEmpty(mUrl) || hasPrepared) {
            mediaPlayer.reset();
        }
        setAudioUrl(url);
    }

    private void startAnimation() {
        if (audioAnimationHandler == null) {
            audioAnimationHandler = new Handler();
        }
        if (animationRunnable == null) {
            animationRunnable = new Runnable() {
                @Override
                public void run() {
                    audioAnimationHandler.postDelayed(this, 200);
                    setDrawable(drawables[index % 3]);
                    index++;
                }
            };
        }
        audioAnimationHandler.removeCallbacks(animationRunnable);
        audioAnimationHandler.postDelayed(animationRunnable, 200);
    }

    private void stopAnimation() {
        setDrawable(drawables[2]);
        if (audioAnimationHandler != null) {
            audioAnimationHandler.removeCallbacks(animationRunnable);
        }
    }

    //暂时只能设置在左边，后期改为可设置方向
    private void setDrawable(@DrawableRes int id) {
        Drawable drawable = getResources().getDrawable(id);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        setCompoundDrawables(drawable, null, null, null);
    }

    private void setViewClick() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    stopAnimation();
                } else {
                    mediaPlayer.seekTo(0);
                    startAnimation();
                    mediaPlayer.start();
                }
            }
        });
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (audioAnimationHandler != null) {
            audioAnimationHandler.removeCallbacks(animationRunnable);
        }
    }
}
