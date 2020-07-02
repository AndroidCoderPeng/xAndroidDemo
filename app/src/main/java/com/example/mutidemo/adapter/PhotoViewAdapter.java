package com.example.mutidemo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aihook.alertview.library.AlertView;
import com.aihook.alertview.library.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mutidemo.R;
import com.example.mutidemo.bean.PhotoBean;
import com.example.mutidemo.util.ImageUtil;
import com.example.mutidemo.util.callback.BitmapCallBackListener;
import com.pengxh.app.multilib.widget.gallery3d.CardAdapterHelper;

import java.util.List;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/25 22:06
 */
public class PhotoViewAdapter extends RecyclerView.Adapter<PhotoViewAdapter.ViewHolder> {

    private Context context;
    private List<PhotoBean.Result> mList;
    private CardAdapterHelper mCardAdapterHelper = new CardAdapterHelper();

    public PhotoViewAdapter(Context context, List<PhotoBean.Result> mList) {
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mCardAdapterHelper.onBindViewHolder(holder.itemView, position, getItemCount());
        final String bigImageUrl = mList.get(position).getBigImageUrl();
        Glide.with(context).load(bigImageUrl).apply(RequestOptions.placeholderOf(R.mipmap.noimage)).into(holder.photoView);
        holder.photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog(bigImageUrl);
                return false;
            }
        });
    }

    private void showDialog(final String url) {
        new AlertView("提示", "是否下载此张壁纸", "取消", new String[]{"确定"}, null, context, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if (position == 0) {
                    ImageUtil.obtainBitmap(url, new BitmapCallBackListener() {
                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            ImageUtil.saveBitmap(context, bitmap);
                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }
                    });
                }
            }
        }).setCancelable(false).show();
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
