package com.example.mutidemo.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mutidemo.R
import com.qmuiteam.qmui.recyclerView.QMUISwipeAction
import com.qmuiteam.qmui.recyclerView.QMUISwipeAction.ActionBuilder
import com.qmuiteam.qmui.recyclerView.QMUISwipeViewHolder
import com.qmuiteam.qmui.util.QMUIDisplayHelper

class SwipeViewAdapter(context: Context?) : RecyclerView.Adapter<QMUISwipeViewHolder>() {

    private val mData: MutableList<String> = ArrayList()
    private val mDeleteAction: QMUISwipeAction

    init {
        val builder = ActionBuilder()
            .textSize(QMUIDisplayHelper.sp2px(context, 18))
            .textColor(Color.WHITE)
            .paddingStartEnd(QMUIDisplayHelper.dp2px(context, 18))
        mDeleteAction = builder.text("删除").backgroundColor(Color.RED).build()
    }

    fun setData(list: List<String>?) {
        mData.clear()
        if (list != null) {
            mData.addAll(list)
        }
        notifyDataSetChanged()
    }

    fun remove(pos: Int) {
        mData.removeAt(pos)
        notifyItemRemoved(pos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QMUISwipeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_unread_lv, parent, false)
        val vh = QMUISwipeViewHolder(view)
        vh.addSwipeAction(mDeleteAction)
        view.setOnClickListener { }
        return vh
    }

    override fun onBindViewHolder(holder: QMUISwipeViewHolder, position: Int) {
        val textView = holder.itemView.findViewById<TextView>(R.id.textView)
        textView.text = mData[position]
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}