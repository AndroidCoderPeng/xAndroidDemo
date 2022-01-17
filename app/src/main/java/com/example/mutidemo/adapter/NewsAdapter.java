package com.example.mutidemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mutidemo.R;
import com.example.mutidemo.bean.NewsBean;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;

import java.util.List;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/21 23:04
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsRecyclerViewHolder> {

    private final Context context;
    private final List<NewsBean.ResultBeanX.ResultBean.ListBean> mItemList;
    private final LayoutInflater inflater;
    private OnNewsItemClickListener mOnItemClickListener;

    public NewsAdapter(Context mContext, List<NewsBean.ResultBeanX.ResultBean.ListBean> list) {
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
    public NewsRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsRecyclerViewHolder(inflater.inflate(R.layout.item_news_recyclerview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsRecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
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

    class NewsRecyclerViewHolder extends RecyclerView.ViewHolder {

        private final QMUIRadiusImageView newsPicture;
        private final TextView newsTitle;
        private final TextView newsSrc;
        private final TextView newsTime;

        private NewsRecyclerViewHolder(View itemView) {
            super(itemView);
            newsPicture = itemView.findViewById(R.id.newsPicture);
            newsTitle = itemView.findViewById(R.id.newsTitle);
            newsSrc = itemView.findViewById(R.id.newsSrc);
            newsTime = itemView.findViewById(R.id.newsTime);
        }

        void bindHolder(NewsBean.ResultBeanX.ResultBean.ListBean listBean) {
            String img = listBean.getPic();
            if (img == null || img.equals("") || img.endsWith(".gif")) {
                newsPicture.setVisibility(View.GONE);
            } else {
                Glide.with(context).load(img).apply(RequestOptions.placeholderOf(R.mipmap.noimage)).into(newsPicture);
            }
            newsTitle.setText(listBean.getTitle());
            newsSrc.setText(listBean.getSrc());
            newsTime.setText(listBean.getTime());
        }
    }

    /**
     * RecyclerView item 无内置点击事件，自定义一个接口实现点击事件
     */
    public interface OnNewsItemClickListener {
        void onClick(int position);
    }

    public void setOnNewsItemClickListener(OnNewsItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
