package com.example.multidemo.view

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.multidemo.R
import com.example.multidemo.callback.DecorationCallback
import com.example.multidemo.databinding.ActivitySlideBinding
import com.example.multidemo.model.CityModel
import com.example.multidemo.util.StringHelper
import com.example.multidemo.util.VerticalItemDecoration
import com.example.multidemo.widget.SlideBarView
import com.google.gson.Gson
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.show
import java.text.Collator
import java.util.Collections
import java.util.Locale

class SlideBarActivity : KotlinBaseActivity<ActivitySlideBinding>() {

    private val kTag = "SlideBarActivity"
    private val CITY = listOf(
        "安徽",
        "北京",
        "滨海",
        "重庆",
        "大连",
        "恩施",
        "福建",
        "甘肃",
        "广东",
        "广西",
        "贵州",
        "海南",
        "河北",
        "河南",
        "黑龙江",
        "湖北",
        "湖南",
        "黄石",
        "吉林",
        "江苏",
        "江西",
        "锦州",
        "荆门",
        "九江",
        "辽宁",
        "洛阳",
        "内蒙古",
        "宁波",
        "宁夏",
        "青岛",
        "青海",
        "三亚",
        "山东",
        "山西",
        "陕西",
        "上海",
        "深圳",
        "十堰",
        "四川",
        "天津",
        "西藏",
        "厦门",
        "襄阳",
        "孝感",
        "新疆",
        "新乡",
        "忻州",
        "宜昌",
        "云南",
        "湛江",
        "浙江",
        "珠海"
    )

    override fun setupTopBarLayout() {}

    override fun initViewBinding(): ActivitySlideBinding {
        return ActivitySlideBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        val cityBeans: List<CityModel> = obtainCityData()
        val cityAdapter = object : NormalRecyclerAdapter<CityModel>(
            R.layout.item_city_rv_l, cityBeans.toMutableList()
        ) {
            override fun convertView(viewHolder: ViewHolder, position: Int, item: CityModel) {
                viewHolder.setText(R.id.cityName, item.city)
            }
        }
        val layoutManager: LinearLayoutManager = object : LinearLayoutManager(this) {
            override fun smoothScrollToPosition(
                recyclerView: RecyclerView, state: RecyclerView.State, position: Int
            ) {
                val scroller = VerticalItemDecoration.TopSmoothScroller(recyclerView.context)
                scroller.targetPosition = position
                startSmoothScroll(scroller)
            }
        }
        binding.cityRecyclerView.layoutManager = layoutManager
        binding.cityRecyclerView.addItemDecoration(
            VerticalItemDecoration(this, object : DecorationCallback {
                override fun getGroupTag(position: Int): Long {
                    return cityBeans[position].tag[0].code.toLong()
                }

                override fun getGroupFirstLetter(position: Int): String {
                    return cityBeans[position].tag
                }
            })
        )
        binding.cityRecyclerView.adapter = cityAdapter
        binding.cityRecyclerView.setOnScrollChangeListener { _, _, _, _, _ -> }
        cityAdapter.setOnItemClickedListener(object :
            NormalRecyclerAdapter.OnItemClickedListener<CityModel> {
            override fun onItemClicked(position: Int, t: CityModel) {
                t.city.show(this@SlideBarActivity)
            }
        })
    }

    /**
     * 将城市整理成分组数据
     */
    private fun obtainCityData(): List<CityModel> {
        //先将数据按照字母排序
        val comparator: Comparator<Any> = Collator.getInstance(Locale.CHINA)
        Collections.sort(CITY, comparator)
        //格式化数据
        val cityBeans: MutableList<CityModel> = ArrayList<CityModel>()
        for (city in CITY) {
            val cityBean = CityModel()
            cityBean.city = city
            val firstLetter = StringHelper.obtainHanYuPinyin(city)
            cityBean.tag = firstLetter
            cityBeans.add(cityBean)
        }
        Log.d(kTag, "obtainCityData: " + Gson().toJson(cityBeans))
        return cityBeans
    }

    override fun initEvent() {
        val layoutInflater: LayoutInflater = LayoutInflater.from(this)
        val rootView: View = layoutInflater.inflate(R.layout.activity_slide, null)
        val contentView: View = layoutInflater.inflate(R.layout.layout_popup, null)
        val popupWindow = PopupWindow(
            contentView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            false
        )
        popupWindow.contentView = contentView
        val letterView: TextView = contentView.findViewById(R.id.letterView)
        val countDownTimer: CountDownTimer = object : CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                popupWindow.dismiss()
            }
        }
        binding.slideBarView.setData(CITY)
        binding.slideBarView.setOnIndexChangeListener(object : SlideBarView.OnIndexChangeListener {
            override fun onIndexChange(letter: String) {
                letterView.text = letter
                popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0)
                countDownTimer.start()

                //根据滑动显示的字母索引到城市名字第一个汉字
                val letterIndex = binding.slideBarView.obtainFirstLetterIndex(letter)
                if (letterIndex != -1) {
                    binding.cityRecyclerView.smoothScrollToPosition(letterIndex)
                }
            }
        })
    }
}