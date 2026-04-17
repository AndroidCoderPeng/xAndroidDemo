package com.example.android.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * 根据Android自定义View的标准实践，即使没有自定义属性（attr），也应该实现4个构造函数
 * 但是可以使用Kotlin的 @JvmOverloads 注解简化
 *
 * | 构造函数 | 调用场景 |
 * |:----:|:----:|
 * | context | 代码中动态创建View |
 * | Context, AttributeSet? | XML布局中使用（最常见） |
 * | + defStyleAttr | 应用主题样式 |
 * | + defStyleRes | 特定样式资源 |
 *
 */
class CircularGradientStripView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {
}