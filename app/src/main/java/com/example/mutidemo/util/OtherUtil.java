package com.example.mutidemo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import static com.pengxh.app.multilib.utils.DensityUtil.getScreenWidth;

/**
 * Created by Administrator on 2019/5/26.
 */

public class OtherUtil {
    /**
     * 解决ScrollView嵌套另一个可滑动的View时，高度异常的问题
     */
    public static void measureViewHeight(Context mContext, GridView gridView) {
        ListAdapter adapter = gridView.getAdapter();
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        if (adapter == null) {
            return;
        }
        int totalHeight = 0;
        View view;
        for (int i = 0; i < adapter.getCount(); i++) {
            view = adapter.getView(i, null, gridView);
            int i1 = View.MeasureSpec.makeMeasureSpec(getScreenWidth(mContext), View.MeasureSpec.EXACTLY);
            int i2 = View.MeasureSpec.makeMeasureSpec(i1, View.MeasureSpec.UNSPECIFIED);
            view.measure(i1, i2);
            totalHeight += view.getMeasuredHeight();
        }
        params.height = totalHeight + (gridView.getLayoutDirection() * (adapter.getCount() - 1));
        gridView.setLayoutParams(params);
    }

    /**
     * 图片背景切换动画帮助类
     * <p>
     * Created by jameson on 9/3/16.
     */

    public static void startSwitchBackgroundAnim(ImageView view, Bitmap bitmap) {
        Drawable oldDrawable = view.getDrawable();
        Drawable oldBitmapDrawable;
        TransitionDrawable oldTransitionDrawable = null;
        if (oldDrawable instanceof TransitionDrawable) {
            oldTransitionDrawable = (TransitionDrawable) oldDrawable;
            oldBitmapDrawable = oldTransitionDrawable.findDrawableByLayerId(oldTransitionDrawable.getId(1));
        } else if (oldDrawable instanceof BitmapDrawable) {
            oldBitmapDrawable = oldDrawable;
        } else {
            oldBitmapDrawable = new ColorDrawable(0xffc2c2c2);
        }

        if (oldTransitionDrawable == null) {
            oldTransitionDrawable = new TransitionDrawable(new Drawable[]{oldBitmapDrawable, new BitmapDrawable(bitmap)});
            oldTransitionDrawable.setId(0, 0);
            oldTransitionDrawable.setId(1, 1);
            oldTransitionDrawable.setCrossFadeEnabled(true);
            view.setImageDrawable(oldTransitionDrawable);
        } else {
            oldTransitionDrawable.setDrawableByLayerId(oldTransitionDrawable.getId(0), oldBitmapDrawable);
            oldTransitionDrawable.setDrawableByLayerId(oldTransitionDrawable.getId(1), new BitmapDrawable(bitmap));
        }
        oldTransitionDrawable.startTransition(1000);
    }
}