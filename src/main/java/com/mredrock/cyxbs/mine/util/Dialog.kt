package com.mredrock.cyxbs.mine.util

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.mredrock.cyxbs.mine.R

/**
 * Created by zia on 2018/8/21.
 */
fun Context.getLoadingDialog(title: String): MaterialDialog {
    return MaterialDialog.Builder(this)
            .title(title)
            .content("请稍候")
            .theme(Theme.LIGHT)
            .backgroundColor(resources.getColor(R.color.mine_white))
            .progress(true, 100)
            .cancelable(false)
            .build()
}