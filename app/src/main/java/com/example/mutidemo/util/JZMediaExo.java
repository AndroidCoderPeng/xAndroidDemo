package com.example.mutidemo.util;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;

import com.example.mutidemo.R;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.TracksInfo;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoSize;

import org.jetbrains.annotations.NotNull;

import cn.jzvd.JZMediaInterface;
import cn.jzvd.Jzvd;

public class JZMediaExo extends JZMediaInterface implements Player.Listener {

    private static final String TAG = "JZMediaExo";
    private ExoPlayer exoPlayer;
    private long previousSeek = 0;
    private Runnable callback;

    public JZMediaExo(Jzvd jzvd) {
        super(jzvd);
    }

    @Override
    public void start() {
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void prepare() {
        Context context = jzvd.getContext();
        release();
        mMediaHandlerThread = new HandlerThread("JZVD");
        mMediaHandlerThread.start();
        mMediaHandler = new Handler(context.getMainLooper());//主线程还是非主线程，就在这里
        handler = new Handler();
        mMediaHandler.post(() -> {
            AdaptiveTrackSelection.Factory factory = new AdaptiveTrackSelection.Factory();
            TrackSelector trackSelector = new DefaultTrackSelector(context, factory);

            LoadControl loadControl = new DefaultLoadControl.Builder()
                    .setAllocator(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
                    .setBufferDurationsMs(360000, 600000, 1000, 5000)
                    .setPrioritizeTimeOverSizeThresholds(false)
                    .setTargetBufferBytes(C.LENGTH_UNSET)
                    .build();

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(context).build();
            // 2. Create the player

            RenderersFactory renderersFactory = new DefaultRenderersFactory(context);
            exoPlayer = new ExoPlayer.Builder(context, renderersFactory)
                    .setTrackSelector(trackSelector)
                    .setLoadControl(loadControl)
                    .setBandwidthMeter(bandwidthMeter)
                    .build();
            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, context.getResources().getString(R.string.app_name)));

            String currUrl = jzvd.jzDataSource.getCurrentUrl().toString();
            MediaItem mediaItem = new MediaItem.Builder().setUri(Uri.parse(currUrl)).build();
            MediaSource videoSource;
            if (currUrl.contains(".m3u8")) {
                videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
                //addEventListener 这里只有两个参数都要传入值才可以成功设置
                // 否者会被断言 Assertions.checkArgument(handler != null && eventListener != null);
                // 并且报错  IllegalArgumentException()  所以不需要添加监听器时 注释掉
                //      videoSource .addEventListener( handler, null);
            } else {
                videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
            }
            exoPlayer.addListener(this);
            Log.d(TAG, "URL Link = " + currUrl);

            exoPlayer.addListener(this);
            boolean isLoop = jzvd.jzDataSource.looping;
            if (isLoop) {
                exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
            } else {
                exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
            }
            exoPlayer.setMediaSource(videoSource);
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(true);
            callback = new onBufferingUpdate();
            if (jzvd.textureView != null) {
                SurfaceTexture surfaceTexture = jzvd.textureView.getSurfaceTexture();
                if (surfaceTexture != null) {
                    exoPlayer.setVideoSurface(new Surface(surfaceTexture));
                }
            }
        });
    }

    @Override
    public void onVideoSizeChanged(VideoSize videoSize) {
        handler.post(() -> jzvd.onVideoSizeChanged(videoSize.width, videoSize.height));
    }

    @Override
    public void onRenderedFirstFrame() {

    }

    @Override
    public void pause() {
        exoPlayer.setPlayWhenReady(false);
    }

    @Override
    public boolean isPlaying() {
        return exoPlayer.getPlayWhenReady();
    }

    @Override
    public void seekTo(long time) {
        if (exoPlayer == null) {
            return;
        }
        if (time != previousSeek) {
            if (time >= exoPlayer.getBufferedPosition()) {
                jzvd.onStatePreparingPlaying();
            }
            exoPlayer.seekTo(time);
            previousSeek = time;
            jzvd.seekToInAdvance = time;
        }
    }

    @Override
    public void release() {
        if (mMediaHandler != null && mMediaHandlerThread != null && exoPlayer != null) {
            HandlerThread tmpHandlerThread = mMediaHandlerThread;
            ExoPlayer tmpMediaPlayer = exoPlayer;
            JZMediaInterface.SAVED_SURFACE = null;
            mMediaHandler.post(() -> {
                tmpMediaPlayer.release();//release就不能放到主线程里，界面会卡顿
                tmpHandlerThread.quit();
            });
            exoPlayer = null;
        }
    }

    @Override
    public long getCurrentPosition() {
        if (exoPlayer != null)
            return exoPlayer.getCurrentPosition();
        else return 0;
    }

    @Override
    public long getDuration() {
        if (exoPlayer != null)
            return exoPlayer.getDuration();
        else return 0;
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        exoPlayer.setVolume(leftVolume);
        exoPlayer.setVolume(rightVolume);
    }

    @Override
    public void setSpeed(float speed) {
        PlaybackParameters playbackParameters = new PlaybackParameters(speed, 1.0F);
        exoPlayer.setPlaybackParameters(playbackParameters);
    }

    @Override
    public void onTimelineChanged(final Timeline timeline, final int reason) {

    }

    @Override
    public void onTracksInfoChanged(TracksInfo tracksInfo) {

    }

    @Override
    public void onIsLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayWhenReadyChanged(final boolean playWhenReady, final int playbackState) {
        Log.d(TAG, "onPlayerStateChanged" + playbackState + "/ready=" + playWhenReady);
        handler.post(() -> {
            switch (playbackState) {
                case Player.STATE_BUFFERING: {
                    jzvd.onStatePreparingPlaying();
                    handler.post(callback);
                }
                break;
                case Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST: {
                    if (playWhenReady) {
                        jzvd.onStatePlaying();
                    }
                }
                break;
                case Player.PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM: {
                    jzvd.onCompletion();
                }
                break;
            }
        });
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(PlaybackException error) {
        Log.d(TAG, "onPlayerError" + error.toString());
        handler.post(() -> jzvd.onError(1000, 1000));
    }

    @Override
    public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
        handler.post(() -> jzvd.onSeekComplete());
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void setSurface(Surface surface) {
        if (exoPlayer != null) {
            exoPlayer.setVideoSurface(surface);
        } else {
            Log.d("AGVideo", "exoPlayer为空");
        }
    }

    @Override
    public void onSurfaceTextureAvailable(@NotNull SurfaceTexture surface, int width, int height) {
        if (SAVED_SURFACE == null) {
            SAVED_SURFACE = surface;
            prepare();
        } else {
            jzvd.textureView.setSurfaceTexture(SAVED_SURFACE);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NotNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NotNull SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NotNull SurfaceTexture surface) {

    }

    private class onBufferingUpdate implements Runnable {
        @Override
        public void run() {
            if (exoPlayer != null) {
                final int percent = exoPlayer.getBufferedPercentage();
                handler.post(() -> jzvd.setBufferProgress(percent));
                if (percent < 100) {
                    handler.postDelayed(callback, 300);
                } else {
                    handler.removeCallbacks(callback);
                }
            }
        }
    }
}