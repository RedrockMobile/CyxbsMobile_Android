package com.mredrock.cyxbs.common.ui

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.MAIN_REQUEST_LOGIN_DIALOG
import com.mredrock.cyxbs.common.config.MINE_CHECK_IN

@Interceptor(priority = 1)
class TouristInterceptor : IInterceptor {
    private var context: Context? = null

    override fun process(postcard: Postcard?, callback: InterceptorCallback?) {
        postcard ?: return
        callback ?: return
        when (postcard.path) {
            MINE_CHECK_IN -> {
                callback.onInterrupt(null)
                ARouter.getInstance().build(MAIN_REQUEST_LOGIN_DIALOG).navigation()
            }
            else -> callback.onContinue(postcard)
        }
    }

    override fun init(context: Context?) {
        this.context = context
    }
}