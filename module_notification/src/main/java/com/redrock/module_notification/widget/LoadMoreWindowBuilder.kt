package com.redrock.module_notification.widget

import android.content.Context
import android.view.Window
import androidx.annotation.LayoutRes
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import kotlin.properties.Delegates

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
    var context by Delegates.notNull<Context>()
    var window by Delegates.notNull<Window>()

    @LayoutRes
    var layoutRes: Int = 0

    var Width: Int = 0
    var Height: Int = 0

    fun build(): LoadMoreWindow {
        if (Width == 0) Width = context.dp2px(120.toFloat())
        if (Height == 0) Height = context.dp2px(120.toFloat())
        return LoadMoreWindow(context, layoutRes, window, Width, Height)
    }
}