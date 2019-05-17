package com.example.mutidemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mutidemo.R;

import java.util.List;

/**
 * Created by Administrator on 2018/7/20.
 */

public class HorizListViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mHorzItemlist;
    private LayoutInflater inflater;

    public HorizListViewAdapter(Context mContext, List<String> mHorzItemlist) {
        this.mContext = mContext;
        this.mHorzItemlist = mHorzItemlist;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mHorzItemlist == null ? 0 : mHorzItemlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mHorzItemlist == null ? 0 : mHorzItemlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HorizViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_horiz_listview, null);
            holder = new HorizViewHolder();
            holder.mImageView = (ImageView) convertView.findViewById(R.id.mImageView);
            convertView.setTag(holder);
        } else {
            holder = (HorizViewHolder) convertView.getTag();
        }
        Glide.with(mContext).load(mHorzItemlist.get(position)).into(holder.mImageView);
        return convertView;
    }

    class HorizViewHolder {
        ImageView mImageView;
    }
}
