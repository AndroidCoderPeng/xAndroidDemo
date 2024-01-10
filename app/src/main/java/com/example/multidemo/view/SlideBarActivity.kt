package com.example.multidemo.view

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.multidemo.R
import com.example.multidemo.databinding.ActivitySlideBinding
import com.example.multidemo.model.CityModel
import com.example.multidemo.util.RecyclerStickDecoration
import com.example.multidemo.util.StringHelper
import com.google.gson.Gson
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.widget.SlideBarView
import java.text.Collator
import java.util.Collections
import java.util.Locale

class SlideBarActivity : KotlinBaseActivity<ActivitySlideBinding>() {

    private val kTag = "SlideBarActivity"
    private val context = this@SlideBarActivity
    private val stickDecoration by lazy { RecyclerStickDecoration() }
    private val cities = listOf(
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

    /**
     * 将城市整理成分组数据
     */
    private fun sortCity(): MutableList<CityModel> {
        //先将数据按照字母排序
        Collections.sort(cities, Collator.getInstance(Locale.CHINA))
        //格式化数据
        val cityBeans = ArrayList<CityModel>()
        for (city in cities) {
            val cityBean = CityModel()
            cityBean.city = city
            val firstLetter = StringHelper.obtainHanYuPinyin(city)
            cityBean.tag = firstLetter
            cityBeans.add(cityBean)
        }
        Log.d(kTag, "sortCity: " + Gson().toJson(cityBeans))
        return cityBeans
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        val cityBeans = sortCity()
        val cityAdapter = object : NormalRecyclerAdapter<CityModel>(
            R.layout.item_city_rv_l, cityBeans
        ) {
            override fun convertView(viewHolder: ViewHolder, position: Int, item: CityModel) {
                viewHolder.setText(R.id.cityName, item.city)
            }
        }
        val layoutManager = object : LinearLayoutManager(this) {
            override fun smoothScrollToPosition(
                recyclerView: RecyclerView, state: RecyclerView.State, position: Int
            ) {
                val scroller = stickDecoration.SmoothGroupTopScroller(recyclerView.context)
                scroller.targetPosition = position
                startSmoothScroll(scroller)
            }
        }
        binding.cityRecyclerView.layoutManager = layoutManager
        stickDecoration.setContext(context).setTopGap(30.dp2px(context)).setViewGroupListener(
            object : RecyclerStickDecoration.ViewGroupListener {
                override fun groupTag(position: Int): Long {
                    return cityBeans[position].tag[0].code.toLong()
                }

                override fun groupFirstLetter(position: Int): String {
                    return cityBeans[position].tag
                }
            }).build()
        binding.cityRecyclerView.addItemDecoration(stickDecoration)
        binding.slideBarView.attachToRecyclerView(binding.cityRecyclerView, cities.toMutableList())
        binding.cityRecyclerView.adapter = cityAdapter
        cityAdapter.setOnItemClickedListener(object :
            NormalRecyclerAdapter.OnItemClickedListener<CityModel> {
            override fun onItemClicked(position: Int, t: CityModel) {
                t.city.show(context)
            }
        })
    }

    override fun initEvent() {
        binding.slideBarView.setOnLetterIndexChangeListener(object :
            SlideBarView.OnLetterIndexChangeListener {
            override fun onLetterIndexChange(letter: String) {
                //根据滑动显示的字母索引到城市名字第一个汉字
                val letterPosition = binding.slideBarView.getLetterPosition(letter)
                if (letterPosition != -1) {
                    binding.cityRecyclerView.smoothScrollToPosition(letterPosition)
                }
            }
        })
    }
}