package com.example.mutidemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mutidemo.R;
import com.example.mutidemo.widget.HorizontalListView;

import java.util.List;

/**
 * Created by Administrator on 2018/7/20.
 */

public class MultiListViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mVerticItemlist;
    private List<String> mHorzItemlist;
    private LayoutInflater inflater;

    public MultiListViewAdapter(Context mContext, List<String> mVerticItemlist, List<String> mHorzItemlist) {
        this.mContext = mContext;
        this.mVerticItemlist = mVerticItemlist;
        this.mHorzItemlist = mHorzItemlist;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mVerticItemlist == null ? 0 : mVerticItemlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mVerticItemlist == null ? 0 : mVerticItemlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MutiViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_vertic_listview, null);
            holder = new MutiViewHolder();
            holder.mTextView = (TextView) convertView.findViewById(R.id.mTextView);
            holder.mHorizontalListView = (HorizontalListView) convertView.findViewById(R.id.mHorizontalListView);
            convertView.setTag(holder);
        } else {
            holder = (MutiViewHolder) convertView.getTag();
        }
        holder.mTextView.setText(mVerticItemlist.get(position));
        HorizListViewAdapter adapter = new HorizListViewAdapter(mContext, mHorzItemlist);
        holder.mHorizontalListView.setAdapter(adapter);
        return convertView;
    }

    class MutiViewHolder {
        TextView mTextView;
        HorizontalListView mHorizontalListView;
    }
}
