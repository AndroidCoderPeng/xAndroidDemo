package com.example.multidemo.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.example.multidemo.R
import com.example.multidemo.databinding.ActivityTimeLineBinding
import com.example.multidemo.extensions.initImmersionBar
import com.example.multidemo.model.TimeLineDataModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.readAssetsFile

class TimeLineActivity : KotlinBaseActivity<ActivityTimeLineBinding>() {

    private val gson by lazy { Gson() }

    override fun initOnCreate(savedInstanceState: Bundle?) {
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
        binding.recyclerView.adapter = logAdapter
    }

    override fun initEvent() {

    }

    override fun initViewBinding(): ActivityTimeLineBinding {
        return ActivityTimeLineBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {
        binding.rootView.initImmersionBar(this, true, R.color.white)
    }
}