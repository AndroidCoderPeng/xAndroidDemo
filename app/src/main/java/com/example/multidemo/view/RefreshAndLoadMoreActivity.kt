package com.example.multidemo.view

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.multidemo.R
import com.example.multidemo.extensions.addAll
import com.example.multidemo.model.NewsListModel
import com.example.multidemo.util.LoadingDialogHub
import com.example.multidemo.vm.NewsViewModel
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.divider.ItemDecoration
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.navigatePageTo
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.utils.WeakReferenceHandler
import com.pengxh.kt.lite.vm.LoadState
import kotlinx.android.synthetic.main.activity_refresh.*
import kotlinx.android.synthetic.main.item_news_rv_l.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/2/21 19:16
 */
class RefreshAndLoadMoreActivity : KotlinBaseActivity() {

    private lateinit var weakReferenceHandler: WeakReferenceHandler
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NormalRecyclerAdapter<NewsListModel.ResultBeanX.ResultBean.ListBean>
    private var dataBeans: MutableList<NewsListModel.ResultBeanX.ResultBean.ListBean> = ArrayList()
    private var isRefresh = false
    private var isLoadMore = false
    private var pageIndex = 0

    override fun setupTopBarLayout() {}

    override fun initLayoutView(): Int = R.layout.activity_refresh

    override fun initData(savedInstanceState: Bundle?) {
        weakReferenceHandler = WeakReferenceHandler(callback)
        newsViewModel = ViewModelProvider(this)[NewsViewModel::class.java]
        newsViewModel.resultModel.observe(this) {
            if (it.code == "10000") {
                val dataRows = it.result.result.list
                when {
                    isRefresh -> {
                        newsAdapter.setRefreshData(dataRows)
                        refreshLayout.finishRefresh()
                        isRefresh = false
                    }
                    isLoadMore -> {
                        if (dataRows.size == 0) {
                            "到底了，别拉了".show(this)
                        }
                        newsAdapter.setLoadMoreData(dataRows)
                        refreshLayout.finishLoadMore()
                        isLoadMore = false
                    }
                    //首次加载数据
                    else -> {
                        dataBeans = dataRows
                        weakReferenceHandler.sendEmptyMessage(2023031301)
                    }
                }
            }
        }

        getNewsList()
    }

    override fun initEvent() {
        refreshLayout.setOnRefreshListener {
            isRefresh = true
            //刷新之后页码重置
            pageIndex = 0
            getNewsList()
        }
        refreshLayout.setOnLoadMoreListener {
            isLoadMore = true
            pageIndex++
            getNewsList()
        }
    }

    override fun observeRequestState() {
        newsViewModel.loadState.observe(this) {
            when (it) {
                LoadState.Loading -> {
                    if (!isRefresh && !isLoadMore) {
                        LoadingDialogHub.show(this, "加载数据中，请稍后...")
                    }
                }
                else -> LoadingDialogHub.dismiss()
            }
        }
    }

    private fun getNewsList() {
        newsViewModel.getNewsList("头条", pageIndex)
    }

    private val callback = Handler.Callback { msg: Message ->
        if (msg.what == 2023031301) {
            newsAdapter = object :
                NormalRecyclerAdapter<NewsListModel.ResultBeanX.ResultBean.ListBean>(
                    R.layout.item_news_rv_l, dataBeans
                ) {
                override fun convertView(
                    viewHolder: ViewHolder, position: Int,
                    item: NewsListModel.ResultBeanX.ResultBean.ListBean
                ) {
                    val img: String = item.pic
                    if (img == "" || img.endsWith(".gif")) {
                        newsPicture.visibility = View.GONE
                    } else {
                        viewHolder.setImageResource(R.id.newsPicture, img)
                    }

                    viewHolder.setText(R.id.newsTitle, item.title)
                        .setText(R.id.newsSrc, item.src)
                        .setText(R.id.newsTime, item.time)
                }
            }
            newsRecyclerView.addItemDecoration(
                ItemDecoration(0f, 130f.dp2px(this@RefreshAndLoadMoreActivity).toFloat())
            )
            newsRecyclerView.adapter = newsAdapter
            newsAdapter.setOnItemClickedListener(object :
                NormalRecyclerAdapter.OnItemClickedListener<NewsListModel.ResultBeanX.ResultBean.ListBean> {
                override fun onItemClicked(
                    position: Int, t: NewsListModel.ResultBeanX.ResultBean.ListBean
                ) {
                    navigatePageTo<NewsDetailsActivity>(addAll(t.title, t.src, t.time, t.content))
                }
            })
        }
        true
    }
}