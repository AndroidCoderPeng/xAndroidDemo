package com.example.multidemo.view

import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.lifecycle.ViewModelProvider
import com.example.multidemo.R
import com.example.multidemo.databinding.ActivityWebCrawlerBinding
import com.example.multidemo.model.CrawlerResultListModel
import com.example.multidemo.util.LoadingDialogHub
import com.example.multidemo.vm.CrawlerViewModel
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.divider.ItemDecoration
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.navigatePageTo
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.utils.WeakReferenceHandler
import com.pengxh.kt.lite.vm.LoadState

class WebsiteCrawlerActivity : KotlinBaseActivity<ActivityWebCrawlerBinding>(), Handler.Callback {

    private val kTag = "WebsiteCrawlerActivity"
    private lateinit var crawlerViewModel: CrawlerViewModel
    private lateinit var weakReferenceHandler: WeakReferenceHandler
    private lateinit var resultAdapter: NormalRecyclerAdapter<CrawlerResultListModel.DataModel>
    private var offset = 1
    private var updateListCode = 2023110701
    private var isRefresh = false
    private var isLoadMore = false
    private var dataBeans: MutableList<CrawlerResultListModel.DataModel> = ArrayList()

    override fun initEvent() {
        binding.refreshLayout.setOnRefreshListener {
            isRefresh = true
            offset = 1
            getCrawlerResultsByPage()
        }
        binding.refreshLayout.setOnLoadMoreListener {
            isLoadMore = true
            offset++
            getCrawlerResultsByPage()
        }
    }

    override fun onResume() {
        super.onResume()
        getCrawlerResultsByPage()
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        crawlerViewModel = ViewModelProvider(this)[CrawlerViewModel::class.java]
        weakReferenceHandler = WeakReferenceHandler(this)
        crawlerViewModel.resultModel.observe(this) {
            if (it.code == 200) {
                val dataRows = it.data
                when {
                    isRefresh -> {
                        resultAdapter.setRefreshData(dataRows)
                        binding.refreshLayout.finishRefresh()
                        isRefresh = false
                    }

                    isLoadMore -> {
                        if (dataRows.size == 0) {
                            "到底了，别拉了".show(this)
                        }
                        resultAdapter.setLoadMoreData(dataRows)
                        binding.refreshLayout.finishLoadMore()
                        isLoadMore = false
                    }

                    else -> {
                        dataBeans = dataRows
                        weakReferenceHandler.sendEmptyMessage(updateListCode)
                    }
                }
            }
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == updateListCode) {
            resultAdapter = object : NormalRecyclerAdapter<CrawlerResultListModel.DataModel>(
                R.layout.item_crawler_rv_l, dataBeans
            ) {
                override fun convertView(
                    viewHolder: ViewHolder, position: Int, item: CrawlerResultListModel.DataModel
                ) {
                    viewHolder.setText(R.id.resultTitle, item.title)
                        .setText(R.id.updateTime, item.updateTime)
                }
            }
            binding.recyclerView.addItemDecoration(
                ItemDecoration(0f, 130f.dp2px(this@WebsiteCrawlerActivity).toFloat())
            )
            binding.recyclerView.adapter = resultAdapter
            resultAdapter.setOnItemClickedListener(object :
                NormalRecyclerAdapter.OnItemClickedListener<CrawlerResultListModel.DataModel> {
                override fun onItemClicked(position: Int, t: CrawlerResultListModel.DataModel) {
                    navigatePageTo<CrawlerResultActivity>(t.link)
                }
            })
        }
        return true
    }

    private fun getCrawlerResultsByPage() {
        crawlerViewModel.getCrawlerResultsByPage("", "", offset)
    }

    override fun initViewBinding(): ActivityWebCrawlerBinding {
        return ActivityWebCrawlerBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {
        crawlerViewModel.loadState.observe(this) {
            when (it) {
                LoadState.Loading -> LoadingDialogHub.show(this, "加载数据中，请稍后...")
                else -> LoadingDialogHub.dismiss()
            }
        }
    }

    override fun setupTopBarLayout() {

    }
}