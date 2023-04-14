package com.example.mutidemo

import android.graphics.Color
import android.view.KeyEvent
import cn.bertsir.zbar.QrConfig
import cn.bertsir.zbar.QrManager
import cn.bertsir.zbar.view.ScanLineView
import com.bumptech.glide.Glide
import com.example.mutidemo.model.BannerImageModel
import com.example.mutidemo.util.DemoConstant
import com.example.mutidemo.util.netty.SocketManager
import com.example.mutidemo.view.*
import com.igexin.sdk.PushManager
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.convertColor
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
    private val itemNames = listOf(
        "顶/底部导航栏", "ZBar扫一扫", "上拉加载下拉刷新", "联系人侧边滑动控件", "OCR识别银行卡",
        "自定义进度条", "拖拽地图选点", "音频录制与播放", "图片添加水印并压缩", "视频压缩",
        "WCJ02ToWGS84", "蓝牙相关", "可删减九宫格", "系统原生分享", "空气污染刻度盘", "人脸检测",
        "TCP客户端", "方向控制盘", "时间轴", "视频区域划分"
    )

    override fun setupTopBarLayout() {

    }

    override fun observeRequestState() {

    }

    override fun initLayoutView(): Int = R.layout.activity_main

    override fun initData() {
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

        //TODO 初始化Netty
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
        val adapter = object : NormalRecyclerAdapter<String>(R.layout.item_main_rv_g, itemNames) {
            override fun convertView(viewHolder: ViewHolder, position: Int, item: String) {
                viewHolder.setText(R.id.itemTitleView, item)
            }
        }
        mainRecyclerView.adapter = adapter
        adapter.setOnItemClickedListener(object :
            NormalRecyclerAdapter.OnItemClickedListener<String> {
            override fun onItemClicked(position: Int, t: String) {
                when (position) {
                    0 -> navigatePageTo<NavigationActivity>()
                    1 -> startScannerActivity()
                    2 -> navigatePageTo<RefreshAndLoadMoreActivity>()
                    3 -> navigatePageTo<SlideBarActivity>()
                    4 -> navigatePageTo<OcrNumberActivity>()
                    5 -> navigatePageTo<ProcessBarActivity>()
                    6 -> navigatePageTo<DragMapActivity>()
                    7 -> navigatePageTo<RecodeAudioActivity>()
                    8 -> navigatePageTo<WaterMarkerActivity>()
                    9 -> navigatePageTo<VideoCompressActivity>()
                    10 -> navigatePageTo<GCJ02ToWGS84Activity>()
                    11 -> navigatePageTo<BluetoothActivity>()
                    12 -> navigatePageTo<GridViewActivity>()
                    13 -> navigatePageTo<OriginalShareActivity>()
                    14 -> navigatePageTo<AirDashBoardActivity>()
                    15 -> navigatePageTo<FaceCollectionActivity>()
                    16 -> {
                        val sendBytes = byteArrayOf(
                            0xFF.toByte(),
                            0x01,
                            0x00,
                            0x95.toByte(),
                            0x00,
                            0x00,
                            0x96.toByte()
                        )
                        Timer().schedule(object : TimerTask() {
                            override fun run() {
                                SocketManager.get.sendData(sendBytes)
                            }
                        }, 0, 1000)
                    }
                    17 -> navigatePageTo<SteeringWheelActivity>()
                    18 -> navigatePageTo<TimeLineActivity>()
                    19 -> navigatePageTo<VideoRegionActivity>()
                }
            }
        })
    }

    private fun startScannerActivity() {
        val qrConfig = QrConfig.Builder()
            .setShowLight(true) //显示手电筒按钮
            .setShowTitle(false) //显示Title
            .setCornerColor(R.color.mainColor.convertColor(this)) //设置扫描框颜色
            .setLineColor(R.color.mainColor.convertColor(this)) //设置扫描线颜色
            .setLineSpeed(QrConfig.LINE_MEDIUM) //设置扫描线速度
            .setScanType(QrConfig.TYPE_QRCODE) //设置扫码类型（二维码，条形码，全部，自定义，默认为二维码）
            .setDesText("扫一扫") //扫描框下文字
            .setShowDes(true) //是否显示扫描框下面文字
            .setPlaySound(true) //是否扫描成功后bi~的声音
            .setDingPath(R.raw.qrcode) //设置提示音(不设置为默认的Ding~)
            .setIsOnlyCenter(true) //是否只识别框中内容(默认为全屏识别)
            .setTitleBackgroudColor(Color.BLACK) //设置状态栏颜色
            .setTitleTextColor(Color.WHITE) //设置Title文字颜色
            .setScreenOrientation(QrConfig.SCREEN_PORTRAIT) //设置屏幕方式
            .setScanLineStyle(ScanLineView.style_hybrid) //扫描线样式
            .setShowVibrator(true) //是否震动提醒
            .create()
        QrManager.getInstance().init(qrConfig).startScan(this) { result ->
            runOnUiThread {
                "扫码结果: " + result.content.show(this@MainActivity)
            }
        }
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