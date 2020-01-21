package com.mredrock.cyxbs.volunteer.fragment

import android.app.Activity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER_FEED
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.volunteer.DiscoverVolunteerFeedViewModel
import com.mredrock.cyxbs.volunteer.R
import com.mredrock.cyxbs.volunteer.widget.EncryptPassword
import com.mredrock.cyxbs.volunteer.widget.VolunteerTimeSP
import kotlinx.android.synthetic.main.fragment_discover_volunteer_feed.*
import org.jetbrains.anko.sp

@Route(path = DISCOVER_VOLUNTEER_FEED)
class DiscoverVolunteerFeedFragment : BaseViewModelFragment<DiscoverVolunteerFeedViewModel>() {
    // TODO: Rename and change types of parameters


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_discover_volunteer_feed, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        init()
        super.onActivityCreated(savedInstanceState)
    }

    override val viewModelClass: Class<DiscoverVolunteerFeedViewModel> = DiscoverVolunteerFeedViewModel::class.java

    override fun onResume() {
        super.onResume()

        val volunteerSP = VolunteerTimeSP(activity as Activity)
        val uid = volunteerSP.volunteerUid
        if (uid == "404" || volunteerSP.volunteerAccount == "404" ||
                volunteerSP.volunteerPassword == "404") {
            tv_volunteer_feed_activity_time.text = "还未绑定账号"
        } else {
            viewModel.loadVolunteerTime(EncryptPassword.encrypt(uid))
        }
    }

    private fun init() {
        val context = context ?: return
        tv_volunteer_feed_total_time.text = SpannableStringBuilder("0时")
                .apply {
                    setSpan(AbsoluteSizeSpan(context.sp(36)), 0, this.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    setSpan(AbsoluteSizeSpan(context.sp(8)), this.length - 1, this.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }


        viewModel.volunteerData.observe { volunteerTime ->

            volunteerTime?.let {

                tv_volunteer_feed_total_time.text = SpannableStringBuilder(it.hours?.toInt().toString().plus("时"))
                        .apply {
                            setSpan(AbsoluteSizeSpan(context.sp(36)), 0, this.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                            setSpan(AbsoluteSizeSpan(context.sp(8)), this.length - 1, this.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                        }
                if(it.hours==0.0){
                    tv_volunteer_feed_activity_time.text = "还没有志愿时长"
                    return@observe
                }
                tv_volunteer_feed_activity_name.text = it.record?.get(0)?.title
                tv_volunteer_feed_activity_date.text = it.record?.get(0)?.start_time?.substring(0, 10)
                tv_volunteer_feed_activity_time.text = it.record?.get(0)?.hours.plus("小时")
                tv_volunteer_feed_activity_address.text = it.record?.get(0)?.server_group

            }

        }
        cl_volunteer_feed.setOnClickListener {
            ARouter.getInstance().build(DISCOVER_VOLUNTEER).navigation()
        }
    }

}
