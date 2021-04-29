package com.mredrock.cyxbs.volunteer.fragment

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.account.IUserStateService
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER_FEED
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER_RECORD
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.common.utils.extensions.runOnUiThread
import com.mredrock.cyxbs.volunteer.R
import com.mredrock.cyxbs.volunteer.adapter.VolunteerFeedAdapter
import com.mredrock.cyxbs.volunteer.adapter.VolunteerFeedUnbindAdapter
import com.mredrock.cyxbs.volunteer.event.VolunteerLoginEvent
import com.mredrock.cyxbs.volunteer.event.VolunteerLogoutEvent
import com.mredrock.cyxbs.volunteer.viewmodel.DiscoverVolunteerFeedViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@Route(path = DISCOVER_VOLUNTEER_FEED)
class DiscoverVolunteerFeedFragment : BaseFeedFragment<DiscoverVolunteerFeedViewModel>(), EventBusLifecycleSubscriber {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //对登录状态判断
        if (ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()) {
            setAdapter(VolunteerFeedUnbindAdapter())
        }
        //设置监听，用于游客->登录
        ServiceManager.getService(IAccountService::class.java).getVerifyService().addOnStateChangedListener {
            if (it == IUserStateService.UserState.LOGIN) {
                context?.runOnUiThread {
                    setAdapter(VolunteerFeedUnbindAdapter())
                }
            }
        }
        init()
    }


    private fun init() {
        setTitle(getString(R.string.volunteer_service_inquire_string))
        setAdapter(VolunteerFeedUnbindAdapter())

        viewModel.volunteerData.observe { volunteerTime ->
            //当data变化了，那说明用户主动再登录了
            //改变了，刷新
            volunteerTime?.let {
                val adapter = getAdapter()
                if (adapter is VolunteerFeedAdapter) {
                    adapter.refresh(it)
                } else {
                    setAdapter(VolunteerFeedAdapter(it))
                }

            }
        }
        viewModel.loadFailed.observe {
            it ?: return@observe
            val adapter = getAdapter()
            if (it && adapter is VolunteerFeedUnbindAdapter) {
                adapter.refresh("查询失败，请稍后再试")
            }
        }
        setOnClickListener {
            context?.doIfLogin(getString(R.string.volunteer_service_inquire_string)) {
                if (!viewModel.isQuerying) {
                    if (viewModel.volunteerData.value != null) {
                        EventBus.getDefault().postSticky(VolunteerLoginEvent(viewModel.volunteerData.value!!))
                        ARouter.getInstance().build(DISCOVER_VOLUNTEER_RECORD)
                                .withString("volunteerTime", Gson().toJson(viewModel.volunteerData.value)).navigation()
                    } else {
                        ARouter.getInstance().build(DISCOVER_VOLUNTEER).navigation()
                    }
                }
            }
        }
    }

    override var hasTopSplitLine = true

    //onResume
    override fun onRefresh() {
        //首先判断是否登录，没登录，那直接return
        if (!ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()) {
            return
        }
        //再判断vm是否有数据，有数据直接加载，再return
        if (viewModel.volunteerData.value != null) {
            viewModel.volunteerData.value?.let {
                val adapter = getAdapter()
                if (adapter is VolunteerFeedAdapter) {
                    adapter.refresh(it)
                } else {
                    setAdapter(VolunteerFeedAdapter(it))
                }

            }
            return
        }
        if (viewModel.requestUnBind) {
            val adapter = getAdapter()
            if (adapter is VolunteerFeedUnbindAdapter) {
                adapter.refresh(getString(R.string.volunteer_unbind))
            } else {
                setAdapter(VolunteerFeedUnbindAdapter())
            }
            return
        }
        //再判断是否绑定，绑定，就请求。能到这里，说明vm已经没有数据了
        if (!viewModel.isBind) {
            viewModel.loadVolunteerTime()
            val adapter = getAdapter()
            if (adapter is VolunteerFeedUnbindAdapter) {
                adapter.refresh("查询中...")
            }
        }
    }

    //用于首次登录志愿者后的数据后传给发现首页刷新，把数据给vm
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun getVolunteerTime(volunteerLoginEvent: VolunteerLoginEvent) {
        if (viewModel.volunteerData.value == null && !viewModel.isQuerying) {
            viewModel.volunteerData.value = volunteerLoginEvent.volunteerTime
        }
    }

    //用于处理VolunteerRecordActivity的取消绑定事件，清除vm数据，并重设adapter
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun unbindVolunteer(volunteerLogoutEvent: VolunteerLogoutEvent) {
        viewModel.unbind()
    }


}
