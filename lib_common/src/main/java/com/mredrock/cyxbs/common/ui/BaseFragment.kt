package com.mredrock.cyxbs.common.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.account.IAccountService
import com.mredrock.cyxbs.common.mark.ActionLoginStatusSubscriber
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.utils.LogUtils
import com.umeng.analytics.MobclickAgent
import org.greenrobot.eventbus.EventBus

/**
 * Created By jay68 on 2018/8/9.
 */
open class BaseFragment : Fragment() {
    //是否打开友盟统计，当此Fragment只是其他fragment容器时，应当考虑关闭统计
    protected open val openStatistics = true

    /**
     * 这里可以开启生命周期的Log，你可以重写这个值并给值为true，
     * 也可以直接赋值为true（赋值的话请在init{}里面赋值或者在super.onCreate(savedInstanceState)调用之前赋值）
     */
    open protected var isOpenLifeCycleLog = false

    //当然，你要定义自己的TAG方便在Log里面找也可以重写这个
    open protected var TAG: String = this::class.java.simpleName

    // 只做本地封装使用
    private var baseBundle: Bundle? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseBundle = savedInstanceState
        if (this is EventBusLifecycleSubscriber) EventBus.getDefault().register(this)
        val verifyService = ServiceManager.getService(IAccountService::class.java).getVerifyService()
        if (this is ActionLoginStatusSubscriber) {
            if (verifyService.isLogin()) initOnLoginMode(baseBundle)
            if (verifyService.isTouristMode()) initOnTouristMode(baseBundle)
            if (verifyService.isLogin()||verifyService.isTouristMode()) initPage(verifyService.isLogin(),baseBundle)
        }
    }

    override fun onPause() {
        super.onPause()
        lifeCycleLog("onPause")
        //在退出当前页面时需要手动调用setUserVisibleHint方法
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifeCycleLog("onDestroyView")
        if (this is EventBusLifecycleSubscriber && EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this)
        val verifyService = ServiceManager.getService(IAccountService::class.java).getVerifyService()
        if (this is ActionLoginStatusSubscriber) {
            if (verifyService.isLogin()) destroyOnLoginMode()
            if (verifyService.isTouristMode()) destroyOnTouristMode()
            if (verifyService.isLogin()||verifyService.isTouristMode()) destroyPage(verifyService.isLogin())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifeCycleLog("onCreate")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifeCycleLog("onAttach")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        lifeCycleLog("onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifeCycleLog("onActivityCreated")
    }

    override fun onStart() {
        super.onStart()
        if (openStatistics) {
            MobclickAgent.onPageStart(javaClass.name)
            LogUtils.d("UMStat", javaClass.name + " started")
        }
        lifeCycleLog("onStart")
    }

    override fun onResume() {
        super.onResume()
        lifeCycleLog("onResume")
    }

    override fun onStop() {
        super.onStop()
        if (openStatistics) {
            MobclickAgent.onPageEnd(javaClass.name)
            LogUtils.d("UMStat", javaClass.name + " paused")
        }
        lifeCycleLog("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        lifeCycleLog("onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        lifeCycleLog("onDetach")
    }

    fun lifeCycleLog(message: String) {
        if (isOpenLifeCycleLog) {
            LogUtils.d(TAG, "${this::class.java.simpleName}\$\$${message}")
        }
    }

}