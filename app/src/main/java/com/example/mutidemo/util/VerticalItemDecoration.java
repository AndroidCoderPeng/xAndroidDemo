package com.example.mutidemo.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mutidemo.util.callback.DecorationCallback;
import com.pengxh.app.multilib.utils.DensityUtil;

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/9/27 22:47
 */
public class VerticalItemDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = "VerticalItemDecoration";
    private Context context;
    private Paint topLinePaint, bottomLinePaint;
    private TextPaint textPaint;
    private DecorationCallback callback;
    private final int topGap;

    public VerticalItemDecoration(Context ctx, DecorationCallback decorationCallback) {
        this.context = ctx;
        this.callback = decorationCallback;

        topLinePaint = new Paint();
        topLinePaint.setAntiAlias(true);
        topLinePaint.setColor(Color.parseColor("#F1F1F1"));

        bottomLinePaint = new Paint();
        bottomLinePaint.setAntiAlias(true);
        bottomLinePaint.setColor(Color.LTGRAY);

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(80);
        textPaint.setColor(Color.GRAY);
        textPaint.setTextAlign(Paint.Align.LEFT);
        topGap = DensityUtil.dp2px(ctx, 30);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int pos = parent.getChildAdapterPosition(view);
        long groupTag = callback.getGroupTag(pos);
        if (groupTag < 0) return;
        if (pos == 0 || isFirstInGroup(pos)) {//同组的第一个才添加padding
            outRect.top = topGap;
        } else {
            outRect.top = 0;
        }
    }

    private boolean isFirstInGroup(int pos) {
        if (pos == 0) {
            return true;
        } else {
            long prevGroupId = callback.getGroupTag(pos - 1);
            long groupId = callback.getGroupTag(pos);
            return prevGroupId != groupId;
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int left = parent.getPaddingLeft() + DensityUtil.dp2px(context, 5);
        int right = parent.getWidth();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            long groupId = callback.getGroupTag(position);
            if (groupId < 0) return;
            String firstLetter = callback.getGroupFirstLetter(position).toUpperCase();
            if (position == 0 || isFirstInGroup(position)) {
                float top = view.getTop() - topGap;
                float bottom = view.getTop();
                c.drawRect(0, top, right, bottom, topLinePaint);//绘制灰色矩形
                c.drawText(firstLetter, left, bottom, textPaint);//绘制文本
            }
            //划分割线
            c.drawRect(DensityUtil.dp2px(context, 15), view.getBottom(), parent.getWidth(), view.getBottom() + 1, bottomLinePaint);
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int itemCount = state.getItemCount();
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth();

        long preGroupId, groupId = -1;
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);

            preGroupId = groupId;
            groupId = callback.getGroupTag(position);
            if (groupId < 0 || groupId == preGroupId) continue;

            String firstLetter = callback.getGroupFirstLetter(position).toUpperCase();
            if (TextUtils.isEmpty(firstLetter)) continue;

            int viewBottom = view.getBottom();
            float textY = Math.max(topGap, view.getTop());
            if (position + 1 < itemCount) { //下一个和当前不一样移动当前
                long nextGroupId = callback.getGroupTag(position + 1);
                if (nextGroupId != groupId && viewBottom < textY) {//组内最后一个view进入了header
                    textY = viewBottom;
                }
            }
            c.drawRect(left, textY - topGap, right, textY, topLinePaint);
            c.drawText(firstLetter, left, textY, textPaint);
        }
    }

    /**
     * 点击某个字母将RecyclerView滑动到item顶部
     */
    public static class TopSmoothScroller extends LinearSmoothScroller {
        public TopSmoothScroller(Context context) {
            super(context);
        }

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;
        }
    }
}