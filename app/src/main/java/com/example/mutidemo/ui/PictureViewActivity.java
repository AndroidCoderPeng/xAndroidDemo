package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.PhotoViewAdapter;
import com.example.mutidemo.bean.PhotoBean;
import com.example.mutidemo.util.HtmlParserHelper;
import com.example.mutidemo.util.HttpHelper;
import com.example.mutidemo.util.ImageUtil;
import com.example.mutidemo.util.OtherUtils;
import com.example.mutidemo.util.callback.BitmapCallBackListener;
import com.example.mutidemo.util.callback.HtmlParserCallBackListener;
import com.example.mutidemo.util.callback.PhotoParserCallBackListener;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.widget.EasyToast;
import com.pengxh.app.multilib.widget.gallery3d.BlurBitmapUtils;
import com.pengxh.app.multilib.widget.gallery3d.CardScaleHelper;
import com.pengxh.app.multilib.widget.gallery3d.ViewSwitchUtils;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/26 12:47
 */

public class PictureViewActivity extends BaseNormalActivity {

    @BindView(R.id.childTitleView)
    TextView childTitleView;
    @BindView(R.id.pictureBlurView)
    ImageView mBlurView;
    @BindView(R.id.pictureGalleryView)
    RecyclerView pictureGalleryView;
    @BindView(R.id.childNumberView)
    TextView childNumberView;

    private Context mContext = PictureViewActivity.this;
    private List<PhotoBean.Result> photoBeanList = new ArrayList<>();
    private CardScaleHelper mCardScaleHelper;
    private Runnable mBlurRunnable;
    private int mLastPos = -1;
    private int photoNumber;

    @Override
    public void initView() {
        setContentView(R.layout.activity_picture);
    }

    @Override
    public void initData() {
        childTitleView.setText(getIntent().getStringExtra("childTitle"));
        String childUrl = getIntent().getStringExtra("childUrl");

        OtherUtils.showProgressDialog(this, "数据加载中...");
        HttpHelper.captureHtmlData(childUrl, new HtmlParserCallBackListener() {
            @Override
            public void onParserDone(Document document) throws IOException {
                Message message = handler.obtainMessage();
                message.what = 10000;
                message.obj = document;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    public void initEvent() {

    }

    /**
     * 使用handler请求网络数据并在handleMessage里面处理返回操作
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10000:
                    Document document = (Document) msg.obj;
                    HtmlParserHelper.getPictureList(document, new PhotoParserCallBackListener() {
                        @Override
                        public void onPictureDone(PhotoBean photoBean) {
                            photoBeanList = photoBean.getList();
                            photoNumber = photoBean.getPhotoNumber();

                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                            pictureGalleryView.setLayoutManager(linearLayoutManager);
                            pictureGalleryView.setAdapter(new PhotoViewAdapter(mContext, photoBeanList));
                            mCardScaleHelper = new CardScaleHelper();
                            mCardScaleHelper.setCurrentItemPos(0);//从第一张图片开始
                            mCardScaleHelper.attachToRecyclerView(pictureGalleryView);

                            initBlurBackground();
                        }
                    });
                    break;
                case 10001:
                    EasyToast.showToast("获取数据失败", EasyToast.ERROR);
                    break;
                default:
                    break;
            }
            OtherUtils.hideProgressDialog();
        }
    };

    private void initBlurBackground() {
        pictureGalleryView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        childNumberView.setText((mLastPos + 1) + "/" + photoNumber);//页码

        String itemURL = photoBeanList.get(mCardScaleHelper.getCurrentItemPos()).getBigImageUrl();
        ImageUtil.obtainBitmap(itemURL, new BitmapCallBackListener() {
            @Override
            public void onSuccess(final Bitmap bitmap) {
                mBlurView.removeCallbacks(mBlurRunnable);
                mBlurRunnable = new Runnable() {
                    @Override
                    public void run() {
                        ViewSwitchUtils.startSwitchBackgroundAnim(mBlurView, BlurBitmapUtils.getBlurBitmap(mBlurView.getContext(), bitmap, 15));
                    }
                };
                mBlurView.postDelayed(mBlurRunnable, 500);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
