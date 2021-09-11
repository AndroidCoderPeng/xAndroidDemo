package com.example.mutidemo.ui;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.NineGridImageAdapter;
import com.example.mutidemo.util.GlideLoadEngine;
import com.example.mutidemo.util.ImageUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class GridViewActivity extends BaseNormalActivity {

    private NineGridImageAdapter nineRecyclerViewAdapter;
    private ArrayList<String> recyclerViewImages = new ArrayList<>();
    @BindView(R.id.nineRecyclerView)
    RecyclerView nineRecyclerView;
    @BindView(R.id.imagePathView)
    TextView imagePathView;
    @BindView(R.id.button)
    Button button;

    @Override
    public int initLayoutView() {
        return R.layout.activity_gridview;
    }

    @Override
    public void initData() {
        nineRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        nineRecyclerViewAdapter = new NineGridImageAdapter(this);
        nineRecyclerView.setAdapter(nineRecyclerViewAdapter);
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
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .isWeChatStyle(true)
                .imageEngine(GlideLoadEngine.createGlideEngine())
                .maxSelectNum(9)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                for (LocalMedia media : selectList) {
                    recyclerViewImages.add(media.getRealPath());
                }
                nineRecyclerViewAdapter.setupImage(recyclerViewImages);
            }
        }
    }

    @Override
    public void initEvent() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePathView.setText(reformatURL(recyclerViewImages));
            }
        });
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
