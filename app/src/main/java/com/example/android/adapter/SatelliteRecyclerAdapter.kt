package com.example.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.R
import com.example.android.model.Satellite
import com.pengxh.kt.lite.adapter.ViewHolder

class SatelliteRecyclerAdapter(
    private val context: Context,
    private val dataRows: MutableList<Satellite>
) : RecyclerView.Adapter<ViewHolder>() {

    private val flags = listOf(
        R.drawable.ic_unknown,  // 0: UNKNOWN
        R.drawable.ic_usa,      // 1: GPS
        R.drawable.ic_usa,      // 2: SBAS（复用GPS图标，因为SBAS主要增强GPS）
        R.drawable.ic_russia,   // 3: GLONASS
        R.drawable.ic_japen,    // 4: QZSS
        R.drawable.ic_china,    // 5: BDS
        R.drawable.ic_eu,       // 6: GALILEO
        R.drawable.ic_india     // 7: IRNSS
    )

    override fun getItemCount(): Int = dataRows.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.item_satellite_rv_l, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val satellite = dataRows[position]
        val signal = satellite.signal

        // 返回true表示该卫星正在被用于定位计算；返回false表示未被用于定位计算
        val signalColor: Int
        if (satellite.isUsedInFix) {
            holder.setImageResource(R.id.satelliteStateView, R.drawable.ic_in_use)
            signalColor = when {
                signal <= 18 -> R.color.mildColor
                signal <= 28 -> R.color.wellColor
                else -> R.color.green
            }
        } else {
            holder.setImageResource(R.id.satelliteStateView, R.drawable.ic_un_use)
            signalColor = R.color.badColor
        }

        val progressBar = holder.getView<ProgressBar>(R.id.signalProgressBar)
        progressBar.progressTintList = ContextCompat.getColorStateList(context, signalColor)
        progressBar.progress = signal

        holder.setImageResource(
            R.id.nationalityView, flags.getOrElse(satellite.type) { R.drawable.ic_unknown }
        )
            .setText(R.id.svidView, satellite.svid.substringAfterLast('_', satellite.svid))
            .setText(R.id.signalValueView, signal.toString())
            .setText(R.id.azimuthView, "${satellite.azimuth}°")
            .setText(R.id.elevationView, "${satellite.elevation}°")
    }

    /**
     * 刷新列表，局部刷新
     * */
    fun refresh(newRows: MutableList<Satellite>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = dataRows.size

            override fun getNewListSize(): Int = newRows.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = dataRows[oldItemPosition]
                val newItem = newRows[newItemPosition]
                return oldItem.type == newItem.type && oldItem.svid == newItem.svid
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItemPosition == newItemPosition
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback, true)

        dataRows.clear()
        dataRows.addAll(newRows)

        diffResult.dispatchUpdatesTo(this)
    }
}