package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.mutidemo.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.gyf.immersionbar.ImmersionBar;
import com.pengxh.app.multilib.utils.StatusBarColorHelper;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/3/6 12:49
 */
public class BigImageViewActivity extends AppCompatActivity {

    @BindView(R.id.imagePagerView)
    ViewPager imagePagerView;
    @BindView(R.id.leftBackView)
    ImageView leftBackView;
    @BindView(R.id.pageNumberView)
    TextView pageNumberView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_picture);
        ButterKnife.bind(this);
        setupTopBarLayout();
        initEvent();

    }

    private void setupTopBarLayout() {
        StatusBarColorHelper.setColor(this, ContextCompat.getColor(this, R.color.black));
        ImmersionBar.with(this).statusBarDarkFont(false).init();
        leftBackView.setOnClickListener(v -> finish());
    }

    @SuppressLint("SetTextI18n")
    private void initEvent() {
        int index = getIntent().getIntExtra("index", 0);
        ArrayList<String> urls = getIntent().getStringArrayListExtra("images");
        if (urls == null || urls.size() == 0) {
            return;
        }
        pageNumberView.setText("(" + (index + 1) + "/" + urls.size() + ")");
        imagePagerView.setAdapter(new BigImageAdapter(this, urls));
        imagePagerView.setCurrentItem(index);
        imagePagerView.setOffscreenPageLimit(2);//设置预加载数量
        imagePagerView.setPageMargin(QMUIDisplayHelper.dp2px(this, 1));//控制两幅图之间的间距
        imagePagerView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageNumberView.setText("(" + (position + 1) + "/" + urls.size() + ")");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class BigImageAdapter extends PagerAdapter {

        private Context context;
        private List<String> data;

        BigImageAdapter(@NotNull Context context, @NotNull List<String> imageList) {
            this.context = context;
            this.data = imageList;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
            return view == object;
        }

        @NotNull
        @Override
        public Object instantiateItem(@NotNull ViewGroup container, int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_big_picture, container, false);
            PhotoView photoView = view.findViewById(R.id.photoView);
            Glide.with(context).load(data.get(position)).into(photoView);
            photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            container.addView(view);
            //点击大图取消预览
            photoView.setOnClickListener(v -> BigImageViewActivity.this.finish());
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
            container.removeView((View) object);
        }
    }

    //设置切换动画
    public static class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        //自由控制缩放比例
        private static final float MAX_SCALE = 1f;
        private static final float MIN_SCALE = 0.85f;//0.85f

        @Override
        public void transformPage(@NotNull View page, float position) {

            if (position <= 1) {
                float scaleFactor = MIN_SCALE + (1 - Math.abs(position)) * (MAX_SCALE - MIN_SCALE);
                page.setScaleX(scaleFactor);
                if (position > 0) {
                    page.setTranslationX(-scaleFactor * 2);
                } else if (position < 0) {
                    page.setTranslationX(scaleFactor * 2);
                }
                page.setScaleY(scaleFactor);
            } else {
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            }
        }
    }
}
