package com.example.mutidemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mutidemo.R;
import com.example.mutidemo.widget.CardAdapterHelper;
import com.pengxh.app.multilib.widget.EasyToast;

import java.util.List;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/25 22:06
 */
public class PhotoViewAdapter extends RecyclerView.Adapter<PhotoViewAdapter.ViewHolder> {

    private Context context;
    private List<String> mList;
    private CardAdapterHelper mCardAdapterHelper = new CardAdapterHelper();

    public PhotoViewAdapter(Context context, List<String> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_photo_recyclerview, parent, false);
        mCardAdapterHelper.onCreateViewHolder(parent, itemView);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mCardAdapterHelper.onBindViewHolder(holder.itemView, position, getItemCount());
        Glide.with(context).load(mList.get(position)).placeholder(R.mipmap.noimage).into(holder.photoView);
        holder.photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = mList.get(position);
                Log.d("PhotoViewAdapter", "onClick: " + s);
                EasyToast.showToast(s, EasyToast.SUCCESS);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photoView;

        ViewHolder(final View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.photoView);
        }
    }
}
