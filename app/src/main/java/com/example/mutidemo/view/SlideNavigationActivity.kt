package com.example.mutidemo.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.mutidemo.R
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.convertColor
import kotlinx.android.synthetic.main.activity_slide_navigation.*


class SlideNavigationActivity : KotlinBaseActivity() {

    private val slideItems = ArrayList<SlideItem>()
    private val slideAdapter by lazy { SlideAdapter(this) }

    init {
        slideItems.add(SlideItem(R.drawable.ic_home, "首页"))
        slideItems.add(SlideItem(R.drawable.ic_alarm, "报警记录"))
        slideItems.add(SlideItem(R.drawable.ic_task, "任务"))
        slideItems.add(SlideItem(R.drawable.ic_user, "我的"))
    }

    override fun initData() {
        slideListView.adapter = slideAdapter

        //默认选中第一个
        slideAdapter.setSelectItem(0)
        slideAdapter.notifyDataSetInvalidated()

        //导航到首页

    }

    override fun initEvent() {
        slideListView.setOnItemClickListener { parent, view, position, id ->
            slideAdapter.setSelectItem(position)
            slideAdapter.notifyDataSetInvalidated()

            //切换页面
            when (position) {
                0 -> {
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.layout.fragment_home,)
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    transaction.commit()
                }
                1 -> {}
                2 -> {}
                4 -> {}
            }
        }
    }

    /**
     * 切换页面显示
     * */
    private inline fun <reified T> Fragment.navigatePageTo() {
        if () {

        }
    }

    override fun initLayoutView(): Int = R.layout.activity_slide_navigation

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }

    /**
     * 侧边导航栏数据模型
     * */
    data class SlideItem(val icon: Int, val title: String)

    /**
     * 侧边导航适配器
     * */
    inner class SlideAdapter(private val context: Context) : BaseAdapter() {

        private var inflater: LayoutInflater = LayoutInflater.from(context)
        private var selectedPosition = -1

        override fun getCount(): Int = slideItems.size

        override fun getItem(position: Int): Any = slideItems[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var view = convertView
            val holder: SlideItemViewHolder?
            if (view == null) {
                view = inflater.inflate(R.layout.item_slide_list, parent, false)
                holder = SlideItemViewHolder()
                holder.rootLayout = view.findViewById(R.id.rootLayout) as LinearLayout
                holder.imageView = view.findViewById(R.id.imageView) as ImageView
                holder.textView = view.findViewById(R.id.textView) as TextView
                view.tag = holder
            } else {
                holder = view.tag as SlideItemViewHolder
            }
            holder.imageView.setImageResource(slideItems[position].icon)
            holder.textView.text = slideItems[position].title
            if (selectedPosition == position) {
                holder.rootLayout.setBackgroundColor(R.color.darkMainColor.convertColor(context))
            } else {
                holder.rootLayout.setBackgroundColor(R.color.mainColor.convertColor(context))
            }
            return view
        }

        fun setSelectItem(position: Int) {
            selectedPosition = position
        }
    }

    inner class SlideItemViewHolder {
        lateinit var rootLayout: LinearLayout
        lateinit var imageView: ImageView
        lateinit var textView: TextView
    }
}