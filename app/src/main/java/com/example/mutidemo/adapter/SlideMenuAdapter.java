package com.example.mutidemo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.viewholder.SlideViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/9.
 */

public class SlideMenuAdapter extends BaseAdapter {
    private List<String> list;
    private Context context;

    public SlideMenuAdapter(Context context) {
        this.context = context;
        list = new ArrayList();
        list.add("糊涂账");
        list.add("月消费分析");
        list.add("年消费分析");
        list.add("账单设置");
        list.add("月光宝盒");
        list.add("隐私保护");
        list.add("数据备份");
        list.add("互动交流");
        list.add("关于我们");
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SlideViewHolder viewHolder;
        String item = list.get(position);
        if (convertView == null) {
            viewHolder = new SlideViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_slidemenu,
                    null);
            viewHolder.tv_item = (TextView) convertView
                    .findViewById(R.id.tv_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SlideViewHolder) convertView.getTag();
        }
        viewHolder.tv_item.setText(item);
        if (position == selectItem) {
            convertView.setBackgroundColor(Color.RED);
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }
        return convertView;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    private int selectItem = -1;
}
