package com.example.mutidemo

import android.os.Bundle
import android.view.KeyEvent
import com.bumptech.glide.Glide
import com.example.mutidemo.model.BannerImageModel
import com.example.mutidemo.util.DemoConstant
import com.example.mutidemo.view.*
import com.igexin.sdk.PushManager
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.navigatePageTo
import com.pengxh.kt.lite.extensions.show
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.transformer.ScaleInTransformer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : KotlinBaseActivity() {

    private var clickTime: Long = 0
    private val timer by lazy { Timer() }
    private val itemNames = listOf(
        "侧边导航栏", "ZBar扫一扫", "上拉加载下拉刷新", "联系人侧边滑动控件",
        "自定义进度条", "拖拽地图选点", "音频录制与播放", "图片添加水印并压缩", "视频压缩",
        "WCJ02ToWGS84", "蓝牙相关", "可删减九宫格", "系统原生分享", "空气污染刻度盘", "人脸检测",
        "TCP客户端", "方向控制盘", "时间轴", "海康摄像头", "裁剪图片"
    )

    override fun setupTopBarLayout() {

    }

    override fun observeRequestState() {

    }

    override fun initLayoutView(): Int = R.layout.activity_main

    override fun initData(savedInstanceState: Bundle?) {
        //个推初始化
        PushManager.getInstance().initialize(this)
        //轮播图
        val banner = bannerView
                as Banner<BannerImageModel.DataBean, BannerImageAdapter<BannerImageModel.DataBean>>
        banner.apply {
            setBannerRound(15f)
            setAdapter(object : BannerImageAdapter<BannerImageModel.DataBean>(data) {
                override fun onBindView(
                    holder: BannerImageHolder,
                    data: BannerImageModel.DataBean,
                    position: Int,
                    size: Int
                ) {
                    Glide.with(holder.itemView).load(data.imageLink).into(holder.imageView)
                }
            })
            addPageTransformer(ScaleInTransformer())
            addBannerLifecycleObserver(this@MainActivity)
            indicator = CircleIndicator(context)
        }

//        SocketManager.get.connectNetty(DemoConstant.HOST, DemoConstant.TCP_PORT)
    }

    private val data: List<BannerImageModel.DataBean>
        get() {
            val list = ArrayList<BannerImageModel.DataBean>()
            for (i in 0..4) {
                val dataBean = BannerImageModel.DataBean()
                dataBean.imageTitle = "测试标题$i"
                dataBean.imageLink = DemoConstant.images[i]
                list.add(dataBean)
            }
            return list
        }

    override fun initEvent() {
        val adapter = object :
            NormalRecyclerAdapter<String>(R.layout.item_main_rv_g, itemNames.toMutableList()) {
            override fun convertView(viewHolder: ViewHolder, position: Int, item: String) {
                viewHolder.setText(R.id.itemTitleView, item)
            }
        }
        mainRecyclerView.adapter = adapter
        adapter.setOnItemClickedListener(object :
            NormalRecyclerAdapter.OnItemClickedListener<String> {
            override fun onItemClicked(position: Int, t: String) {
                when (position) {
                    0 -> navigatePageTo<SlideNavigationActivity>()
                    1 -> {}
                    2 -> navigatePageTo<RefreshAndLoadMoreActivity>()
                    3 -> navigatePageTo<SlideBarActivity>()
                    4 -> {}
                    5 -> navigatePageTo<DragMapActivity>()
                    6 -> navigatePageTo<RecodeAudioActivity>()
                    7 -> navigatePageTo<WaterMarkerActivity>()
                    8 -> navigatePageTo<VideoCompressActivity>()
                    9 -> {}
                    10 -> navigatePageTo<BluetoothActivity>()
                    11 -> navigatePageTo<GridViewActivity>()
                    12 -> {}
                    13 -> {}
                    14 -> navigatePageTo<FaceCollectionActivity>()
                    15 -> {
                        val sendBytes = byteArrayOf(
                            0xFF.toByte(),
                            0x01,
                            0x00,
                            0x95.toByte(),
                            0x00,
                            0x00,
                            0x96.toByte()
                        )
//                        timer.schedule(object : TimerTask() {
//                            override fun run() {
//                                SocketManager.get.sendData(sendBytes)
//                            }
//                        }, 0, 1000)
                    }
                    16 -> navigatePageTo<SteeringWheelActivity>()
                    17 -> navigatePageTo<TimeLineActivity>()
                    18 -> navigatePageTo<HikVisionActivity>()
                    19 -> navigatePageTo<CropPictureActivity>()
                }
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - clickTime > 2000) {
                "再按一次退出程序".show(this)
                clickTime = System.currentTimeMillis()
                true
            } else {
                super.onKeyDown(keyCode, event)
            }
        } else super.onKeyDown(keyCode, event)
    }
}