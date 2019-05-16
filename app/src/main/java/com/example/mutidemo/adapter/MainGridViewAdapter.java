package com.example.mutidemo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.viewholder.MainGridViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2018/3/10.
 */

public class MainGridViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mItemlist;
    private final LayoutInflater inflater;

    public MainGridViewAdapter(Context mContext, List<String> list) {
        this.mContext = mContext;
        this.mItemlist = list;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mItemlist == null ? 0 : mItemlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mItemlist == null ? 0 : mItemlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MainGridViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_gridview, null);
            holder = new MainGridViewHolder();
            holder.mMainTextView = (TextView) convertView.findViewById(R.id.mMainTextView);
            holder.mLinearLayout = (LinearLayout) convertView.findViewById(R.id.mLinearLayout);
            convertView.setTag(holder);
        } else {
            holder = (MainGridViewHolder) convertView.getTag();
        }
        holder.mMainTextView.setText(mItemlist.get(position));
        if (position % 2 == 0) {
            holder.mLinearLayout.setBackgroundColor(Color.rgb(255, 255, 0));

        } else {
            holder.mLinearLayout.setBackgroundColor(Color.rgb(0, 255, 255));
        }
        return convertView;
    }
}
