package com.example.mutidemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mutidemo.bean.WeatherBean;

import java.util.List;

public class AqiListViewAdapter extends BaseAdapter {

    private Context context;
    private List<WeatherBean.ResultBeanX.ResultBean.AqiBean> aqiList;
    private LayoutInflater inflater;

    public AqiListViewAdapter(Context context, List<WeatherBean.ResultBeanX.ResultBean.AqiBean> aqiList) {
        this.context = context;
        this.aqiList = aqiList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return aqiList == null ? 0 : aqiList.size();
    }

    @Override
    public Object getItem(int position) {
        return aqiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        AqiInfoHolder itemHolder;
//        if (convertView == null) {
//            convertView = inflater.inflate(R.layout.item_aqilist, null);
//            itemHolder = new AqiInfoHolder();
//            itemHolder.mAppName = convertView.findViewById(R.id.mAppName);
//            itemHolder.mAppVersion = convertView.findViewById(R.id.mAppVersion);
//            itemHolder.mAppIcon = convertView.findViewById(R.id.mAppIcon);
//            convertView.setTag(itemHolder);
//        } else {
//            itemHolder = (AqiInfoHolder) convertView.getTag();
//        }
//        itemHolder.bindHolder(aqiList.get(position));
        return convertView;
    }

    class AqiInfoHolder {
        private TextView mInfoName;
        private TextView mInfoLevel;

        void bindHolder(WeatherBean.ResultBeanX.ResultBean.AqiBean aqiBean) {
//            mInfoName.setText(aqiBean.get);
//            mInfoLevel.setText(bean.getVersionName());
        }
    }
}
