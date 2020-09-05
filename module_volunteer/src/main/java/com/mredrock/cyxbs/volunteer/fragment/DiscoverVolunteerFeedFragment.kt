package com.mredrock.cyxbs.volunteer.fragment

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER_FEED
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER_RECORD
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.service.account.IUserStateService
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.common.utils.extensions.runOnUiThread
import com.mredrock.cyxbs.volunteer.R
import com.mredrock.cyxbs.volunteer.adapter.VolunteerFeedAdapter
import com.mredrock.cyxbs.volunteer.adapter.VolunteerFeedUnbindAdapter
import com.mredrock.cyxbs.volunteer.event.VolunteerLoginEvent
import com.mredrock.cyxbs.volunteer.viewmodel.DiscoverVolunteerFeedViewModel
import com.mredrock.cyxbs.volunteer.widget.EncryptPassword
import com.mredrock.cyxbs.volunteer.widget.VolunteerTimeSP
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@Route(path = DISCOVER_VOLUNTEER_FEED)
class DiscoverVolunteerFeedFragment : BaseFeedFragment<DiscoverVolunteerFeedViewModel>(), EventBusLifecycleSubscriber {


    override val viewModelClass: Class<DiscoverVolunteerFeedViewModel> = DiscoverVolunteerFeedViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()) {
            setAdapter(VolunteerFeedUnbindAdapter())
        }
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
                        ARouter.getInstance().build(DISCOVER_VOLUNTEER_RECORD).navigation()
                    } else {
                        ARouter.getInstance().build(DISCOVER_VOLUNTEER).navigation()
                    }
                }
            }
        }
    }

    override var hasTopSplitLine = true
    override fun onRefresh() {
        //首先判断是否登录
        if (!ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()) {
            return
        }

        val volunteerSP = VolunteerTimeSP(context ?: return)
        val uid = volunteerSP.volunteerUid
        //再判断是否存有sp，因为有可能重新登录后vm中数据不为空
        if (!volunteerSP.isBind()) return

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
        viewModel.loadVolunteerTime(EncryptPassword.encrypt(uid))
        val adapter = getAdapter()
        if (adapter is VolunteerFeedUnbindAdapter) {
            adapter.refresh("查询中...")
        }

    }

    //用于首次登录志愿者后的数据后传给发现首页刷新
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun getVolunteerTime(volunteerLoginEvent: VolunteerLoginEvent) {
        if (viewModel.volunteerData.value == null && !viewModel.isQuerying) {
            viewModel.volunteerData.value = volunteerLoginEvent.volunteerTime
        }
    }


}
