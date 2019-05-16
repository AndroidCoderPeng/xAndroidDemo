package com.example.mutidemo.ui;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mutidemo.R;
import com.example.mutidemo.widget.CustomProgressBar;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/11/26.
 */

public class AsyncTaskActivity extends BaseNormalActivity {

    @BindView(R.id.mBtn_Start)
    Button mBtnStart;
    @BindView(R.id.mCustomProgressBar)
    CustomProgressBar mCustomProgressBar;
    @BindView(R.id.mBtn_Async)
    Button mBtnAsync;
    @BindView(R.id.mImageView)
    ImageView mImageView;

    private int max;//进度条最大值

    @Override
    public void initView() {
        setContentView(R.layout.activity_asynctask);
    }

    @Override
    public void init() {

    }

    @Override
    public void initEvent() {
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 出发异步任务，必须在UI线程中执行
                /**
                 * @params 进度标尺=1
                 * @params 进度时间间隔=50
                 * */
                new MyAsyncTask().execute(1, 50);
                max = mCustomProgressBar.getMax();//获取进度条最大值，只能在UI线程中获取
            }
        });
        mBtnAsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyHttpAsyncTask().execute();
            }
        });
    }

    /**
     * AsyncTask是抽象类，必须创建子类继承他，然后实现方法
     * <p>
     * 为了更好理解，将方法执行的顺序按如下排放
     */
    class MyAsyncTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected void onPreExecute() {
            //TODO execute以后立刻执行此方法，用来标记UI
            mCustomProgressBar.setProgress(0);
            mBtnStart.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {
            //TODO 用来执行耗时操作，一定不能更新UI，否则ANR
            String ret;
            //对应于execute中的两个参数
            int bushu = params[0];
            int sleeptime = params[1];
            for (int i = 1; i <= max; i++) {
                //TODO 发送进度条进度
                publishProgress(i);
                SystemClock.sleep(sleeptime);
            }
            ret = "更新完毕...";
            return ret;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //TODO 用来更新异步任务进度
            int p = mCustomProgressBar.getMax() / 100 * values[0];//值越大越慢
            Log.d("onProgressUpdate", "------------------" + p);
            mCustomProgressBar.setProgress(p);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            //TODO 将异步任务的结果作为Result返回，更新
            mBtnStart.setEnabled(true);
            Toast.makeText(AsyncTaskActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    }

    class MyHttpAsyncTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            mBtnAsync.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Drawable drawable = null;
            try {
                String img = "http://f.hiphotos.baidu.com/baike/pic/item/503d269759ee3d6dd88f2ebf48166d224f4ade7d.jpg";
                URL url = new URL(img);
                URLConnection connection = url.openConnection();
                connection.connect();
                InputStream is = connection.getInputStream();
                drawable = Drawable.createFromStream(is, null);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return drawable;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mBtnAsync.setEnabled(true);
            mImageView.setImageDrawable((Drawable) o);
        }
    }
}