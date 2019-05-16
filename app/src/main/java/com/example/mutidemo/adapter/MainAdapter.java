package com.example.mutidemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mutidemo.R;

import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2018/3/10.
 */

public class MainAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<String> mItemList;
    private LayoutInflater inflater;
    private OnItemClickListener mOnItemClickListener;


    public MainAdapter(Context mContext, List<String> mItemList) {
        this.mContext = mContext;
        this.mItemList = mItemList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @NonNull
    @Override
    public MainRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainRecyclerViewHolder(inflater.inflate(R.layout.item_main_recyclerview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        MainRecyclerViewHolder itemHolder = (MainRecyclerViewHolder) holder;
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

    class MainRecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView mMainTextView;

        private MainRecyclerViewHolder(View itemView) {
            super(itemView);
            mMainTextView = itemView.findViewById(R.id.mMainTextView);
        }

        public void bindHolder(String title) {
            mMainTextView.setText(title);
            mMainTextView.setBackgroundColor(getRandomColor());
        }
    }

    private int getRandomColor() {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return Color.rgb(red, green, blue);
    }

    /**
     * RecyclerView item 无内置点击事件，自定义一个接口实现点击事件
     */
    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}