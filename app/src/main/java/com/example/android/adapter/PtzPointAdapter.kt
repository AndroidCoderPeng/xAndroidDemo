package com.example.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.R
import com.pengxh.kt.lite.adapter.ViewHolder

class PtzPointAdapter(private val context: Context, private val points: MutableList<Int>) :
    RecyclerView.Adapter<ViewHolder>() {

    private val kTag = "PtzPointAdapter"
    private var currentPosition = -1

    fun updatePosition(position: Int) {
        val previousPosition = currentPosition
        currentPosition = position
        notifyItemChanged(previousPosition)
        notifyItemChanged(currentPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_point_rv_g, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rootView = holder.getView<LinearLayout>(R.id.rootView)
        val textView = holder.getView<TextView>(R.id.textView)

        if (position == currentPosition) {
            rootView.setBackgroundResource(R.drawable.bg_layout_button_highlight)
        } else {
            rootView.setBackgroundResource(R.drawable.selector_layout_button_background)
        }
        textView.text = "预置点 - ${position + 1}"
    }

    override fun getItemCount() = points.size
}