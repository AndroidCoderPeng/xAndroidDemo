package com.example.multidemo.extensions

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Path
import android.graphics.PathMeasure
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.graphics.drawable.toDrawable
import com.pengxh.kt.lite.extensions.dp2px

/**
 * @param  shopCarView 购物车图标
 * @param  rootView 根布局
 * @param  bitmap 商品图片
 * @param  imageWidth 商品图片宽度
 * @param  imageHeight 商品图片高度
 * @param  animationTime 动画时长
 * @param  scale 物品移动过程最后到购物车里的缩放比例
 * @param  onStart 动画开始回调
 * @param  onEnd 动画结束回调
 * */
fun ImageView.showAnimation(
    shopCarView: View,
    rootView: ViewGroup,
    bitmap: Bitmap,
    imageWidth: Int = 80,
    imageHeight: Int = 80,
    animationTime: Long = 800,
    scale: Float = 0.25f,
    onStart: () -> Unit,
    onEnd: (View) -> Unit
) {
    val context = rootView.context

    // 获取坐标
    val startLocation = IntArray(2)
    val endLocation = IntArray(2)
    this.getLocationInWindow(startLocation)
    shopCarView.getLocationInWindow(endLocation)
    val startX = startLocation[0].toFloat()
    val startY = startLocation[1].toFloat()
    val endX = endLocation[0].toFloat()
    val endY = endLocation[1].toFloat()

    // 动态创建动画视图
    val animatingView = ImageView(context).apply {
        setImageDrawable(bitmap.toDrawable(resources))
        pivotX = width / 2f // 设置中心点为图片中心
        pivotY = height / 2f // 设置中心点为图片中心
        layoutParams = RelativeLayout.LayoutParams(
            imageWidth.dp2px(context), imageHeight.dp2px(context)
        ) // 设置动画视图的宽高
        x = startX
        y = startY
    }
    rootView.addView(animatingView)

    // 生成路径
    val offset = (endY - startY) * 0.5f
    val path = Path().apply {
        moveTo(startX, startY)
        //二阶贝塞尔曲线
        val controlX = (startX + endX) / 2 // 中点X坐标
        val controlY = startY - offset     // 控制点Y坐标（向上偏移）
        quadTo(controlX, controlY, endX, endY)
    }
    val pathMeasure = PathMeasure(path, false)
    val point = FloatArray(2)
    val totalLength = pathMeasure.length

    //动画逻辑
    ValueAnimator.ofFloat(0f, pathMeasure.length).apply {
        addUpdateListener { animation ->
            val distance = animation.animatedValue as Float
            pathMeasure.getPosTan(distance, point, null)
            animatingView.x = point[0]
            animatingView.y = point[1]

            // 计算当前缩放比例（线性插值）
            val progress = distance / totalLength
            animatingView.scaleX = 1f + (scale - 1f) * progress
            animatingView.scaleY = animatingView.scaleX
        }

        addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                onStart()
            }

            override fun onAnimationEnd(animation: Animator) {
                onEnd(animatingView)
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
        duration = animationTime
        start()
    }
}