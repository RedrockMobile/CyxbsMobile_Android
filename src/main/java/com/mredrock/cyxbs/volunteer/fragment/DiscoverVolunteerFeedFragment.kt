package com.mredrock.cyxbs.volunteer.fragment

import android.app.Activity
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER_FEED
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import com.mredrock.cyxbs.volunteer.DiscoverVolunteerFeedViewModel
import com.mredrock.cyxbs.volunteer.adapter.VolunteerFeedAdapter
import com.mredrock.cyxbs.volunteer.adapter.VolunteerFeedUnbindAdapter
import com.mredrock.cyxbs.volunteer.widget.EncryptPassword
import com.mredrock.cyxbs.volunteer.widget.VolunteerTimeSP


@Route(path = DISCOVER_VOLUNTEER_FEED)
class DiscoverVolunteerFeedFragment : BaseFeedFragment<DiscoverVolunteerFeedViewModel>() {


    override val viewModelClass: Class<DiscoverVolunteerFeedViewModel> = DiscoverVolunteerFeedViewModel::class.java

    override fun onResume() {
        super.onResume()

        setTitle("志愿服务")
        val volunteerSP = VolunteerTimeSP(activity as Activity)
        val uid = volunteerSP.volunteerUid
        setAdapter(VolunteerFeedUnbindAdapter())
        if (!(uid == "404" || volunteerSP.volunteerAccount == "404" ||
                        volunteerSP.volunteerPassword == "404")) {

            viewModel.loadVolunteerTime(EncryptPassword.encrypt(uid))
        }
        init()
    }

    private fun init() {

        viewModel.volunteerData.observe { volunteerTime ->

            volunteerTime?.let {
                setAdapter(VolunteerFeedAdapter(it))
            }

        }
        setOnClickListener { ARouter.getInstance().build(DISCOVER_VOLUNTEER).navigation() }
    }


}
