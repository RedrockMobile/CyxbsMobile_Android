package com.mredrock.cyxbs.discover.grades.utils.extension

import com.mredrock.cyxbs.common.BaseApp

/**
 * Created by roger on 2020/2/12
 */
fun dp2px(value: Int): Int {
    val v = BaseApp.appContext.resources.displayMetrics.density
    return (v * value + 0.5f).toInt()
}