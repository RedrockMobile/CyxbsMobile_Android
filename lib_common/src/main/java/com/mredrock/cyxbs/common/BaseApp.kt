package com.mredrock.cyxbs.common

import android.content.Context
import com.mredrock.cyxbs.lib.utils.extensions.appContext as appContext2

/**
 * Created By jay68 on 2018/8/7.
 */
class BaseApp {
    companion object {
        val appContext: Context
            get() = appContext2
    }
}