package com.example.mutidemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mutidemo.R;
import com.example.mutidemo.bean.AppInfoBean;
import com.pengxh.app.multilib.widget.swipemenu.BaseSwipListAdapter;

import java.util.List;

public class AppListAdapter extends BaseSwipListAdapter {
    private Context context;
    private List<AppInfoBean> appInfoList;
    private final LayoutInflater inflater;

    public AppListAdapter(Context context, List<AppInfoBean> appInfoList) {
        this.context = context;
        this.appInfoList = appInfoList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return appInfoList == null ? 0 : appInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return appInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AppInfoHolder itemHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_swipelist, null);
            itemHolder = new AppInfoHolder();
            itemHolder.mAppName = convertView.findViewById(R.id.mAppName);
            itemHolder.mAppVersion = convertView.findViewById(R.id.mAppVersion);
            itemHolder.mAppIcon = convertView.findViewById(R.id.mAppIcon);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (AppInfoHolder) convertView.getTag();
        }
        itemHolder.bindHolder(appInfoList.get(position));
        return convertView;
    }

    class AppInfoHolder {
        private TextView mAppName;
        private TextView mAppVersion;
        private ImageView mAppIcon;

        void bindHolder(AppInfoBean bean) {
            mAppName.setText(bean.getAppName());
            mAppVersion.setText(bean.getVersionName());
            mAppIcon.setImageDrawable(bean.getAppIcon());
        }
    }
}