package com.mredrock.cyxbs.volunteer.fragment

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER_FEED
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.service.account.IUserStateService
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.common.utils.extensions.runOnUiThread
import com.mredrock.cyxbs.volunteer.viewmodel.DiscoverVolunteerFeedViewModel
import com.mredrock.cyxbs.volunteer.R
import com.mredrock.cyxbs.volunteer.adapter.VolunteerFeedAdapter
import com.mredrock.cyxbs.volunteer.adapter.VolunteerFeedUnbindAdapter
import com.mredrock.cyxbs.volunteer.widget.EncryptPassword
import com.mredrock.cyxbs.volunteer.widget.VolunteerTimeSP


@Route(path = DISCOVER_VOLUNTEER_FEED)
class DiscoverVolunteerFeedFragment : BaseFeedFragment<DiscoverVolunteerFeedViewModel>() {


    override val viewModelClass: Class<DiscoverVolunteerFeedViewModel> = DiscoverVolunteerFeedViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        setOnClickListener {
            context?.doIfLogin(getString(R.string.volunteer_service_inquire_string)) {
                ARouter.getInstance().build(DISCOVER_VOLUNTEER).navigation()
            }
        }
    }

    override var hasTopSplitLine = true
    override fun onRefresh() {
        val volunteerSP = VolunteerTimeSP(activity as Activity)
        val uid = volunteerSP.volunteerUid

        if (!(uid == "404" || volunteerSP.volunteerAccount == "404" ||
                        volunteerSP.volunteerPassword == "404")) {

            viewModel.loadVolunteerTime(EncryptPassword.encrypt(uid))
        } else {
            return
        }
    }


}
