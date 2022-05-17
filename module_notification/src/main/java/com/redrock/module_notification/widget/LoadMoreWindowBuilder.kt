package com.redrock.module_notification.widget

import android.content.Context
import android.view.Window
import androidx.annotation.LayoutRes
import com.mredrock.cyxbs.common.utils.extensions.dp2px

/**
 * Author by OkAndGreat
 * Date on 2022/5/17 23:04.
 * 使用DSL方式构建LoadMoreWindow
 */
fun buildLoadMoreWindow(init: (LoadMoreWindowBuilder.() -> Unit)): LoadMoreWindow {
    val builder = LoadMoreWindowBuilder()
    builder.init()
    return builder.build()
}

class LoadMoreWindowBuilder {
    lateinit var context: Context
    lateinit var window: Window

    @LayoutRes
    var layoutRes: Int = 0

    var Width: Int = context.dp2px(120.toFloat())
    var Height: Int = context.dp2px(120.toFloat())

    fun build(): LoadMoreWindow {
        return LoadMoreWindow(context, layoutRes, window, Width, Height)
    }
}