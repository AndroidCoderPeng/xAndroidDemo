package com.example.android.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.android.R
import com.example.android.databinding.ActivitySlideNavigationBinding
import com.example.android.fragment.AlarmPageFragment
import com.example.android.fragment.HomePageFragment
import com.example.android.fragment.MinePageFragment
import com.example.android.fragment.TaskPageFragment
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.convertColor

class SlideNavigationActivity : KotlinBaseActivity<ActivitySlideNavigationBinding>() {

    private val slideUnSelectedItems = ArrayList<SlideItem>()
    private val slideSelectedItems = ArrayList<SlideItem>()
    private val fragmentPages = ArrayList<Fragment>()
    private val slideAdapter by lazy { SlideAdapter(this) }

    init {
        slideUnSelectedItems.add(SlideItem(R.drawable.ic_home_unselected, "首页"))
        slideUnSelectedItems.add(SlideItem(R.drawable.ic_alarm_unselected, "报警记录"))
        slideUnSelectedItems.add(SlideItem(R.drawable.ic_task_unselected, "任务"))
        slideUnSelectedItems.add(SlideItem(R.drawable.ic_mine_unselected, "我的"))

        slideSelectedItems.add(SlideItem(R.drawable.ic_home_selected, "首页"))
        slideSelectedItems.add(SlideItem(R.drawable.ic_alarm_selected, "报警记录"))
        slideSelectedItems.add(SlideItem(R.drawable.ic_task_selected, "任务"))
        slideSelectedItems.add(SlideItem(R.drawable.ic_mine_selected, "我的"))

        fragmentPages.add(HomePageFragment())
        fragmentPages.add(AlarmPageFragment())
        fragmentPages.add(TaskPageFragment())
        fragmentPages.add(MinePageFragment())
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        binding.slideListView.adapter = slideAdapter

        //默认选中第一个
        slideAdapter.setSelectItem(0)
        slideAdapter.notifyDataSetInvalidated()

        //显示首页
        switchPage(fragmentPages[0])
    }

    override fun initEvent() {
        binding.slideListView.setOnItemClickListener { _, _, position, _ ->
            slideAdapter.setSelectItem(position)
            slideAdapter.notifyDataSetInvalidated()

            //切换页面
            switchPage(fragmentPages[position])
        }
    }

    private fun switchPage(description: Fragment) {
        val transition = supportFragmentManager.beginTransaction()
        transition.replace(R.id.contentLayout, description)
        transition.commit()
    }

    override fun initViewBinding(): ActivitySlideNavigationBinding {
        return ActivitySlideNavigationBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {
        ImmersionBar.with(this).statusBarDarkFont(true).init()
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

        override fun getCount(): Int = slideUnSelectedItems.size

        override fun getItem(position: Int): Any = slideUnSelectedItems[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var view = convertView
            val holder: SlideItemViewHolder?
            if (view == null) {
                view = inflater.inflate(R.layout.item_slide_list, parent, false)
                holder = SlideItemViewHolder()
                holder.imageView = view.findViewById(R.id.imageView)
                holder.textView = view.findViewById(R.id.textView)
                view.tag = holder
            } else {
                holder = view.tag as SlideItemViewHolder
            }
            if (selectedPosition == position) {
                holder.imageView.setImageResource(slideSelectedItems[position].icon)
                holder.textView.setTextColor(R.color.mainColor.convertColor(context))
            } else {
                holder.imageView.setImageResource(slideUnSelectedItems[position].icon)
                holder.textView.setTextColor(R.color.white.convertColor(context))
            }
            holder.textView.text = slideUnSelectedItems[position].title
            return view
        }

        fun setSelectItem(position: Int) {
            selectedPosition = position
        }
    }

    class SlideItemViewHolder {
        lateinit var imageView: ImageView
        lateinit var textView: TextView
    }
}