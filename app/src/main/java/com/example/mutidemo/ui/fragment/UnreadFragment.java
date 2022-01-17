package com.example.mutidemo.ui.fragment;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mutidemo.adapter.SwipeViewAdapter;
import com.example.mutidemo.base.AndroidxBaseFragment;
import com.example.mutidemo.databinding.FragmentUnreadBinding;
import com.qmuiteam.qmui.recyclerView.QMUIRVItemSwipeAction;
import com.qmuiteam.qmui.recyclerView.QMUISwipeAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnreadFragment extends AndroidxBaseFragment<FragmentUnreadBinding> {

    private final List<String> data = new ArrayList<>(Arrays.asList("Helps", "Maintain", "Liver", "Health", "Function", "Supports", "Healthy", "Fat",
            "Metabolism", "Nuturally", "Bracket", "Refrigerator", "Bathtub", "Wardrobe", "Comb", "Apron", "Carpet", "Bolster", "Pillow", "Cushion"));
    private SwipeViewAdapter swipeViewAdapter;

    @Override
    public void initData() {
        swipeViewAdapter = new SwipeViewAdapter(getContext());
        swipeViewAdapter.setData(data);
    }

    @Override
    public void initEvent() {
        QMUIRVItemSwipeAction swipeAction = new QMUIRVItemSwipeAction(true, new QMUIRVItemSwipeAction.Callback() {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                swipeViewAdapter.remove(viewHolder.getAdapterPosition());
            }

            @Override
            public int getSwipeDirection(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return QMUIRVItemSwipeAction.SWIPE_LEFT;
            }

            @Override
            public void onClickAction(QMUIRVItemSwipeAction swipeAction, RecyclerView.ViewHolder selected, QMUISwipeAction action) {
                super.onClickAction(swipeAction, selected, action);
                swipeViewAdapter.remove(selected.getAdapterPosition());
            }
        });
        swipeAction.attachToRecyclerView(viewBinding.swipeRecyclerView);
        viewBinding.swipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });
        viewBinding.swipeRecyclerView.addItemDecoration(new DividerItemDecoration(viewBinding.getRoot().getContext(), DividerItemDecoration.VERTICAL));
        viewBinding.swipeRecyclerView.setAdapter(swipeViewAdapter);
    }
}
