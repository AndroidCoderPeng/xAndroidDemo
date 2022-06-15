package com.example.mutidemo.ui;

import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.example.mutidemo.databinding.ActivityGridviewBinding;
import com.example.mutidemo.util.GlideLoadEngine;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.pengxh.androidx.lite.adapter.EditableImageAdapter;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class GridViewActivity extends AndroidxBaseActivity<ActivityGridviewBinding> {

    private EditableImageAdapter editableImageAdapter;
    private final ArrayList<String> recyclerViewImages = new ArrayList<>();

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    public void initData() {
        viewBinding.nineRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        editableImageAdapter = new EditableImageAdapter(this, 9);
        viewBinding.nineRecyclerView.setAdapter(editableImageAdapter);
        editableImageAdapter.setOnItemClickListener(new EditableImageAdapter.OnItemClickListener() {
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
                editableImageAdapter.deleteImage(position);
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
                        editableImageAdapter.setupImage(recyclerViewImages);
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
