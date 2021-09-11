package com.example.mutidemo.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mutidemo.R;
import com.pengxh.app.multilib.utils.SizeUtil;

import java.util.ArrayList;
import java.util.List;

public class NineGridImageAdapter extends RecyclerView.Adapter<NineGridImageAdapter.ItemViewHolder> {

    private static final int mCountLimit = 9;
    private Context mContext;
    private List<String> mImageData = new ArrayList<>();

    public NineGridImageAdapter(Context context) {
        this.mContext = context;
    }

    public void setupImage(List<String> images) {
        this.mImageData = images;
        notifyDataSetChanged();
    }

    public void deleteImage(int position) {
        if (mImageData.size() != 0) {
            mImageData.remove(position);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(mContext);
        int screenWidth = SizeUtil.getScreenWidth(mContext);
        int margins = SizeUtil.dp2px(mContext, 3);
        int itemSize = (screenWidth - 6 * margins) / 3;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(itemSize, itemSize);
        params.setMargins(margins, margins, margins, margins);
        params.gravity = Gravity.CENTER;
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(params);
        return new ItemViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        if (position == getItemCount() - 1 && mImageData.size() < mCountLimit) {
            holder.imageView.setImageResource(R.drawable.ic_add_pic);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //添加图片
                    mOnItemClickListener.onAddImageClick();
                }
            });
        } else {
            Glide.with(mContext)
                    .load(mImageData.get(position))
                    .into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击操作，查看大图
                    mOnItemClickListener.onItemClick(position);
                }
            });
            // 长按监听
            holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //长按删除
                    mOnItemClickListener.onItemLongClick(v, position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        // 满3张图就不让其添加新图
        if (mImageData != null && mImageData.size() >= mCountLimit) {
            return mCountLimit;
        } else {
            return mImageData == null ? 1 : mImageData.size() + 1;
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onAddImageClick();

        void onItemClick(int position);

        void onItemLongClick(View view, int position);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        private ItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
}