package com.example.mutidemo.ui;

import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.example.mutidemo.adapter.NineGridImageAdapter;
import com.example.mutidemo.base.AndroidxBaseActivity;
import com.example.mutidemo.databinding.ActivityGridviewBinding;
import com.example.mutidemo.util.GlideLoadEngine;
import com.example.mutidemo.util.ImageUtil;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;

import java.util.ArrayList;
import java.util.List;

public class GridViewActivity extends AndroidxBaseActivity<ActivityGridviewBinding> {

    private NineGridImageAdapter nineRecyclerViewAdapter;
    private final ArrayList<String> recyclerViewImages = new ArrayList<>();

    @Override
    public void initData() {
        viewBinding.nineRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        nineRecyclerViewAdapter = new NineGridImageAdapter(this);
        viewBinding.nineRecyclerView.setAdapter(nineRecyclerViewAdapter);
        nineRecyclerViewAdapter.setOnItemClickListener(new NineGridImageAdapter.OnItemClickListener() {
            @Override
            public void onAddImageClick() {
                selectPicture();
            }

            @Override
            public void onItemClick(int position) {
                ImageUtil.showBigImage(GridViewActivity.this, position, recyclerViewImages);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                nineRecyclerViewAdapter.deleteImage(position);
            }
        });
    }

    private void selectPicture() {
        EasyPhotos.createAlbum(this, true, false, GlideLoadEngine.getInstance())
                .setFileProviderAuthority("com.example.mutidemo.fileProvider")
                .setCount(9)
                .setMinFileSize(1024 * 10)
                .start(new SelectCallback() {
                    @Override
                    public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                        for (Photo media : photos) {
                            recyclerViewImages.add(media.path);
                        }
                        nineRecyclerViewAdapter.setupImage(recyclerViewImages);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    @Override
    public void initEvent() {
        viewBinding.button.setOnClickListener(v -> viewBinding.imagePathView.setText(reformatURL(recyclerViewImages)));
    }

    private String reformatURL(List<String> urls) {
        if (urls.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < urls.size(); i++) {
            if (i != urls.size() - 1) {
                builder.append(urls.get(i)).append(",");
            } else {
                builder.append(urls.get(i));
            }
        }
        return builder.toString();
    }
}
