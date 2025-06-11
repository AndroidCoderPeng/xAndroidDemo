package com.example.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.example.android.R
import com.example.android.model.Satellite
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.extensions.convertDrawable

class SatelliteRecyclerAdapter(
    private val context: Context, private val dataRows: MutableList<Satellite>
) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int = dataRows.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_satellite_rv_l, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val satellite = dataRows[position]

        var image = R.drawable.ic_unknown
        when (satellite.type) {
            1 -> image = R.drawable.ic_usa
            3 -> image = R.drawable.ic_russia
            4 -> image = R.drawable.ic_japen
            5 -> image = R.drawable.ic_china
            6 -> image = R.drawable.ic_eu
            7 -> image = R.drawable.ic_india
        }

        //如果返回true，则表示该卫星正在被用于定位计算；如果返回false，则表示该卫星未被用于定位计算
        val signalDrawable = if (satellite.isUsedInFix) {
            holder.setImageResource(R.id.satelliteStateView, R.drawable.ic_in_use)
            if (satellite.signal <= 19) {
                R.drawable.bg_progress_bar_middle_low
            } else if (satellite.signal in 20..29) {
                R.drawable.bg_progress_bar_middle_high
            } else {
                R.drawable.bg_progress_bar_high
            }
        } else {
            holder.setImageResource(R.id.satelliteStateView, R.drawable.ic_un_use)
            R.drawable.bg_progress_bar_low
        }
        val signalProgressView = holder.getView<ProgressBar>(R.id.signalProgressView)
        signalProgressView.progressDrawable = signalDrawable.convertDrawable(context)
        signalProgressView.progress = satellite.signal

        holder.setImageResource(R.id.nationalityView, image)
            .setText(R.id.svidView, satellite.svid.split("_")[1])
            .setText(R.id.signalValueView, "${satellite.signal}")
            .setText(R.id.azimuthView, "${satellite.azimuth}°")
            .setText(R.id.elevationView, "${satellite.elevation}°")
    }
}