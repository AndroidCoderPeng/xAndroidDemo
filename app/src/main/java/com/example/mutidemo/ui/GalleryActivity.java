package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.PhotoViewAdapter;
import com.example.mutidemo.util.Constant;
import com.example.mutidemo.widget.BlurBitmapUtils;
import com.example.mutidemo.widget.CardScaleHelper;
import com.example.mutidemo.widget.ViewSwitchUtils;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/26 14:15
 */
public class GalleryActivity extends BaseNormalActivity {

    @BindView(R.id.blurView)
    ImageView mBlurView;
    @BindView(R.id.galleryView)
    RecyclerView galleryView;

    private List<String> mList = new ArrayList<>();
    private CardScaleHelper mCardScaleHelper;
    private Runnable mBlurRunnable;
    private int mLastPos = -1;

    @Override
    public void initView() {
        setContentView(R.layout.activity_gallery);
    }

    @Override
    public void initData() {
        mList = Constant.IMAGE_URL;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        galleryView.setLayoutManager(linearLayoutManager);
        galleryView.setAdapter(new PhotoViewAdapter(this, mList));
        mCardScaleHelper = new CardScaleHelper();
        mCardScaleHelper.setCurrentItemPos(2);
        mCardScaleHelper.attachToRecyclerView(galleryView);

        initBlurBackground();
    }

    private void initBlurBackground() {
        galleryView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    notifyBackgroundChange();
                }
            }
        });
        notifyBackgroundChange();
    }

    private void notifyBackgroundChange() {
        if (mLastPos == mCardScaleHelper.getCurrentItemPos()) {
            return;
        }
        mLastPos = mCardScaleHelper.getCurrentItemPos();
        String itemURL = mList.get(mCardScaleHelper.getCurrentItemPos());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                message.what = 1;
                message.obj = ImageUtil.getBitmap(itemURL);
                handler.sendMessage(message);
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Bitmap bitmap = (Bitmap) msg.obj;

                mBlurView.removeCallbacks(mBlurRunnable);
                mBlurRunnable = new Runnable() {
                    @Override
                    public void run() {
                        ViewSwitchUtils.startSwitchBackgroundAnim(mBlurView, BlurBitmapUtils.getBlurBitmap(mBlurView.getContext(), bitmap, 15));
                    }
                };
                mBlurView.postDelayed(mBlurRunnable, 500);
            }
        }
    };

    @Override
    public void initEvent() {

    }
}
