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
import com.example.mutidemo.bean.CityBean;

import java.util.List;

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/9/27 22:40
 */
public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<CityBean> mCityItem;

    public CityAdapter(Context mContext, List<CityBean> list) {
        this.mCityItem = list;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_city_recyclerview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bindHolder(mCityItem.get(position).getCity());
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mCityItem == null ? 0 : mCityItem.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView cityName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.cityName);
        }

        void bindHolder(String city) {
            cityName.setText(city);
        }
    }

    private OnCityItemClickListener mOnItemClickListener;

    public interface OnCityItemClickListener {
        void onClick(int position);
    }

    public void setOnCityItemClickListener(OnCityItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}