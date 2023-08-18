package com.example.multidemo.view

import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.recyclerview.widget.RecyclerView
import com.example.multidemo.R
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.divider.ItemDecoration
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.timestampToCompleteDate
import com.pengxh.kt.lite.utils.WeakReferenceHandler
import kotlinx.android.synthetic.main.activity_radio_rv.*
import java.util.*

class RadioButtonActivity : KotlinBaseActivity(), Handler.Callback {

    private val kTag = "RadioButtonActivity"
    private var index = 0
    private lateinit var weakReferenceHandler: WeakReferenceHandler
    private lateinit var baseAdapter: NormalRecyclerAdapter<String>
    private var dataBeans = ArrayList<String>()

    override fun initData(savedInstanceState: Bundle?) {
        weakReferenceHandler = WeakReferenceHandler(this)

        //默认选中
        modeRadioGroup.check(R.id.redRadioButton)

        dataBeans = getRecyclerViewData()
        weakReferenceHandler.sendEmptyMessage(2023081801)
    }

    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == 2023081801) {
            baseAdapter = object : NormalRecyclerAdapter<String>(
                R.layout.item_recycler_view, dataBeans
            ) {
                override fun convertView(viewHolder: ViewHolder, position: Int, item: String) {
                    viewHolder.setText(R.id.textView, item)
                }
            }
            recyclerView.adapter = baseAdapter
            recyclerView.addItemDecoration(
                ItemDecoration(10f.dp2px(this).toFloat(), 10f.dp2px(this).toFloat())
            )
        }
        return true
    }

    override fun initEvent() {
        //监听滑动到底部
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (recyclerView.canScrollVertically(1)) {
                        //切换顶部Tab
                        index++
                        if (index > 3) {
                            index = 0
                        }
                        when (index) {
                            0 -> modeRadioGroup.check(R.id.redRadioButton)
                            1 -> modeRadioGroup.check(R.id.blueRadioButton)
                            2 -> modeRadioGroup.check(R.id.whiteRadioButton)
                            3 -> modeRadioGroup.check(R.id.blackRadioButton)
                        }
                    } else if (recyclerView.canScrollVertically(-1)) {
                        index--
                        if (index < 0) {
                            index = 3
                        }
                        when (index) {
                            0 -> modeRadioGroup.check(R.id.redRadioButton)
                            1 -> modeRadioGroup.check(R.id.blueRadioButton)
                            2 -> modeRadioGroup.check(R.id.whiteRadioButton)
                            3 -> modeRadioGroup.check(R.id.blackRadioButton)
                        }
                    }
                }
            }
        })

        //顶部Tab选中监听
        modeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
//            recyclerView.smoothScrollToPosition(0)
            when (checkedId) {
                //滚动rv到顶部然后刷新rv数据
                R.id.redRadioButton -> {
                    index = 0
                    dataBeans = getRecyclerViewData()
                }
                R.id.blueRadioButton -> {
                    index = 1
                    dataBeans = getOtherRecyclerViewData()
                }
                R.id.whiteRadioButton -> {
                    index = 2
                    dataBeans = getRecyclerViewData()
                }
                R.id.blackRadioButton -> {
                    index = 3
                    dataBeans = getOtherRecyclerViewData()
                }
            }
            weakReferenceHandler.sendEmptyMessage(2023081801)
        }
    }

    private fun getRecyclerViewData(): ArrayList<String> {
        val arrayList = ArrayList<String>()
        for (i in 0..20) {
            arrayList.add(UUID.randomUUID().toString())
        }
        return arrayList
    }

    private fun getOtherRecyclerViewData(): ArrayList<String> {
        val arrayList = ArrayList<String>()
        for (i in 0..20) {
            arrayList.add(System.currentTimeMillis().timestampToCompleteDate())
        }
        return arrayList
    }

    override fun initLayoutView(): Int = R.layout.activity_radio_rv

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }
}