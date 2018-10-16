package com.mredrock.cyxbs.common.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.mredrock.cyxbs.common.event.LoginStateChangeEvent
import com.umeng.analytics.MobclickAgent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created By jay68 on 2018/8/9.
 */
open class BaseFragment : Fragment() {
    //是否打开友盟统计，当此Fragment只是其他fragment容器时，应当考虑关闭统计
    protected open val openStatistics = true

    override fun onResume() {
        super.onResume()
        if (openStatistics) {
            MobclickAgent.onPageStart(javaClass.name)
        }
    }

    override fun onPause() {
        super.onPause()
        if (openStatistics) {
            MobclickAgent.onPageEnd(javaClass.name)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onLoginStateChangeEvent(event: LoginStateChangeEvent) {
    }
}