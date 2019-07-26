package com.mredrock.cyxbs.common.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.mredrock.cyxbs.common.event.LoginStateChangeEvent
import com.mredrock.cyxbs.common.utils.LogUtils
import com.umeng.analytics.MobclickAgent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created By jay68 on 2018/8/9.
 */
open class BaseFragment : androidx.fragment.app.Fragment() {
    //是否打开友盟统计，当此Fragment只是其他fragment容器时，应当考虑关闭统计
    protected open val openStatistics = true

    private var isStarted = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        //在退出当前页面时需要手动调用setUserVisibleHint方法
        userVisibleHint = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (!openStatistics) {
            return
        }
        if (!isStarted && isVisibleToUser) {
            isStarted = true
            MobclickAgent.onPageStart(javaClass.name)
            LogUtils.d("UMStat",  javaClass.name + " started")
        } else if (isStarted) {
            isStarted = false
            MobclickAgent.onPageEnd(javaClass.name)
            LogUtils.d("UMStat",  javaClass.name + " paused")
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onLoginStateChangeEvent(event: LoginStateChangeEvent) {
    }
}