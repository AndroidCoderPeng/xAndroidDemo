package com.example.multidemo.view

import android.animation.ObjectAnimator
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import com.example.multidemo.R
import com.example.multidemo.databinding.ActivityAddProductAnimationBinding
import com.example.multidemo.extensions.initImmersionBar
import com.example.multidemo.extensions.showAnimation
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.divider.RecyclerViewItemOffsets
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.show
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenu
import com.yanzhenjie.recyclerview.SwipeMenuBridge
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem

class AddProductAnimationActivity : KotlinBaseActivity<ActivityAddProductAnimationBinding>() {

    private val kTag = "AddProductAnimationActivity"
    private val context = this
    private val marginOffsets by lazy { 7.dp2px(this) }
    private var count = 0

    override fun initViewBinding(): ActivityAddProductAnimationBinding {
        return ActivityAddProductAnimationBinding.inflate(layoutInflater)
    }

    override fun setupTopBarLayout() {
        binding.rootView.initImmersionBar(this, true, R.color.backgroundColor)
    }

    class Product(val image: Int, val name: String, val price: String)

    override fun initOnCreate(savedInstanceState: Bundle?) {
        val list = ArrayList<Product>().apply {
            add(Product(R.mipmap.launcher_logo, "全国直邮|红薯", "￥20.0"))
            add(Product(R.mipmap.launcher_logo, "仅发北京|阳光玫瑰", "￥39.9"))
            add(Product(R.mipmap.launcher_logo, "仅发北京|麒麟瓜", "￥19.9"))
            add(Product(R.mipmap.launcher_logo, "全国直邮|菜籽油", "￥69.8"))
            add(Product(R.mipmap.launcher_logo, "仅发北京|老面馒头", "￥9.8"))
            add(Product(R.mipmap.launcher_logo, "仅发二院|卤牛肉", "￥70.0"))
            add(Product(R.mipmap.launcher_logo, "双汇玉米热狗肠40g*8根", "￥9.78"))
            add(Product(R.mipmap.launcher_logo, "羊角蜜甜瓜单粒约500g", "￥9.9"))
            add(Product(R.mipmap.launcher_logo, "【无抗】鲜猪通脊肉约200g", "￥12.6"))
        }
        binding.recyclerView.apply {
            setSwipeMenuCreator(swipeMenuCreator)
            setOnItemMenuClickListener(onItemMenuClickListener)
            adapter = object : NormalRecyclerAdapter<Product>(R.layout.item_product_l, list) {
                override fun convertView(viewHolder: ViewHolder, position: Int, item: Product) {
                    val imageView = viewHolder.getView<ImageView>(R.id.imageView)
                    imageView.setImageResource(item.image)
                    viewHolder.setText(R.id.nameView, item.name)
                        .setText(R.id.priceView, item.price)
                        .setOnClickListener(R.id.addProductButton) {
                            imageView.showAnimation(
                                binding.shopCarView,
                                binding.rootView,
                                BitmapFactory.decodeResource(resources, item.image),
                                onStart = {
                                    count++
                                    binding.cardView.visibility = View.VISIBLE
                                    count.run {
                                        if (this >= 100) {
                                            binding.productCountView.text = "99+"
                                        } else {
                                            binding.productCountView.text = "$this"
                                        }
                                    }
                                },
                                onEnd = {
                                    binding.rootView.removeView(it)
                                    startShopCarAnimation()
                                }
                            )
                        }
                }
            }
            addItemDecoration(
                RecyclerViewItemOffsets(
                    marginOffsets, marginOffsets shr 1, marginOffsets, marginOffsets shr 1
                )
            )
        }
    }

    private val swipeMenuCreator = object : SwipeMenuCreator {
        override fun onCreateMenu(leftMenu: SwipeMenu?, rightMenu: SwipeMenu?, position: Int) {
            val deleteButton = SwipeMenuItem(context).apply {
                width = 100.dp2px(context)
                height = ViewGroup.LayoutParams.MATCH_PARENT
                setBackgroundColor(Color.RED)
                text = "删除"
                textSize = 16
                setImage(R.drawable.ic_delete)
                setTextColor(Color.WHITE)
            }
            rightMenu?.addMenuItem(deleteButton)
        }
    }

    private val onItemMenuClickListener = object : OnItemMenuClickListener {
        override fun onItemClick(menuBridge: SwipeMenuBridge?, adapterPosition: Int) {
            menuBridge?.closeMenu()
        }
    }

    private var isAnimating = false

    private fun startShopCarAnimation() {
        if (isAnimating) return
        isAnimating = true

        // 垂直弹跳动画
        val translateAnimator = ObjectAnimator.ofFloat(
            binding.shopCarView, View.TRANSLATION_Y, 0f, -20f, 0f
        ).apply {
            duration = 500
            interpolator = BounceInterpolator()
        }

        translateAnimator.start()
        translateAnimator.doOnEnd {
            isAnimating = false
            "加入购物车成功".show(this)
        }
    }

    override fun observeRequestState() {

    }

    override fun initEvent() {

    }
}