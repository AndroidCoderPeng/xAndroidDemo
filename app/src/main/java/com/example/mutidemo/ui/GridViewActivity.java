package com.example.mutidemo.ui;

import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.example.mutidemo.databinding.ActivityGridviewBinding;
import com.example.mutidemo.util.GlideLoadEngine;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
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
        editableImageAdapter = new EditableImageAdapter(this, 9,1f);
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
        PictureSelector.create(GridViewActivity.this)
                .openGallery(SelectMimeType.ofImage())
                .isGif(false)
                .isMaxSelectEnabledMask(true)
                .setFilterMinFileSize(100)
                .setMaxSelectNum(9)
                .isDisplayCamera(false)
                .setImageEngine(GlideLoadEngine.getInstance())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        for (LocalMedia media : result) {
                            recyclerViewImages.add(media.getRealPath());
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
