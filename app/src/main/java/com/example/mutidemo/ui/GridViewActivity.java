package com.example.mutidemo.ui;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.NineGridImageAdapter;
import com.example.mutidemo.util.GlideLoadEngine;
import com.example.mutidemo.util.ImageUtil;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class GridViewActivity extends BaseNormalActivity {

    private NineGridImageAdapter nineRecyclerViewAdapter;
    private final ArrayList<String> recyclerViewImages = new ArrayList<>();
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
