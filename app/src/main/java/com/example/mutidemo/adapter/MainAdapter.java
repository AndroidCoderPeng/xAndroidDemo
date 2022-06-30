package com.example.mutidemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mutidemo.R;
import com.pengxh.androidx.lite.utils.ColorUtil;

import java.util.List;

/**
 * Created by Administrator on 2018/3/10.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainRecyclerViewHolder> {

    private final List<String> mItemList;
    private final LayoutInflater inflater;
    private OnItemClickListener mOnItemClickListener;


    public MainAdapter(Context mContext, List<String> mItemList) {
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
        return new MainRecyclerViewHolder(inflater.inflate(R.layout.item_main_rv, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainRecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bindHolder(mItemList.get(position));
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });
        }
    }

    static class MainRecyclerViewHolder extends RecyclerView.ViewHolder {

        private final TextView mMainTextView;

        private MainRecyclerViewHolder(View itemView) {
            super(itemView);
            mMainTextView = itemView.findViewById(R.id.mMainTextView);
        }

        void bindHolder(String title) {
            mMainTextView.setText(title);
            mMainTextView.setBackgroundColor(ColorUtil.randomColor());
        }
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