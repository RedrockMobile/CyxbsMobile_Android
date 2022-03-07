package com.mredrock.cyxbs.store.utils

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.BaseApp.Companion.appContext

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2021/8/27
 */
internal fun Int.dp2px(): Int {
    return (this.dp2pxF() + 0.5f).toInt()
}

internal fun Int.dp2pxF(): Float {
    return appContext.resources.displayMetrics.density * this
}

internal fun Float.dp2px(): Int {
    return (this.dp2pxF() + 0.5f).toInt()
}

internal fun Float.dp2pxF(): Float {
    return appContext.resources.displayMetrics.density * this
}

/**
 * 写个 2 是为了与 context 中的 getColor() 同名方法区分
 */
internal fun getColor2(@ColorRes colorId: Int): Int {
    return ContextCompat.getColor(appContext, colorId)
}