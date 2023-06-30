package com.example.mutidemo.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.example.mutidemo.R
import com.example.mutidemo.model.TimeLineDataModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.readAssetsFile
import kotlinx.android.synthetic.main.activity_time_line.*

class TimeLineActivity : KotlinBaseActivity() {

    private val gson by lazy { Gson() }

    override fun initData(savedInstanceState: Bundle?) {
        val data = readAssetsFile("TestData.json")
        val models = gson.fromJson<TimeLineDataModel>(
            data, object : TypeToken<TimeLineDataModel>() {}.type
        )

        val logAdapter = object : NormalRecyclerAdapter<TimeLineDataModel.DataModel>(
            R.layout.item_entrust_log_rv_l, models.data
        ) {
            override fun convertView(
                viewHolder: ViewHolder,
                position: Int,
                item: TimeLineDataModel.DataModel
            ) {
                when (position) {
                    0 -> {
                        //最后一项
                        viewHolder.setBackgroundColor(R.id.dotView, Color.BLACK)
                        viewHolder.setTextColor(R.id.operatorNameView, Color.BLACK)
                            .setTextColor(R.id.statusView, Color.BLACK)
                            .setTextColor(R.id.operateTimeView, Color.BLACK)
                            .setTextColor(R.id.remarkView, Color.BLACK)

                        viewHolder.setVisibility(R.id.topLineView, View.INVISIBLE)
                        viewHolder.setImageResource(R.id.tagImageView, R.drawable.ic_dot_red)
                    }
                    models.data.size - 1 -> {
                        viewHolder.setVisibility(R.id.bottomLineView, View.INVISIBLE)
                        viewHolder.setImageResource(R.id.tagImageView, R.drawable.ic_dot_gray)
                    }
                    else -> {
                        viewHolder.setImageResource(R.id.tagImageView, R.drawable.ic_dot)
                    }
                }

                viewHolder.setText(R.id.operatorNameView, item.createUserName)
                    .setText(R.id.statusView, item.status)
                    .setText(R.id.operateTimeView, item.createTime)
                    .setText(R.id.remarkView, item.recordContent)
            }
        }
        recyclerView.adapter = logAdapter
    }

    override fun initEvent() {

    }

    override fun initLayoutView(): Int = R.layout.activity_time_line

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }
}