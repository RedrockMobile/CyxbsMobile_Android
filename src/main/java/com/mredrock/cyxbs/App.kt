package com.mredrock.cyxbs

import android.content.Context
import com.mredrock.cyxbs.common.BaseApp
import com.taobao.sophix.SophixManager

/**
 * Created By jay68 on 2018/8/8.
 */
class App : BaseApp() {

    override fun onCreate() {
        super.onCreate()
        AppInitService.init(this)
        SophixManager.getInstance().queryAndLoadNewPatch()
    }
}