package com.example.mutidemo.fragment

import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mutidemo.R
import com.example.mutidemo.adapter.SwipeViewAdapter
import com.pengxh.kt.lite.base.KotlinBaseFragment
import com.qmuiteam.qmui.recyclerView.QMUIRVItemSwipeAction
import com.qmuiteam.qmui.recyclerView.QMUISwipeAction
import kotlinx.android.synthetic.main.fragment_unread.*

class UnreadFragment : KotlinBaseFragment() {

    private val data: List<String> = ArrayList(
        listOf(
            "Helps",
            "Maintain",
            "Liver",
            "Health",
            "Function",
            "Supports",
            "Healthy",
            "Fat",
            "Metabolism",
            "Nuturally",
            "Bracket",
            "Refrigerator",
            "Bathtub",
            "Wardrobe",
            "Comb",
            "Apron",
            "Carpet",
            "Bolster",
            "Pillow",
            "Cushion"
        )
    )
    private lateinit var swipeViewAdapter: SwipeViewAdapter

    override fun setupTopBarLayout() {

    }

    override fun initLayoutView(): Int = R.layout.fragment_unread

    override fun observeRequestState() {

    }

    override fun initData() {
        swipeViewAdapter = SwipeViewAdapter(requireContext())
        swipeViewAdapter.setData(data)
    }

    override fun initEvent() {
        val swipeAction = QMUIRVItemSwipeAction(true, object : QMUIRVItemSwipeAction.Callback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                swipeViewAdapter.remove(viewHolder.adapterPosition)
            }

            override fun getSwipeDirection(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return QMUIRVItemSwipeAction.SWIPE_LEFT
            }

            override fun onClickAction(
                swipeAction: QMUIRVItemSwipeAction,
                selected: RecyclerView.ViewHolder,
                action: QMUISwipeAction
            ) {
                super.onClickAction(swipeAction, selected, action)
                swipeViewAdapter.remove(selected.adapterPosition)
            }
        })
        swipeAction.attachToRecyclerView(swipeRecyclerView)
        swipeRecyclerView.layoutManager = object : LinearLayoutManager(context) {
            override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
                return RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }
        swipeRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(), DividerItemDecoration.VERTICAL
            )
        )
        swipeRecyclerView.adapter = swipeViewAdapter
    }
}