package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.example.mutidemo.R;
import com.pengxh.app.multilib.base.NormalActivity;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;

public class ImgAutoFitActivity extends NormalActivity {

    @BindView(R.id.mAutoImg)
    ImageView mAutoImg;


    private String imgURL = "https://read.html5.qq.com/image?imageUrl=http://abco1.heibaimanhua.com/wp-content/uploads/2018/11/20181102_5bdc700f9ec94.jpg&src=share";
    private int screenWidth;
    private int screenHeight;

    @Override
    public void initView() {
        setContentView(R.layout.activity_img_autofit);
    }

    @Override
    public void init() {
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        Log.d(this.getClass().getSimpleName(), "屏幕宽度=====>" + screenWidth + "，屏幕的高度是=====>" + screenHeight);
    }

    @Override
    public void initEvent() {
//        Picasso.with(this).load(imgURL).into(new Target() {
//            @Override
//            /**
//             * 只适用于小于1M的网络图片，大于1M的图片就无法显示
//             * */
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                int height = bitmap.getHeight();
//                Picasso.with(ImgAutoFitActivity.this)
//                        .load(imgURL)
//                        .resize(screenWidth, height)
//                        .into(mAutoImg);
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable) {
//
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//            }
//        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = getBitmap(imgURL);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = bitmap;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    int height = bitmap.getHeight();
                    Picasso.with(ImgAutoFitActivity.this)
                            .load(imgURL)
                            .resize(screenWidth, height)
                            .into(mAutoImg);
                    break;
                default:
                    break;
            }
        }
    };

    //TODO 加载图片
    public Bitmap getBitmap(String url) {
        Bitmap bitmap = null;
        try {
            URL imgurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) imgurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
