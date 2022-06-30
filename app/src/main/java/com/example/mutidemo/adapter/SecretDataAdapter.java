package com.example.mutidemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mutidemo.R;
import com.example.mutidemo.bean.SecretExcelBean;

import java.util.List;

public class SecretDataAdapter extends RecyclerView.Adapter<SecretDataAdapter.ItemViewHolder> {

    private final List<SecretExcelBean> dataRows;
    private final LayoutInflater layoutInflater;

    public SecretDataAdapter(Context context, List<SecretExcelBean> dataRows) {
        this.dataRows = dataRows;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(layoutInflater.inflate(R.layout.item_secret_rv, parent, false));
    }

    @Override
    public int getItemCount() {
        return dataRows.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        SecretExcelBean secretBean = dataRows.get(position);

        holder.titleView.setText("标题：" + secretBean.getTitle());
        holder.categoryView.setText("分类：" + secretBean.getCategory());
        holder.iconView.setText("图标：" + secretBean.getDataIcon());
        holder.accountView.setText("账号：" + secretBean.getAccount());
        holder.passwordView.setText("密码：" + secretBean.getPassword());
        holder.remarkView.setText("备注：" + secretBean.getRemarks());
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleView;
        private final TextView categoryView;
        private final TextView iconView;
        private final TextView accountView;
        private final TextView passwordView;
        private final TextView remarkView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.titleView);
            categoryView = itemView.findViewById(R.id.categoryView);
            iconView = itemView.findViewById(R.id.iconView);
            accountView = itemView.findViewById(R.id.accountView);
            passwordView = itemView.findViewById(R.id.passwordView);
            remarkView = itemView.findViewById(R.id.remarkView);
        }
    }
}
