package com.example.mutidemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mutidemo.R;

import java.util.List;

/**
 * Created by Administrator on 2018/7/20.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<String> mItemList;
    private LayoutInflater inflater;

    public MyRecyclerViewAdapter(Context mContext, List<String> mItemList) {
        this.mContext = mContext;
        this.mItemList = mItemList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public MyRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyRecyclerViewHolder(inflater.inflate(R.layout.item_recylceview, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MyRecyclerViewHolder itemHolder = (MyRecyclerViewHolder) holder;
        itemHolder.bindHolder(mItemList.get(position));
    }

    class MyRecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvTitle;

        private MyRecyclerViewHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.mTvTitle);
        }

        public void bindHolder(String title) {
            mTvTitle.setText(title);
        }
    }
}