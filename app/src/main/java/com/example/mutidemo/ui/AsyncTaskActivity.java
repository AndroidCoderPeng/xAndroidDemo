package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.mutidemo.R;
import com.example.mutidemo.util.Constant;
import com.example.mutidemo.widget.CustomProgressBar;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.ToastUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/11/26.
 */

public class AsyncTaskActivity extends BaseNormalActivity {


    @BindView(R.id.mBtn_Async)
    Button mBtnAsync;
    @BindView(R.id.mImageView)
    ImageView mImageView;
    @BindView(R.id.mProgressBar)
    CustomProgressBar mProgressBar;

    @Override
    public void initView() {
        setContentView(R.layout.activity_asynctask);
    }

    @Override
    public void init() {

    }

    @Override
    public void initEvent() {
        mBtnAsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTaskTogetPicture().execute(Constant.mImageUrl_1);
            }
        });
    }

    /**
     * String url
     * void   进度
     * Bitmap 返回值类型
     */
    @SuppressLint("StaticFieldLeak")
    class AsyncTaskTogetPicture extends AsyncTask<String, Integer, Bitmap> {

        //第一阶段————准备阶段让进度条显示
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setProgress(0);
            mProgressBar.setProgressDesc("当前进度");
            mProgressBar.setProgressColor(Color.parseColor("#0094ff"));
        }

        //第二阶段——网络获取图片
        @Override
        protected Bitmap doInBackground(String... params) {
            //从可变参数的数组中拿到第0位的图片地址
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setConnectTimeout(20000);
                int code = connection.getResponseCode();
                if (code == 200) {
                    //为了显示进度条这里使用,字节数组输出流
                    InputStream is = connection.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int length;
                    int progress = 0;    //进度
                    int count = connection.getContentLength();  //获取内容长度
                    Log.d("PXH", "doInBackground: 图片大小是 >>> " + count);
                    mProgressBar.setMaxProgress(count);

                    byte[] bytes = new byte[5];
                    while ((length = is.read(bytes)) != -1) {
                        progress += length;    //进度累加
                        if (count == 0) {
                            publishProgress(-1);
                        } else {
                            //进度值改变通知
                            publishProgress(progress);
                        }
                        if (isCancelled()) {//如果取消了任务 就不执行
                            return null;
                        }
                        bos.write(bytes, 0, length);
                    }
                    return BitmapFactory.decodeByteArray(bos.toByteArray(), 0, bos.size());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        //下载过程之中更新进度条
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            if (progress != -1) {
                mProgressBar.setProgress(progress);
            }
        }

        //第三阶段，拿到结果bitmap图片，更新ui
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mProgressBar.setOnFinishedListener(new CustomProgressBar.OnFinishedListener() {
                @Override
                public void onFinish() {
                    ToastUtil.showBeautifulToast("加载完成!", 3);
                }
            });
            mProgressBar.setVisibility(View.GONE);
            mImageView.setImageBitmap(bitmap);
        }
    }
}