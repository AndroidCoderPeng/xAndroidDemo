package com.example.mutidemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mutidemo.R;
import com.example.mutidemo.bean.ResultBean;

import java.util.List;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/25 22:06
 */
public class PictureAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<ResultBean.CategoryBean.ListBean> mItemList;
    private LayoutInflater inflater;
    private OnItemClickListener mOnItemClickListener;

    public PictureAdapter(Context mContext, List<ResultBean.CategoryBean.ListBean> list) {
        this.context = mContext;
        this.mItemList = list;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PictureViewHolder(inflater.inflate(R.layout.item_picture_recyclerview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        PictureViewHolder itemHolder = (PictureViewHolder) holder;
        itemHolder.bindHolder(mItemList.get(position));
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });
        }
    }

    class PictureViewHolder extends RecyclerView.ViewHolder {

        private ImageView pictureView;
        private TextView pictureTitle;

        private PictureViewHolder(View itemView) {
            super(itemView);
            pictureView = itemView.findViewById(R.id.pictureView);
            pictureTitle = itemView.findViewById(R.id.pictureTitle);
        }

        void bindHolder(ResultBean.CategoryBean.ListBean listBean) {
            Glide.with(context).load(listBean.getChildPicture()).apply(RequestOptions.placeholderOf(R.mipmap.noimage)).into(pictureView);
            pictureTitle.setText(listBean.getChildTitle());
        }
    }

    /**
     * RecyclerView item 无内置点击事件，自定义一个接口实现点击事件
     */
    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnNewsItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
