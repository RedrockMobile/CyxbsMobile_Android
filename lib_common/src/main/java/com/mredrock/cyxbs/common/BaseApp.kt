package com.mredrock.cyxbs.common

import android.app.Application
import android.content.Context
import androidx.annotation.CallSuper

/**
 * Created By jay68 on 2018/8/7.
 */
abstract class BaseApp : Application() {
    companion object {
        lateinit var appContext: Context
            private set
    }

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}